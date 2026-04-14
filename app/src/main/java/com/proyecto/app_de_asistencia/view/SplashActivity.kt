package com.proyecto.app_de_asistencia.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.proyecto.app_de_asistencia.databinding.ActivitySplashBinding
import com.proyecto.app_de_asistencia.db.UsuarioDatabase
import com.proyecto.app_de_asistencia.util.Constantes
import com.proyecto.app_de_asistencia.view.empleado.EmpleadoHomeActivity
import com.proyecto.app_de_asistencia.view.gerente.GerenteHomeActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Iniciar animación Lottie
        binding.lottieAnimation.playAnimation()

        // Esperar 5 segundos y luego verificar sesión en Room
        Handler(Looper.getMainLooper()).postDelayed({
            verificarSesion()
        }, Constantes.SPLASH_DELAY_MS)
    }

    private fun verificarSesion() {
        Thread {
            val db      = UsuarioDatabase.getInstance(this)
            val usuario = db.usuarioDao().obtenerSinLiveData()

            runOnUiThread {
                val intent = when {
                    usuario != null && usuario.rol == "gerente"  ->
                        Intent(this, GerenteHomeActivity::class.java)
                    usuario != null && usuario.rol == "empleado" ->
                        Intent(this, EmpleadoHomeActivity::class.java)
                    else ->
                        Intent(this, LoginActivity::class.java)
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }
        }.start()
    }
}
