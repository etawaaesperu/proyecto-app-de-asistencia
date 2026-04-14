package com.proyecto.app_de_asistencia.retrofit

import com.proyecto.app_de_asistencia.retrofit.api.AsistenciaApi
import com.proyecto.app_de_asistencia.util.Constantes
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ClienteRetrofit {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private fun buildRetrofit() = Retrofit.Builder()
        .baseUrl(Constantes.URL_BASE)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: AsistenciaApi by lazy {
        buildRetrofit().create(AsistenciaApi::class.java)
    }
}
