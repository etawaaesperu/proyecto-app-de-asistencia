package com.proyecto.app_de_asistencia.retrofit.request

data class LoginRequest(
    val usuario: String,
    val contrasena: String
)

data class EmpleadoRequest(
    val nombre: String,
    val apellidos: String,
    val dni: String,
    val email: String,
    val telefono: String? = null,
    val fechaNacimiento: String? = null,
    val estadoCivil: String? = null,
    val cargo: String? = null,
    val departamento: String? = null
)

data class UsuarioRequest(
    val empleadoId: Long,
    val usuario: String,
    val contrasena: String,
    val rol: String
)

data class HorarioRequest(
    val empleadoId: Long,
    val fecha: String,
    val horaEntrada: String,
    val horaSalida: String? = null,
    val observaciones: String? = null
)

data class AsistenciaRequest(
    val empleadoId: Long,
    val estado: String,
    val justificacion: String? = null
)
