package com.proyecto.app_de_asistencia.view.gerente

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.proyecto.app_de_asistencia.R
import com.proyecto.app_de_asistencia.databinding.ActivityAsistenciaBinding
import com.proyecto.app_de_asistencia.retrofit.ClienteRetrofit
import com.proyecto.app_de_asistencia.retrofit.response.AsistenciaResponse
import com.proyecto.app_de_asistencia.util.AppMensaje
import com.proyecto.app_de_asistencia.util.TipoMensaje
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class AsistenciaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAsistenciaBinding
    private lateinit var adapter: AsistenciaAdapter
    private var fechaSeleccionada: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAsistenciaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.tvFechaSeleccionada.text = fechaSeleccionada
        binding.btnFiltroFecha.setOnClickListener { mostrarDatePicker() }

        adapter = AsistenciaAdapter(mutableListOf())
        binding.rvAsistencia.layoutManager = LinearLayoutManager(this)
        binding.rvAsistencia.adapter = adapter

        cargarAsistencias()
    }

    private fun mostrarDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, day ->
            fechaSeleccionada = "%04d-%02d-%02d".format(year, month + 1, day)
            binding.tvFechaSeleccionada.text = fechaSeleccionada
            cargarAsistencias()
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun cargarAsistencias() {
        ClienteRetrofit.api.getAsistencias(fechaSeleccionada).enqueue(object : Callback<List<AsistenciaResponse>> {
            override fun onResponse(call: Call<List<AsistenciaResponse>>, response: Response<List<AsistenciaResponse>>) {
                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()
                    adapter.actualizar(lista)
                    actualizarContadores(lista)
                } else {
                    AppMensaje.enviarMensaje(binding.root, "Error al obtener asistencias", TipoMensaje.ERROR)
                }
            }
            override fun onFailure(call: Call<List<AsistenciaResponse>>, t: Throwable) {
                AppMensaje.enviarMensaje(binding.root, "Sin conexión al servidor", TipoMensaje.ERROR)
            }
        })
    }

    private fun actualizarContadores(lista: List<AsistenciaResponse>) {
        binding.tvTotalAsistieron.text = "✓ ${lista.count { it.estado == "ASISTENCIA" || it.estado == "COMPLETA" }}"
        binding.tvTotalTardanza.text   = "⚠ ${lista.count { it.estado == "TARDANZA" }}"
        binding.tvTotalFaltas.text     = "✗ ${lista.count { it.estado == "FALTA" }}"
    }
}

// ── Adapter ────────────────────────────────────────────────────────────────────
class AsistenciaAdapter(private val lista: MutableList<AsistenciaResponse>) :
    RecyclerView.Adapter<AsistenciaAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvNombre  : TextView = v.findViewById(R.id.tvNombreEmpleado)
        val tvEstado  : TextView = v.findViewById(R.id.tvEstado)
        val tvEntrada : TextView = v.findViewById(R.id.tvEntrada)
        val tvSalida  : TextView = v.findViewById(R.id.tvSalida)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_asistencia, parent, false))

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = lista[position]
        holder.tvNombre.text  = item.nombreEmpleado
        holder.tvEntrada.text = "Entrada: ${item.horaEntrada ?: "--:--"}"
        holder.tvSalida.text  = "Salida: ${item.horaSalida ?: "--:--"}"
        holder.tvEstado.text  = item.estado

        val color = when (item.estado) {
            "ASISTENCIA", "COMPLETA" -> android.graphics.Color.parseColor("#3BB412")
            "TARDANZA"               -> android.graphics.Color.parseColor("#F59E0B")
            else                     -> android.graphics.Color.parseColor("#EF0000")
        }
        holder.tvEstado.setTextColor(color)
    }

    fun actualizar(nuevaLista: List<AsistenciaResponse>) {
        lista.clear()
        lista.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}
