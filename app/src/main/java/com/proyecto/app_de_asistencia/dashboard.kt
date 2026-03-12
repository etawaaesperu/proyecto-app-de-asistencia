package com.proyecto.app_de_asistencia

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class dashboard : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Recibir datos del login
        val nombre   = intent.getStringExtra("nombre")   ?: "Usuario"
        val apellido = intent.getStringExtra("apellido") ?: ""
        val rol      = intent.getStringExtra("rol")      ?: "empleado"
        val idEmpleado = intent.getLongExtra("id_empleado", -1)

        // Saludo: "Bienvenido, Félix!" como en el mockup
        findViewById<TextView>(R.id.tvBienvenido).text = "Bienvenido, $nombre!"
        findViewById<TextView>(R.id.tvRol).text =
            "Rol: ${rol.replaceFirstChar { it.uppercase() }}"

        // Cards del menú principal
        findViewById<MaterialButton>(R.id.cardMenuPrincipal).setOnClickListener {
            Toast.makeText(this, "Menú Principal", Toast.LENGTH_SHORT).show()
        }
        findViewById<MaterialButton>(R.id.cardUsuarios).setOnClickListener {
            Toast.makeText(this, "Usuarios", Toast.LENGTH_SHORT).show()
        }
        findViewById<MaterialButton>(R.id.cardHorarios).setOnClickListener {
            Toast.makeText(this, "Horarios", Toast.LENGTH_SHORT).show()
        }
        findViewById<MaterialButton>(R.id.cardNoticias).setOnClickListener {
            Toast.makeText(this, "Noticias", Toast.LENGTH_SHORT).show()
        }

        // Sidebar
        findViewById<ImageButton>(R.id.sibHome).setOnClickListener {
            Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()
        }
        findViewById<ImageButton>(R.id.sibUsuarios).setOnClickListener {
            Toast.makeText(this, "Usuarios", Toast.LENGTH_SHORT).show()
        }
        findViewById<ImageButton>(R.id.sibEntrada).setOnClickListener {
            Toast.makeText(this, "Registrar Entrada — próximamente", Toast.LENGTH_SHORT).show()
        }
        findViewById<ImageButton>(R.id.sibSalida).setOnClickListener {
            Toast.makeText(this, "Registrar Salida — próximamente", Toast.LENGTH_SHORT).show()
        }
        findViewById<ImageButton>(R.id.sibLogout).setOnClickListener { cerrarSesion() }
        findViewById<MaterialButton>(R.id.btnLogout).setOnClickListener { cerrarSesion() }
    }

    private fun cerrarSesion() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro que deseas salir?")
            .setPositiveButton("Sí") { _, _ ->
                startActivity(
                    Intent(this, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or
                                 Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    }
                )
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
