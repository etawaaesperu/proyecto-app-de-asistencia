package com.proyecto.app_de_asistencia

import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

/**
 * Cliente HTTP simple — no necesita dependencias externas.
 * IMPORTANTE: llamar siempre desde un hilo de fondo (executor), nunca desde el main thread.
 */
object ApiClient {

    fun postJson(urlString: String, body: JSONObject): JSONObject {
        var conn: HttpURLConnection? = null
        try {
            conn = (URL(urlString).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                setRequestProperty("Accept", "application/json")
                doOutput = true
                connectTimeout = AppConfig.TIMEOUT_MS
                readTimeout    = AppConfig.TIMEOUT_MS
            }

            // Enviar JSON
            conn.outputStream.use { os ->
                os.write(body.toString().toByteArray(StandardCharsets.UTF_8))
            }

            // Leer respuesta (errores 4xx/5xx van por errorStream)
            val stream = if (conn.responseCode >= 400) conn.errorStream else conn.inputStream
            val text   = BufferedReader(InputStreamReader(stream, StandardCharsets.UTF_8))
                .use { it.readText() }

            return JSONObject(text)

        } finally {
            conn?.disconnect()
        }
    }
}
