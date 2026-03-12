package com.proyecto.app_de_asistencia

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var etUsuario    : TextInputEditText
    private lateinit var etPassword   : TextInputEditText
    private lateinit var btnIngresar  : MaterialButton
    private lateinit var progressLogin: LinearProgressIndicator
    private lateinit var tvOlvide     : TextView

    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etUsuario     = findViewById(R.id.etUsuario)
        etPassword    = findViewById(R.id.etPassword)
        btnIngresar   = findViewById(R.id.btnIngresar)
        progressLogin = findViewById(R.id.progressLogin)
        tvOlvide      = findViewById(R.id.tvOlvide)

        btnIngresar.setOnClickListener { intentarLogin() }
        tvOlvide.setOnClickListener {
            Toast.makeText(this, "Recuperación de contraseña próximamente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun intentarLogin() {
        val usuario  = etUsuario.text?.toString()?.trim() ?: ""
        val password = etPassword.text?.toString() ?: ""

        // Validaciones locales
        if (usuario.isEmpty()) {
            etUsuario.error = "Ingresa tu ID de empleado"
            etUsuario.requestFocus()
            return
        }
        if (password.isEmpty()) {
            etPassword.error = "Ingresa tu contraseña"
            etPassword.requestFocus()
            return
        }

        setLoading(true)

        // Petición HTTP en hilo de fondo — OBLIGATORIO en Android
        executor.execute {
            try {
                val body = JSONObject().apply {
                    put("usuario",   usuario)
                    put("contrasena", password)
                }

                val respuesta = ApiClient.postJson(AppConfig.URL_LOGIN, body)

                runOnUiThread {
                    setLoading(false)
                    if (respuesta.optBoolean("success", false)) {
                        navegarDashboard(respuesta)
                    } else {
                        val msg = respuesta.optString("mensaje", "Usuario o contraseña incorrectos")
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                        etPassword.error = msg
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    setLoading(false)
                    Toast.makeText(
                        this,
                        "Sin conexión. Verifica que el servidor esté activo.\n${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun navegarDashboard(resp: JSONObject) {
        val usuario = resp.optJSONObject("usuario") ?: return
        startActivity(
            Intent(this, dashboard::class.java).apply {
                putExtra("id_usuario",   usuario.optLong("id"))
                putExtra("nombre",       usuario.optString("nombre"))
                putExtra("apellido",     usuario.optString("apellido"))
                putExtra("rol",          usuario.optString("rol"))
                putExtra("id_empleado",  usuario.optLong("idEmpleado"))
                // Impide volver al login con el botón Atrás
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
        )
    }

    private fun setLoading(loading: Boolean) {
        progressLogin.visibility = if (loading) View.VISIBLE else View.GONE
        btnIngresar.isEnabled    = !loading
        btnIngresar.text         = if (loading) "Verificando..." else "Iniciar Sesión"
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown()
    }
}
