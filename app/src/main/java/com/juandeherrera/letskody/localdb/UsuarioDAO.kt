package com.juandeherrera.letskody.localdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// OPERACIONES QUE SE REALIZARAN EN LA TABLA USUARIO
@Dao
interface UsuarioDAO {
    // OBTENER EL USUARIO DEL PERFIL
    // con flow emitirá cambios automáticamente que serán notificados a Jetpack Compose
    @Query(value = "SELECT * FROM ${Estructura.Usuario.TABLE_NAME} LIMIT 1")
    fun getUser(): Flow<UsuarioData?>

    // AGREGAR UN NUEVO USUARIO
    @Insert
    fun nuevoUsuario (usuarioData: UsuarioData)

    // REFRESCAR UN USUARIO EXISTENTE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun refrescarUsuario(usuarioData: UsuarioData)

    // AGREGAR LA LISTA DE USUARIOS DE FIREBASE A LOCAL
    @Insert
    fun agregarUsuarios (usuarios: List<UsuarioData>)

    // ACTUALIZAR UN USUARIO EXISTENTE
    @Update
    fun actualizarUsuario(usuarioData: UsuarioData)

    // ELIMINAR EL USUARIO DEL PERFIL
    // se usan el parámetro (:email) para evitar inyección SQL y que sea gestionado a través de la función
    // se usa ? para comprobar si el resultado es null antes de usarlo
    @Query("DELETE FROM ${Estructura.Usuario.TABLE_NAME} WHERE emailUsuario = :email")
    fun eliminarUsuario(email:String): Int

    // ELIMINAR TODOS LOS USUARIOS
    @Query("DELETE FROM ${Estructura.Usuario.TABLE_NAME}")
    fun eliminarTodosUsuarios(): Int
}