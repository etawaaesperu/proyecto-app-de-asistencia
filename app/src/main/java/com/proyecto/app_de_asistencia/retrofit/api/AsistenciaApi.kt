package com.proyecto.app_de_asistencia.retrofit.api

import com.proyecto.app_de_asistencia.retrofit.request.*
import com.proyecto.app_de_asistencia.retrofit.response.*
import retrofit2.Call
import retrofit2.http.*

interface AsistenciaApi {

    // AUTH
    @POST("api/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    // EMPLEADOS
    @GET("api/empleados")
    fun getEmpleados(): Call<List<EmpleadoResponse>>

    @POST("api/empleados")
    fun crearEmpleado(@Body request: EmpleadoRequest): Call<EmpleadoResponse>

    @PUT("api/empleados/{id}")
    fun editarEmpleado(
        @Path("id") id: Long,
        @Body request: EmpleadoRequest
    ): Call<EmpleadoResponse>

    @DELETE("api/empleados/{id}")
    fun eliminarEmpleado(@Path("id") id: Long): Call<Void>

    // USUARIOS
    @GET("api/usuarios")
    fun getUsuarios(): Call<List<UsuarioResponse>>

    @POST("api/usuarios")
    fun crearUsuario(@Body request: UsuarioRequest): Call<UsuarioResponse>

    @DELETE("api/usuarios/{id}")
    fun eliminarUsuario(@Path("id") id: Long): Call<Void>

    // HORARIOS
    @GET("api/horarios")
    fun getHorarios(): Call<List<HorarioResponse>>

    @POST("api/horarios")
    fun crearHorario(@Body request: HorarioRequest): Call<HorarioResponse>

    // ASISTENCIA
    @GET("api/asistencia")
    fun getAsistencias(@Query("fecha") fecha: String): Call<List<AsistenciaResponse>>

    @POST("api/asistencia")
    fun registrarAsistencia(@Body request: AsistenciaRequest): Call<AsistenciaResponse>

    @PUT("api/asistencia/{id}")
    fun actualizarAsistencia(
        @Path("id") id: Long,
        @Body request: AsistenciaRequest
    ): Call<AsistenciaResponse>
}
