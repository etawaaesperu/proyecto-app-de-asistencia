package com.proyecto.app_de_asistencia.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.proyecto.app_de_asistencia.databinding.ActivityLoginBinding
import com.proyecto.app_de_asistencia.db.UsuarioDatabase
import com.proyecto.app_de_asistencia.db.entity.UsuarioEntity
import com.proyecto.app_de_asistencia.retrofit.ClienteRetrofit
import com.proyecto.app_de_asistencia.retrofit.request.LoginRequest
import com.proyecto.app_de_asistencia.retrofit.response.LoginResponse
import com.proyecto.app_de_asistencia.util.AppMensaje
import com.proyecto.app_de_asistencia.util.TipoMensaje
import com.proyecto.app_de_asistencia.view.admin.AdminHomeActivity
import com.proyecto.app_de_asistencia.view.empleado.EmpleadoHomeActivity
import com.proyecto.app_de_asistencia.view.gerente.GerenteHomeActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // HU01 — botón deshabilitado hasta que ambos campos tengan texto
        binding.btnLogin.isEnabled = false
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val usuario = binding.etCorreo.text.toString().trim()
                val pass    = binding.etContrasena.text.toString()
                binding.btnLogin.isEnabled = usuario.isNotEmpty() && pass.isNotEmpty()
            }
        }
        binding.etCorreo.addTextChangedListener(watcher)
        binding.etContrasena.addTextChangedListener(watcher)

        binding.btnLogin.setOnClickListener { intentarLogin() }
        binding.tvOlvide.setOnClickListener {
            startActivity(Intent(this, RecuperarContrasenaActivity::class.java))
        }
    }

    private fun intentarLogin() {
        val usuario    = binding.etCorreo.text.toString().trim()
        val contrasena = binding.etContrasena.text.toString().trim()

        // HU01 — validación de contraseña: mínimo 6 caracteres
        if (contrasena.length < 6) {
            AppMensaje.enviarMensaje(binding.root, "La contraseña debe tener al menos 6 caracteres", TipoMensaje.ERROR)
            return
        }

        setLoading(true)

        ClienteRetrofit.api.login(LoginRequest(usuario, contrasena))
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    setLoading(false)
                    val body = response.body()
                    if (response.isSuccessful && body != null) {
                        procesarRespuesta(body)
                    } else {
                        AppMensaje.enviarMensaje(binding.root, "ID de empleado o contraseña incorrectos", TipoMensaje.ERROR)
                    }
                }
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    setLoading(false)
                    AppMensaje.enviarMensaje(binding.root, "Sin conexión. Verifica que el servidor esté activo.", TipoMensaje.ERROR)
                }
            })
    }

    private fun procesarRespuesta(response: LoginResponse) {
        if (!response.success || response.usuario == null) {
            AppMensaje.enviarMensaje(binding.root, response.mensaje ?: "ID de empleado no válido", TipoMensaje.ERROR)
            return
        }
        val u = response.usuario
        Thread {
            val db = UsuarioDatabase.getInstance(this)
            db.usuarioDao().eliminarTodo()
            db.usuarioDao().insertar(
                UsuarioEntity(u.id.toString(), u.nombre, u.apellido, u.rol, u.idEmpleado)
            )
            runOnUiThread {
                // Tres roles: "empleado" → solo asistencia, "gerente" y "admin" → CRUD completo
                val intent = when (u.rol.lowercase()) {
                    "gerente" -> Intent(this, GerenteHomeActivity::class.java)
                    "admin"   -> Intent(this, AdminHomeActivity::class.java)
                    else      -> Intent(this, EmpleadoHomeActivity::class.java)  // "empleado"
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }
        }.start()
    }

    private fun setLoading(loading: Boolean) {
        binding.progressLogin.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !loading
        binding.btnLogin.text = if (loading) "Verificando..." else "Iniciar Sesión"
    }
}
