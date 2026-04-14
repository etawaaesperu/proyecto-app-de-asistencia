package com.proyecto.app_de_asistencia.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuario")
data class UsuarioEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val apellido: String,
    val rol: String,
    val idEmpleado: Long
)
