package com.proyecto.app_de_asistencia.view.gerente

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.proyecto.app_de_asistencia.R
import com.proyecto.app_de_asistencia.databinding.ActivityEmpleadosBinding
import com.proyecto.app_de_asistencia.retrofit.ClienteRetrofit
import com.proyecto.app_de_asistencia.retrofit.request.EmpleadoRequest
import com.proyecto.app_de_asistencia.retrofit.response.EmpleadoResponse
import com.proyecto.app_de_asistencia.util.AppMensaje
import com.proyecto.app_de_asistencia.util.TipoMensaje
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Calendar

class EmpleadosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmpleadosBinding
    private lateinit var adapter: EmpleadoAdapter
    private var listaCompleta: List<EmpleadoResponse> = emptyList()

    // Opciones de cargo disponibles en el sistema (HU09)
    private val opcionesCargo = listOf("empleado", "gerente", "admin")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmpleadosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnAgregarEmpleado.setOnClickListener { mostrarDialogoEmpleado(null) }

        adapter = EmpleadoAdapter(
            mutableListOf(),
            onEditar   = { emp -> mostrarDialogoEmpleado(emp) },
            onEliminar = { emp -> confirmarEliminar(emp) }
        )
        binding.rvEmpleados.layoutManager = LinearLayoutManager(this)
        binding.rvEmpleados.adapter = adapter

        // HU07 — búsqueda en tiempo real por nombre o DNI
        binding.etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { filtrarLista(s.toString()) }
        })

        cargarEmpleados()
    }

    private fun cargarEmpleados() {
        ClienteRetrofit.api.getEmpleados().enqueue(object : Callback<List<EmpleadoResponse>> {
            override fun onResponse(call: Call<List<EmpleadoResponse>>, response: Response<List<EmpleadoResponse>>) {
                if (response.isSuccessful) {
                    listaCompleta = response.body() ?: emptyList()
                    filtrarLista(binding.etBuscar.text.toString())
                } else {
                    AppMensaje.enviarMensaje(binding.root, "Error al cargar empleados", TipoMensaje.ERROR)
                }
            }
            override fun onFailure(call: Call<List<EmpleadoResponse>>, t: Throwable) {
                AppMensaje.enviarMensaje(binding.root, "Sin conexión al servidor", TipoMensaje.ERROR)
            }
        })
    }

    // HU07 — filtrar por nombre completo o DNI
    private fun filtrarLista(query: String) {
        val filtrada = if (query.isEmpty()) {
            listaCompleta
        } else {
            listaCompleta.filter {
                val nombreCompleto = "${it.nombre} ${it.apellidos}".lowercase()
                nombreCompleto.contains(query.lowercase()) || it.dni.contains(query)
            }
        }
        if (filtrada.isEmpty() && query.isNotEmpty()) {
            AppMensaje.enviarMensaje(binding.root, "Empleado no encontrado", TipoMensaje.WARNING)
        }
        adapter.actualizar(filtrada)
    }

    private fun mostrarDialogoEmpleado(empleado: EmpleadoResponse?) {
        val esEdicion = empleado != null
        val contenedor = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 24)
        }

        fun campo(hint: String, valor: String = "", tipo: Int = android.text.InputType.TYPE_CLASS_TEXT): EditText =
            EditText(this).apply {
                this.hint = hint
                setText(valor)
                inputType = tipo
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = 16 }
            }

        val etNombre       = campo("Nombre *", empleado?.nombre ?: "")
        val etApellidos    = campo("Apellidos *", empleado?.apellidos ?: "")
        val etDni          = campo("DNI (8 dígitos) *", empleado?.dni ?: "", android.text.InputType.TYPE_CLASS_NUMBER)
        val etEmail        = campo("Email *", empleado?.email ?: "", android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
        val etTelefono     = campo("Teléfono (9 dígitos)", empleado?.telefono ?: "", android.text.InputType.TYPE_CLASS_NUMBER)
        val etDepartamento = campo("Departamento", empleado?.departamento ?: "")

        // HU09 — Cargo como Spinner (combobox) con opciones: empleado, gerente, admin
        val tvLabelCargo = TextView(this).apply {
            text = "Cargo *"
            setTextColor(android.graphics.Color.parseColor("#7A94B0"))
            setPadding(0, 8, 0, 4)
        }
        val spinnerCargo = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@EmpleadosActivity,
                android.R.layout.simple_spinner_dropdown_item,
                opcionesCargo
            )
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 16 }
            // Pre-seleccionar el cargo actual si es edición
            val cargoActual = empleado?.cargo?.lowercase() ?: "empleado"
            val idx = opcionesCargo.indexOfFirst { it == cargoActual }.coerceAtLeast(0)
            setSelection(idx)
        }

        var fechaNac = empleado?.fechaNacimiento ?: ""
        val tvFecha = TextView(this).apply {
            text = fechaNac.ifEmpty { "Fecha nacimiento (toca para elegir)" }
            setTextColor(android.graphics.Color.parseColor(if (fechaNac.isEmpty()) "#7A94B0" else "#E2EAF5"))
            setPadding(0, 12, 0, 16)
        }
        tvFecha.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this, { _, y, m, d ->
                fechaNac = "%04d-%02d-%02d".format(y, m + 1, d)
                tvFecha.text = fechaNac
                tvFecha.setTextColor(android.graphics.Color.parseColor("#E2EAF5"))
            }, cal.get(Calendar.YEAR) - 30, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        val opcionesEC = listOf("Soltero", "Casado", "Divorciado", "Viudo")
        val spinnerEC = Spinner(this).apply {
            adapter = ArrayAdapter(this@EmpleadosActivity, android.R.layout.simple_spinner_dropdown_item, opcionesEC)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = 16 }
            val idx = opcionesEC.indexOf(empleado?.estadoCivil ?: "Soltero")
            if (idx >= 0) setSelection(idx)
        }

        contenedor.addView(etNombre)
        contenedor.addView(etApellidos)
        contenedor.addView(etDni)
        contenedor.addView(etEmail)
        contenedor.addView(etTelefono)
        contenedor.addView(tvLabelCargo)
        contenedor.addView(spinnerCargo)
        contenedor.addView(etDepartamento)
        contenedor.addView(tvFecha)
        contenedor.addView(spinnerEC)
        val scroll = ScrollView(this).apply { addView(contenedor) }

        MaterialAlertDialogBuilder(this)
            .setTitle(if (esEdicion) "Editar Empleado" else "Nuevo Empleado")
            .setView(scroll)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre       = etNombre.text.toString().trim()
                val apellidos    = etApellidos.text.toString().trim()
                val dni          = etDni.text.toString().trim()
                val email        = etEmail.text.toString().trim()
                val telefono     = etTelefono.text.toString().trim()
                val cargo        = spinnerCargo.selectedItem.toString()   // valor del spinner
                val departamento = etDepartamento.text.toString().trim()
                val estadoCivil  = spinnerEC.selectedItem.toString()

                // HU09 — validaciones de campos
                if (nombre.isEmpty() || apellidos.isEmpty() || dni.isEmpty() || email.isEmpty()) {
                    AppMensaje.enviarMensaje(binding.root, "Completa los campos obligatorios (*)", TipoMensaje.ERROR)
                    return@setPositiveButton
                }
                if (dni.length != 8 || !dni.all { it.isDigit() }) {
                    AppMensaje.enviarMensaje(binding.root, "El DNI debe tener exactamente 8 dígitos", TipoMensaje.ERROR)
                    return@setPositiveButton
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    AppMensaje.enviarMensaje(binding.root, "El formato del email no es válido", TipoMensaje.ERROR)
                    return@setPositiveButton
                }
                if (telefono.isNotEmpty() && (telefono.length != 9 || !telefono.all { it.isDigit() })) {
                    AppMensaje.enviarMensaje(binding.root, "El teléfono debe tener 9 dígitos", TipoMensaje.ERROR)
                    return@setPositiveButton
                }
                if (fechaNac.isNotEmpty()) {
                    try {
                        val edad = Period.between(LocalDate.parse(fechaNac, DateTimeFormatter.ofPattern("yyyy-MM-dd")), LocalDate.now()).years
                        if (edad < 18 || edad > 65) {
                            AppMensaje.enviarMensaje(binding.root, "La edad debe estar entre 18 y 65 años", TipoMensaje.ERROR)
                            return@setPositiveButton
                        }
                    } catch (_: Exception) {}
                }

                val request = EmpleadoRequest(
                    nombre, apellidos, dni, email,
                    telefono.ifEmpty { null }, fechaNac.ifEmpty { null },
                    estadoCivil, cargo, departamento.ifEmpty { null }
                )

                if (esEdicion) {
                    ClienteRetrofit.api.editarEmpleado(empleado!!.id, request).enqueue(object : Callback<EmpleadoResponse> {
                        override fun onResponse(call: Call<EmpleadoResponse>, response: Response<EmpleadoResponse>) {
                            if (response.isSuccessful) {
                                AppMensaje.enviarMensaje(binding.root, "Empleado actualizado correctamente", TipoMensaje.SUCCESS)
                                cargarEmpleados()
                            } else AppMensaje.enviarMensaje(binding.root, "Error al actualizar", TipoMensaje.ERROR)
                        }
                        override fun onFailure(call: Call<EmpleadoResponse>, t: Throwable) { AppMensaje.enviarMensaje(binding.root, "Sin conexión", TipoMensaje.ERROR) }
                    })
                } else {
                    ClienteRetrofit.api.crearEmpleado(request).enqueue(object : Callback<EmpleadoResponse> {
                        override fun onResponse(call: Call<EmpleadoResponse>, response: Response<EmpleadoResponse>) {
                            if (response.isSuccessful) {
                                AppMensaje.enviarMensaje(binding.root, "Empleado registrado correctamente", TipoMensaje.SUCCESS)
                                cargarEmpleados()
                            } else AppMensaje.enviarMensaje(binding.root, "Error al crear empleado", TipoMensaje.ERROR)
                        }
                        override fun onFailure(call: Call<EmpleadoResponse>, t: Throwable) { AppMensaje.enviarMensaje(binding.root, "Sin conexión", TipoMensaje.ERROR) }
                    })
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // HU14 — confirmación clara + eliminar en cascada (usuario + horarios + asistencias)
    private fun confirmarEliminar(empleado: EmpleadoResponse) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Eliminar empleado")
            .setMessage(
                "¿Eliminar a ${empleado.nombre} ${empleado.apellidos}?\n\n" +
                "⚠ Esta acción eliminará también:\n" +
                "• Su cuenta de usuario (si tiene)\n" +
                "• Sus horarios registrados\n" +
                "• Sus registros de asistencia\n\n" +
                "Esta acción no se puede deshacer."
            )
            .setPositiveButton("Continuar y eliminar") { _, _ ->
                ClienteRetrofit.api.eliminarEmpleado(empleado.id).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            AppMensaje.enviarMensaje(binding.root, "Empleado eliminado correctamente", TipoMensaje.SUCCESS)
                            cargarEmpleados()
                        } else {
                            AppMensaje.enviarMensaje(binding.root, "No se pudo eliminar el empleado", TipoMensaje.ERROR)
                        }
                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) { AppMensaje.enviarMensaje(binding.root, "Sin conexión", TipoMensaje.ERROR) }
                })
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}

class EmpleadoAdapter(
    private val lista: MutableList<EmpleadoResponse>,
    private val onEditar: (EmpleadoResponse) -> Unit,
    private val onEliminar: (EmpleadoResponse) -> Unit
) : RecyclerView.Adapter<EmpleadoAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvNombre   : TextView    = v.findViewById(R.id.tvNombreCompleto)
        val tvCargo    : TextView    = v.findViewById(R.id.tvCargoDepartamento)
        val tvDni      : TextView    = v.findViewById(R.id.tvDni)
        val btnEditar  : ImageButton = v.findViewById(R.id.btnEditar)
        val btnEliminar: ImageButton = v.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_empleado, parent, false))

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = lista[position]
        holder.tvNombre.text  = "${item.nombre} ${item.apellidos}"
        holder.tvCargo.text   = "${item.cargo} / ${item.departamento}"
        holder.tvDni.text     = "DNI: ${item.dni}"
        holder.btnEditar.setOnClickListener   { onEditar(item) }
        holder.btnEliminar.setOnClickListener { onEliminar(item) }
    }

    fun actualizar(nuevaLista: List<EmpleadoResponse>) {
        lista.clear(); lista.addAll(nuevaLista); notifyDataSetChanged()
    }
}
