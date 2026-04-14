package com.proyecto.app_de_asistencia.util

import android.app.Application
import com.proyecto.app_de_asistencia.db.UsuarioDatabase

class MiApp : Application() {

    val database: UsuarioDatabase by lazy {
        UsuarioDatabase.getInstance(this)
    }

    override fun onCreate() {
        super.onCreate()
    }
}
