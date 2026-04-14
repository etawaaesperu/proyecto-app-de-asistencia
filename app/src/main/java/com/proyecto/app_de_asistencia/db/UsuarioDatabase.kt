package com.proyecto.app_de_asistencia.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.proyecto.app_de_asistencia.db.dao.UsuarioDao
import com.proyecto.app_de_asistencia.db.entity.UsuarioEntity

@Database(entities = [UsuarioEntity::class], version = 1, exportSchema = false)
abstract class UsuarioDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var INSTANCE: UsuarioDatabase? = null

        fun getInstance(context: Context): UsuarioDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    UsuarioDatabase::class.java,
                    "asistencia_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
