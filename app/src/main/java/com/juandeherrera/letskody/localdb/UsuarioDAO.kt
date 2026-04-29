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

    // ACTUALIZAR UN USUARIO EXISTENTE
    @Update
    fun actualizarUsuario(usuarioData: UsuarioData)

    // ACTUALIZAR LA MARCA TEMPORAL DEL ÚLTIMO TICKET ENVIADO AL SERVICIO TÉCNICO
    @Query(value = "UPDATE ${Estructura.Usuario.TABLE_NAME} SET ${Estructura.Usuario.ULTIMO_ENVIO_TICKET} = :marca WHERE ${Estructura.Usuario.UID_USUARIO} = :uid")
    fun actualizarUltimoEnvioTicket(uid: String, marca: Long)

    // ELIMINAR EL USUARIO DEL PERFIL
    // se usan el parámetro (:email) para evitar inyección SQL y que sea gestionado a través de la función
    // se usa ? para comprobar si el resultado es null antes de usarlo
    @Query(value = "DELETE FROM ${Estructura.Usuario.TABLE_NAME} WHERE emailUsuario = :email")
    fun eliminarUsuario(email:String): Int
}