package com.proyecto.app_de_asistencia.retrofit.response

data class UsuarioData(
    val id: Long,
    val nombre: String,
    val apellido: String,
    val rol: String,
    val idEmpleado: Long
)

data class LoginResponse(
    val success: Boolean,
    val mensaje: String? = null,
    val usuario: UsuarioData? = null
)

data class EmpleadoResponse(
    val id: Long,
    val codigo: String = "",
    val nombre: String,
    val apellidos: String,
    val departamento: String = "",
    val cargo: String = "",
    val dni: String,
    val email: String? = null,
    val telefono: String? = null,
    val fechaNacimiento: String? = null,
    val estadoCivil: String? = null,
    val tieneUsuario: Boolean = false
)

data class UsuarioResponse(
    val id: Long,
    val usuario: String,
    val correo: String = "",
    val nombre: String,
    val apellido: String,
    val rol: String,
    val empleadoId: Long? = null
)

data class HorarioResponse(
    val id: Long,
    val empleadoId: Long,
    val nombreEmpleado: String,
    val fecha: String,
    val horaEntrada: String,
    val horaSalida: String? = null,
    val observaciones: String? = null
)

data class AsistenciaResponse(
    val id: Long,
    val empleadoId: Long,
    val nombreEmpleado: String,
    val fecha: String,
    val horaEntrada: String? = null,
    val horaSalida: String? = null,
    val estado: String,
    val justificacion: String? = null
)
