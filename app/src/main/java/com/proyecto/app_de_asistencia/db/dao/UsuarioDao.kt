package com.proyecto.app_de_asistencia.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.proyecto.app_de_asistencia.db.entity.UsuarioEntity

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertar(usuario: UsuarioEntity)

    @Query("DELETE FROM usuario")
    fun eliminarTodo()

    @Query("SELECT * FROM usuario LIMIT 1")
    fun obtener(): LiveData<UsuarioEntity>

    @Query("SELECT * FROM usuario LIMIT 1")
    fun obtenerSinLiveData(): UsuarioEntity?
}
