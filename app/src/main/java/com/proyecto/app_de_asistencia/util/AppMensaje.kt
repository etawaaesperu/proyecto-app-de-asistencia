package com.proyecto.app_de_asistencia.util

import android.graphics.Color
import android.view.View
import com.google.android.material.snackbar.Snackbar

object AppMensaje {
    fun enviarMensaje(root: View, mensaje: String, tipo: TipoMensaje) {
        val snackbar = Snackbar.make(root, mensaje, Snackbar.LENGTH_LONG)
        val color = when (tipo) {
            TipoMensaje.ERROR   -> Color.parseColor("#EF0000")
            TipoMensaje.SUCCESS -> Color.parseColor("#3BB412")
            TipoMensaje.WARNING -> Color.parseColor("#F59E0B")
            TipoMensaje.INFO    -> Color.parseColor("#1A6EF7")
        }
        snackbar.view.setBackgroundColor(color)
        snackbar.setTextColor(Color.WHITE)
        snackbar.show()
    }
}
