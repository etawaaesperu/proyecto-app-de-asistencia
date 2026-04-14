package com.proyecto.app_de_asistencia.view.gerente

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.proyecto.app_de_asistencia.databinding.ActivityGerenteHomeBinding
import com.proyecto.app_de_asistencia.db.UsuarioDatabase
import com.proyecto.app_de_asistencia.retrofit.ClienteRetrofit
import com.proyecto.app_de_asistencia.retrofit.request.AsistenciaRequest
import com.proyecto.app_de_asistencia.retrofit.response.AsistenciaResponse
import com.proyecto.app_de_asistencia.util.AppMensaje
import com.proyecto.app_de_asistencia.util.Constantes
import com.proyecto.app_de_asistencia.util.TipoMensaje
import com.proyecto.app_de_asistencia.view.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class GerenteHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGerenteHomeBinding
    private val relojHandler = Handler(Looper.getMainLooper())
    private var idEmpleado: Long = 0L
    private var asistenciaHoy: AsistenciaResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGerenteHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cargarDatosUsuario()
        iniciarReloj()
        configurarNavegacion()
    }

    private fun cargarDatosUsuario() {
        UsuarioDatabase.getInstance(this).usuarioDao().obtener().observe(this) { usuario ->
            if (usuario != null) {
                binding.tvBienvenido.text = "Bienvenido, ${usuario.nombre}!"
                binding.tvRol.text = "Rol: Gerente"
                idEmpleado = usuario.idEmpleado
                consultarAsistenciaHoy()
            }
        }
    }

    private fun consultarAsistenciaHoy() {
        val fechaHoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        ClienteRetrofit.api.getAsistencias(fechaHoy).enqueue(object : Callback<List<AsistenciaResponse>> {
            override fun onResponse(call: Call<List<AsistenciaResponse>>, response: Response<List<AsistenciaResponse>>) {
                if (response.isSuccessful) {
                    asistenciaHoy = response.body()?.find { it.empleadoId == idEmpleado }
                    actualizarEstadoAsistencia()
                    if (asistenciaHoy == null) mostrarDialogoAsistenciaInicial()
                }
            }
            override fun onFailure(call: Call<List<AsistenciaResponse>>, t: Throwable) {
                mostrarDialogoAsistenciaInicial()
            }
        })
    }

    private fun mostrarDialogoAsistenciaInicial() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Marcar Asistencia")
            .setMessage("¿Desea marcar su asistencia ahora?")
            .setPositiveButton("Marcar Entrada") { _, _ -> marcarAsistencia() }
            .setNegativeButton("Después", null)
            .show()
    }

    private fun marcarAsistencia() {
        if (idEmpleado == 0L) return

        if (asistenciaHoy?.horaEntrada != null && asistenciaHoy?.horaSalida != null) {
            AppMensaje.enviarMensaje(binding.root, "Tu asistencia de hoy ya está completa", TipoMensaje.INFO)
            return
        }

        if (asistenciaHoy?.horaEntrada != null && asistenciaHoy?.horaSalida == null) {
            MaterialAlertDialogBuilder(this)
                .setTitle("Marcar Salida")
                .setMessage("¿Confirmas el registro de tu salida?")
                .setPositiveButton("Confirmar") { _, _ ->
                    val req = AsistenciaRequest(idEmpleado, asistenciaHoy!!.estado)
                    ClienteRetrofit.api.actualizarAsistencia(asistenciaHoy!!.id, req)
                        .enqueue(object : Callback<AsistenciaResponse> {
                            override fun onResponse(call: Call<AsistenciaResponse>, response: Response<AsistenciaResponse>) {
                                if (response.isSuccessful) {
                                    asistenciaHoy = response.body()
                                    AppMensaje.enviarMensaje(binding.root, "✓ Marcación exitosa — Salida registrada", TipoMensaje.SUCCESS)
                                    actualizarEstadoAsistencia()
                                }
                            }
                            override fun onFailure(call: Call<AsistenciaResponse>, t: Throwable) {
                                AppMensaje.enviarMensaje(binding.root, "Sin conexión", TipoMensaje.ERROR)
                            }
                        })
                }
                .setNegativeButton("Cancelar", null)
                .show()
            return
        }

        val cal      = Calendar.getInstance()
        val totalMin = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
        val inicioMin   = Constantes.HORA_INICIO * 60
        val finPuntual  = inicioMin + Constantes.MIN_FIN_PUNTUAL
        val finTardanza = Constantes.HORA_FIN_TARDANZA * 60

        val estado = when {
            totalMin in inicioMin..finPuntual         -> "ASISTENCIA"
            totalMin in (finPuntual + 1)..finTardanza -> "TARDANZA"
            else                                       -> "FALTA"
        }

        ClienteRetrofit.api.registrarAsistencia(AsistenciaRequest(idEmpleado, estado))
            .enqueue(object : Callback<AsistenciaResponse> {
                override fun onResponse(call: Call<AsistenciaResponse>, response: Response<AsistenciaResponse>) {
                    if (response.isSuccessful) {
                        asistenciaHoy = response.body()
                        val (msg, tipo) = when (estado) {
                            "ASISTENCIA" -> "✓ Marcación exitosa — Entrada registrada" to TipoMensaje.SUCCESS
                            "TARDANZA"   -> "⚠ Marcación exitosa — Registrado con tardanza" to TipoMensaje.WARNING
                            else         -> "Marcación registrada — Falta" to TipoMensaje.ERROR
                        }
                        AppMensaje.enviarMensaje(binding.root, msg, tipo)
                        actualizarEstadoAsistencia()
                    }
                }
                override fun onFailure(call: Call<AsistenciaResponse>, t: Throwable) {
                    AppMensaje.enviarMensaje(binding.root, "Sin conexión al servidor", TipoMensaje.ERROR)
                }
            })
    }

    // HU21 — estados claros con botón dinámico
    private fun actualizarEstadoAsistencia() {
        when {
            asistenciaHoy == null -> {
                binding.tvEstadoAsistencia.text = "Estado: Sin marcación"
                binding.btnMarcarAsistencia.text = "Marcar Entrada"
                binding.btnMarcarAsistencia.isEnabled = true
            }
            asistenciaHoy!!.horaSalida != null -> {
                binding.tvEstadoAsistencia.text =
                    "Estado: Asistencia completada ✓  |  " +
                    "${asistenciaHoy!!.horaEntrada} → ${asistenciaHoy!!.horaSalida}"
                binding.btnMarcarAsistencia.text = "Asistencia Completa"
                binding.btnMarcarAsistencia.isEnabled = false
            }
            asistenciaHoy!!.horaEntrada != null -> {
                binding.tvEstadoAsistencia.text =
                    "Estado: Entrada registrada — ${asistenciaHoy!!.horaEntrada}"
                binding.btnMarcarAsistencia.text = "Marcar Salida"
                binding.btnMarcarAsistencia.isEnabled = true
            }
            else -> {
                binding.tvEstadoAsistencia.text = "Estado: Sin marcación"
                binding.btnMarcarAsistencia.text = "Marcar Entrada"
            }
        }
    }

    private fun configurarNavegacion() {
        binding.btnMarcarAsistencia.setOnClickListener { marcarAsistencia() }
        binding.cardAsistencia.setOnClickListener { startActivity(Intent(this, AsistenciaActivity::class.java)) }
        binding.cardEmpleados.setOnClickListener  { startActivity(Intent(this, EmpleadosActivity::class.java)) }
        binding.cardUsuarios.setOnClickListener   { startActivity(Intent(this, UsuariosActivity::class.java)) }
        binding.cardHorarios.setOnClickListener   { startActivity(Intent(this, HorariosActivity::class.java)) }
        binding.sibInicio.setOnClickListener      { /* ya estamos aquí */ }
        binding.sibAsistencia.setOnClickListener  { startActivity(Intent(this, AsistenciaActivity::class.java)) }
        binding.sibEmpleados.setOnClickListener   { startActivity(Intent(this, EmpleadosActivity::class.java)) }
        binding.sibUsuarios.setOnClickListener    { startActivity(Intent(this, UsuariosActivity::class.java)) }
        binding.sibHorarios.setOnClickListener    { startActivity(Intent(this, HorariosActivity::class.java)) }
        binding.sibLogout.setOnClickListener      { confirmarCerrarSesion() }
        binding.btnLogout.setOnClickListener      { confirmarCerrarSesion() }
    }

    private fun confirmarCerrarSesion() {
        // HU03 — confirmación y eliminar sesión activa
        MaterialAlertDialogBuilder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Deseas cerrar sesión? No quedará ninguna sesión activa.")
            .setPositiveButton("Sí, salir") { _, _ ->
                Thread {
                    UsuarioDatabase.getInstance(this).usuarioDao().eliminarTodo()
                    runOnUiThread {
                        startActivity(
                            Intent(this, LoginActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            }
                        )
                    }
                }.start()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun iniciarReloj() {
        val fmt = SimpleDateFormat("HH:mm:ss  •  dd/MM/yyyy", Locale.getDefault())
        relojHandler.post(object : Runnable {
            override fun run() {
                binding.tvFechaHora.text = fmt.format(Date())
                relojHandler.postDelayed(this, 1000)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        relojHandler.removeCallbacksAndMessages(null)
    }
}
