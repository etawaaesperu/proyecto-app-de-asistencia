package com.proyecto.app_de_asistencia.view.gerente

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.proyecto.app_de_asistencia.R
import com.proyecto.app_de_asistencia.databinding.ActivityHorariosBinding
import com.proyecto.app_de_asistencia.retrofit.ClienteRetrofit
import com.proyecto.app_de_asistencia.retrofit.request.HorarioRequest
import com.proyecto.app_de_asistencia.retrofit.response.EmpleadoResponse
import com.proyecto.app_de_asistencia.retrofit.response.HorarioResponse
import com.proyecto.app_de_asistencia.util.AppMensaje
import com.proyecto.app_de_asistencia.util.TipoMensaje
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class HorariosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHorariosBinding
    private lateinit var adapter: HorarioAdapter
    private var empleadoFiltroId: Long? = null
    private var listaEmpleados: List<EmpleadoResponse> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHorariosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnAgregarHorario.setOnClickListener {
            // HU11 — alerta informativa al ingresar al formulario
            MaterialAlertDialogBuilder(this)
                .setTitle("Registrar Horario")
                .setMessage("Vas a registrar el horario de un empleado.\nAsegúrate de tener los datos correctos antes de continuar.")
                .setPositiveButton("Continuar") { _, _ -> cargarEmpleadosYMostrarDialogo() }
                .setNegativeButton("Cancelar", null)
                .show()
        }
        binding.btnFiltrarHorario.setOnClickListener { mostrarDialogoFiltro() }

        adapter = HorarioAdapter(mutableListOf())
        binding.rvHorarios.layoutManager = LinearLayoutManager(this)
        binding.rvHorarios.adapter = adapter

        cargarHorarios()
    }

    private fun cargarHorarios() {
        ClienteRetrofit.api.getHorarios().enqueue(object : Callback<List<HorarioResponse>> {
            override fun onResponse(call: Call<List<HorarioResponse>>, response: Response<List<HorarioResponse>>) {
                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()
                    val filtrada = if (empleadoFiltroId != null) lista.filter { it.empleadoId == empleadoFiltroId } else lista
                    adapter.actualizar(filtrada)
                } else AppMensaje.enviarMensaje(binding.root, "Error al cargar horarios", TipoMensaje.ERROR)
            }
            override fun onFailure(call: Call<List<HorarioResponse>>, t: Throwable) {
                AppMensaje.enviarMensaje(binding.root, "Sin conexión al servidor", TipoMensaje.ERROR)
            }
        })
    }

    private fun cargarEmpleadosYMostrarDialogo() {
        ClienteRetrofit.api.getEmpleados().enqueue(object : Callback<List<EmpleadoResponse>> {
            override fun onResponse(call: Call<List<EmpleadoResponse>>, response: Response<List<EmpleadoResponse>>) {
                listaEmpleados = response.body() ?: emptyList()
                if (listaEmpleados.isEmpty()) AppMensaje.enviarMensaje(binding.root, "No hay empleados registrados", TipoMensaje.INFO)
                else mostrarDialogoNuevoHorario()
            }
            override fun onFailure(call: Call<List<EmpleadoResponse>>, t: Throwable) {
                AppMensaje.enviarMensaje(binding.root, "Sin conexión al servidor", TipoMensaje.ERROR)
            }
        })
    }

    private fun mostrarDialogoNuevoHorario() {
        val contenedor = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 24)
        }

        val spinnerEmpleados = Spinner(this).apply {
            adapter = ArrayAdapter(this@HorariosActivity, android.R.layout.simple_spinner_dropdown_item,
                listaEmpleados.map { "${it.nombre} ${it.apellidos}" })
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = 16 }
        }

        val cal = Calendar.getInstance()
        var fechaElegida = ""
        val tvFecha = TextView(this).apply {
            text = "Fecha: (toca para elegir)"
            setTextColor(android.graphics.Color.parseColor("#7A94B0"))
            setPadding(0, 12, 0, 16)
        }
        tvFecha.setOnClickListener {
            DatePickerDialog(this, { _, y, m, d ->
                fechaElegida = "%04d-%02d-%02d".format(y, m + 1, d)
                tvFecha.text = "Fecha: $fechaElegida"
                tvFecha.setTextColor(android.graphics.Color.parseColor("#E2EAF5"))
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        var horaEntrada = ""
        val tvEntrada = TextView(this).apply {
            text = "Hora entrada: (toca para elegir)"
            setTextColor(android.graphics.Color.parseColor("#7A94B0"))
            setPadding(0, 12, 0, 16)
        }
        tvEntrada.setOnClickListener {
            TimePickerDialog(this, { _, h, m ->
                horaEntrada = "%02d:%02d".format(h, m)
                tvEntrada.text = "Hora entrada: $horaEntrada"
                tvEntrada.setTextColor(android.graphics.Color.parseColor("#E2EAF5"))
            }, 7, 0, true).show()
        }

        var horaSalida = ""
        val tvSalida = TextView(this).apply {
            text = "Hora salida: (opcional)"
            setTextColor(android.graphics.Color.parseColor("#7A94B0"))
            setPadding(0, 12, 0, 16)
        }
        tvSalida.setOnClickListener {
            TimePickerDialog(this, { _, h, m ->
                horaSalida = "%02d:%02d".format(h, m)
                tvSalida.text = "Hora salida: $horaSalida"
                tvSalida.setTextColor(android.graphics.Color.parseColor("#E2EAF5"))
            }, 17, 0, true).show()
        }

        val etObs = EditText(this).apply {
            hint = "Observaciones (opcional)"
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = 8 }
        }

        contenedor.addView(spinnerEmpleados)
        contenedor.addView(tvFecha)
        contenedor.addView(tvEntrada)
        contenedor.addView(tvSalida)
        contenedor.addView(etObs)
        val scroll = ScrollView(this).apply { addView(contenedor) }

        MaterialAlertDialogBuilder(this)
            .setTitle("Nuevo Horario")
            .setView(scroll)
            .setPositiveButton("Guardar") { _, _ ->
                if (fechaElegida.isEmpty() || horaEntrada.isEmpty()) {
                    AppMensaje.enviarMensaje(binding.root, "Selecciona fecha y hora de entrada", TipoMensaje.ERROR); return@setPositiveButton
                }
                val emp = listaEmpleados[spinnerEmpleados.selectedItemPosition]
                val request = HorarioRequest(emp.id, fechaElegida, horaEntrada, horaSalida.ifEmpty { null }, etObs.text.toString().trim().ifEmpty { null })
                ClienteRetrofit.api.crearHorario(request).enqueue(object : Callback<HorarioResponse> {
                    override fun onResponse(call: Call<HorarioResponse>, response: Response<HorarioResponse>) {
                        if (response.isSuccessful) { AppMensaje.enviarMensaje(binding.root, "✓ Horario registrado correctamente", TipoMensaje.SUCCESS); cargarHorarios() }
                        else AppMensaje.enviarMensaje(binding.root, "Error al guardar horario", TipoMensaje.ERROR)
                    }
                    override fun onFailure(call: Call<HorarioResponse>, t: Throwable) { AppMensaje.enviarMensaje(binding.root, "Sin conexión", TipoMensaje.ERROR) }
                })
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoFiltro() {
        val opciones = listOf("Todos") + listaEmpleados.map { "${it.nombre} ${it.apellidos}" }
        MaterialAlertDialogBuilder(this)
            .setTitle("Filtrar por empleado")
            .setItems(opciones.toTypedArray()) { _, idx ->
                empleadoFiltroId = if (idx == 0) null else listaEmpleados[idx - 1].id
                cargarHorarios()
            }
            .show()
    }
}

// ── Adapter ────────────────────────────────────────────────────────────────────
class HorarioAdapter(private val lista: MutableList<HorarioResponse>) :
    RecyclerView.Adapter<HorarioAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvNombre : TextView = v.findViewById(R.id.tvNombreEmpleado)
        val tvFecha  : TextView = v.findViewById(R.id.tvFecha)
        val tvHoras  : TextView = v.findViewById(R.id.tvHoras)
        val tvObs    : TextView = v.findViewById(R.id.tvObservaciones)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_horario, parent, false))

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = lista[position]
        holder.tvNombre.text = item.nombreEmpleado
        holder.tvFecha.text  = "Fecha: ${item.fecha}"
        holder.tvHoras.text  = "${item.horaEntrada} → ${item.horaSalida ?: "Sin salida"}"
        if (!item.observaciones.isNullOrEmpty()) {
            holder.tvObs.visibility = View.VISIBLE
            holder.tvObs.text = item.observaciones
        } else {
            holder.tvObs.visibility = View.GONE
        }
    }

    fun actualizar(nuevaLista: List<HorarioResponse>) {
        lista.clear(); lista.addAll(nuevaLista); notifyDataSetChanged()
    }
}
