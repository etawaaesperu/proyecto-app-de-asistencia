package com.proyecto.app_de_asistencia.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.proyecto.app_de_asistencia.databinding.ActivityRecuperarContrasenaBinding
import com.proyecto.app_de_asistencia.util.AppMensaje
import com.proyecto.app_de_asistencia.util.TipoMensaje

class RecuperarContrasenaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecuperarContrasenaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecuperarContrasenaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEnviar.setOnClickListener { simularEnvio() }
        binding.tvVolverLogin.setOnClickListener { finish() }
    }

    private fun simularEnvio() {
        val correo = binding.etCorreo.text.toString().trim()

        if (correo.isEmpty()) {
            AppMensaje.enviarMensaje(binding.root, "Ingresa tu correo institucional", TipoMensaje.ERROR)
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            AppMensaje.enviarMensaje(binding.root, "El formato del correo no es válido", TipoMensaje.ERROR)
            return
        }

        setLoading(true)
        binding.root.postDelayed({
            setLoading(false)
            mostrarConfirmacion(correo)
        }, 1800)
    }

    private fun mostrarConfirmacion(correo: String) {
        binding.layoutConfirmacion.visibility = View.VISIBLE
        binding.tvMensajeConfirmacion.text =
            "Si $correo está registrado recibirás las instrucciones en breve. Revisa también tu carpeta de spam."
        binding.btnEnviar.isEnabled = false
        binding.etCorreo.isEnabled = false
        AppMensaje.enviarMensaje(binding.root, "Solicitud enviada", TipoMensaje.SUCCESS)
    }

    private fun setLoading(loading: Boolean) {
        binding.progressRecuperar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnEnviar.isEnabled = !loading
        binding.btnEnviar.text = if (loading) "Enviando..." else "Enviar instrucciones"
    }
}
