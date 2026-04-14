package com.proyecto.app_de_asistencia.view.gerente

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
import com.proyecto.app_de_asistencia.databinding.ActivityUsuariosBinding
import com.proyecto.app_de_asistencia.db.UsuarioDatabase
import com.proyecto.app_de_asistencia.retrofit.ClienteRetrofit
import com.proyecto.app_de_asistencia.retrofit.request.UsuarioRequest
import com.proyecto.app_de_asistencia.retrofit.response.EmpleadoResponse
import com.proyecto.app_de_asistencia.retrofit.response.UsuarioResponse
import com.proyecto.app_de_asistencia.util.AppMensaje
import com.proyecto.app_de_asistencia.util.TipoMensaje
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UsuariosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUsuariosBinding
    private lateinit var adapter: UsuarioAdapter
    private var gerenteLogueadoId: String = ""
    private val regexPassword = Regex("^(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\d\\s]).{8,}\$")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsuariosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnAgregarUsuario.setOnClickListener { cargarEmpleadosYMostrarDialogo() }

        val db = UsuarioDatabase.getInstance(this)
        db.usuarioDao().obtener().observe(this) { if (it != null) gerenteLogueadoId = it.id }

        adapter = UsuarioAdapter(mutableListOf()) { usuario -> confirmarEliminar(usuario) }
        binding.rvUsuarios.layoutManager = LinearLayoutManager(this)
        binding.rvUsuarios.adapter = adapter

        cargarUsuarios()
    }

    private fun cargarUsuarios() {
        ClienteRetrofit.api.getUsuarios().enqueue(object : Callback<List<UsuarioResponse>> {
            override fun onResponse(call: Call<List<UsuarioResponse>>, response: Response<List<UsuarioResponse>>) {
                if (response.isSuccessful) adapter.actualizar(response.body() ?: emptyList())
                else AppMensaje.enviarMensaje(binding.root, "Error al cargar usuarios", TipoMensaje.ERROR)
            }
            override fun onFailure(call: Call<List<UsuarioResponse>>, t: Throwable) {
                AppMensaje.enviarMensaje(binding.root, "Sin conexión al servidor", TipoMensaje.ERROR)
            }
        })
    }

    private fun cargarEmpleadosYMostrarDialogo() {
        ClienteRetrofit.api.getEmpleados().enqueue(object : Callback<List<EmpleadoResponse>> {
            override fun onResponse(call: Call<List<EmpleadoResponse>>, response: Response<List<EmpleadoResponse>>) {
                val sinUsuario = response.body()?.filter { !it.tieneUsuario } ?: emptyList()
                if (sinUsuario.isEmpty()) {
                    AppMensaje.enviarMensaje(binding.root, "Todos los empleados ya tienen usuario", TipoMensaje.INFO)
                } else {
                    mostrarDialogoNuevoUsuario(sinUsuario)
                }
            }
            override fun onFailure(call: Call<List<EmpleadoResponse>>, t: Throwable) {
                AppMensaje.enviarMensaje(binding.root, "Sin conexión al servidor", TipoMensaje.ERROR)
            }
        })
    }

    private fun mostrarDialogoNuevoUsuario(empleados: List<EmpleadoResponse>) {
        val contenedor = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 24)
        }

        val spinnerEmpleados = Spinner(this).apply {
            adapter = ArrayAdapter(this@UsuariosActivity, android.R.layout.simple_spinner_dropdown_item,
                empleados.map { "${it.nombre} ${it.apellidos}" })
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = 16 }
        }

        val etUsuario = EditText(this).apply {
            hint = "Nombre de usuario"
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = 16 }
        }
        val etPassword = EditText(this).apply {
            hint = "Contraseña (8+ chars, mayúscula, número, símbolo)"
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = 16 }
        }
        val spinnerRol = Spinner(this).apply {
            adapter = ArrayAdapter(this@UsuariosActivity, android.R.layout.simple_spinner_dropdown_item, listOf("empleado", "gerente", "admin"))
        }

        contenedor.addView(spinnerEmpleados)
        contenedor.addView(etUsuario)
        contenedor.addView(etPassword)
        contenedor.addView(spinnerRol)
        val scroll = ScrollView(this).apply { addView(contenedor) }

        MaterialAlertDialogBuilder(this)
            .setTitle("Nuevo Usuario")
            .setView(scroll)
            .setPositiveButton("Crear") { _, _ ->
                val usuario  = etUsuario.text.toString().trim()
                val password = etPassword.text.toString()
                val rol      = spinnerRol.selectedItem.toString()
                val emp      = empleados[spinnerEmpleados.selectedItemPosition]

                if (usuario.isEmpty()) { AppMensaje.enviarMensaje(binding.root, "Ingresa un nombre de usuario", TipoMensaje.ERROR); return@setPositiveButton }
                if (!regexPassword.matches(password)) { AppMensaje.enviarMensaje(binding.root, "Contraseña débil: 8+ chars, mayúscula, número y símbolo", TipoMensaje.ERROR); return@setPositiveButton }

                val request = UsuarioRequest(emp.id, usuario, password, rol)
                ClienteRetrofit.api.crearUsuario(request).enqueue(object : Callback<UsuarioResponse> {
                    override fun onResponse(call: Call<UsuarioResponse>, response: Response<UsuarioResponse>) {
                        if (response.isSuccessful) { AppMensaje.enviarMensaje(binding.root, "Usuario creado", TipoMensaje.SUCCESS); cargarUsuarios() }
                        else AppMensaje.enviarMensaje(binding.root, "Error al crear usuario", TipoMensaje.ERROR)
                    }
                    override fun onFailure(call: Call<UsuarioResponse>, t: Throwable) { AppMensaje.enviarMensaje(binding.root, "Sin conexión", TipoMensaje.ERROR) }
                })
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun confirmarEliminar(usuario: UsuarioResponse) {
        if (usuario.id.toString() == gerenteLogueadoId) {
            AppMensaje.enviarMensaje(binding.root, "No puedes eliminar tu propia cuenta", TipoMensaje.WARNING); return
        }
        MaterialAlertDialogBuilder(this)
            .setTitle("Eliminar cuenta de usuario")
            .setMessage(
                "¿Eliminar la cuenta de usuario '${usuario.usuario}'?\n\n" +
                "ℹ Solo se eliminará la cuenta de acceso al sistema.\n" +
                "El empleado asociado permanecerá intacto en la base de datos."
            )
            .setPositiveButton("Continuar") { _, _ ->
                ClienteRetrofit.api.eliminarUsuario(usuario.id).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) { AppMensaje.enviarMensaje(binding.root, "Usuario eliminado", TipoMensaje.SUCCESS); cargarUsuarios() }
                        else AppMensaje.enviarMensaje(binding.root, "Error al eliminar", TipoMensaje.ERROR)
                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) { AppMensaje.enviarMensaje(binding.root, "Sin conexión", TipoMensaje.ERROR) }
                })
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}

// ── Adapter ────────────────────────────────────────────────────────────────────
class UsuarioAdapter(
    private val lista: MutableList<UsuarioResponse>,
    private val onEliminar: (UsuarioResponse) -> Unit
) : RecyclerView.Adapter<UsuarioAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvNombre  : TextView    = v.findViewById(R.id.tvNombreUsuario)
        val tvCorreo  : TextView    = v.findViewById(R.id.tvCorreo)
        val tvRol     : TextView    = v.findViewById(R.id.tvRolBadge)
        val btnEliminar: ImageButton = v.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_usuario, parent, false))

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = lista[position]
        holder.tvNombre.text = item.usuario
        holder.tvCorreo.text = "${item.nombre} ${item.apellido}"
        holder.tvRol.text    = item.rol
        val rolColor = if (item.rol == "gerente" || item.rol == "admin") android.graphics.Color.parseColor("#9B59B6")
                       else android.graphics.Color.parseColor("#3BB412")
        holder.tvRol.setTextColor(rolColor)
        holder.btnEliminar.setOnClickListener { onEliminar(item) }
    }

    fun actualizar(nuevaLista: List<UsuarioResponse>) {
        lista.clear(); lista.addAll(nuevaLista); notifyDataSetChanged()
    }
}
