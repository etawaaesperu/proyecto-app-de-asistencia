package com.proyecto.app_de_asistencia.util

object Constantes {
    // ============================================================
    //  CAMBIA SOLO ESTA LÍNEA PARA CONECTAR TU BACKEND
    //  Emulador Android  → HOST = "10.0.2.2"
    //  Celular físico    → HOST = IP de tu PC en WiFi (ej: "192.168.1.15")
    // ============================================================
    private const val HOST    = "10.0.2.2"
    private const val PORT    = "8080"
    private const val PROJECT = "AsistenciaAPI"   // nombre exacto de tu proyecto NetBeans

    const val URL_BASE = "http://$HOST:$PORT/$PROJECT/"

    // Splash
    const val SPLASH_DELAY_MS = 5000L

    // Horarios de asistencia
    const val HORA_INICIO       = 7    // 7:00 am inicio
    const val MIN_FIN_PUNTUAL   = 30   // hasta 7:30 = ASISTENCIA
    const val HORA_FIN_TARDANZA = 8    // hasta 8:00 = TARDANZA — después = FALTA
}
