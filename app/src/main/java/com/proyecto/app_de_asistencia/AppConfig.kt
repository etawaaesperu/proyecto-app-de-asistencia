package com.proyecto.app_de_asistencia

object AppConfig {
    // ============================================================
    //  CONFIGURA AQUÍ LA URL DE TU SERVIDOR
    //  Emulador Android  → HOST = "10.0.2.2"
    //  Celular físico    → HOST = IP de tu PC en WiFi (ej: "192.168.1.15")
    //  Ver IP de tu PC   → cmd → ipconfig → IPv4
    // ============================================================
    private const val HOST    = "10.0.2.2"
    private const val PORT    = "8080"
    private const val PROJECT = "AsistenciaAPI"   // nombre exacto de tu proyecto NetBeans

    val BASE_URL        = "http://$HOST:$PORT/$PROJECT"
    val URL_LOGIN       = "$BASE_URL/api/login"
    val URL_ASISTENCIA  = "$BASE_URL/api/asistencia"
    const val TIMEOUT_MS = 10_000
}
