package com.juandeherrera.letskody.localdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// se indica que la clase es una entidad que corresponde a una tabla en la base de datos, accediendo por el nombre
// se indica que el email es un campo único, creando su índice
@Entity(
    tableName = Estructura.Usuario.TABLE_NAME,
    indices = [Index(value = [Estructura.Usuario.EMAIL], unique = true)]
)
data class UsuarioData (
    // clave primaria autogenerada incremental cuyo valor inicial es 0
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Estructura.Usuario.ID)
    val idUsuario: Int = 0,

    // columna del id del usuario
    @ColumnInfo(name = Estructura.Usuario.UID_USUARIO)
    val uidUsuario: String,

    // columna del nombre del usuario
    @ColumnInfo(name = Estructura.Usuario.NOMBRE)
    val nombreUsuario: String,

    // columna de los apellidos del usuario
    @ColumnInfo(name = Estructura.Usuario.APELLIDOS)
    val apellidosUsuario: String,

    // columna del numero de teléfono del usuario
    @ColumnInfo(name = Estructura.Usuario.TELEFONO)
    val telefonoUsuario: String,

    // columna del email del usuario
    @ColumnInfo(name = Estructura.Usuario.EMAIL)
    val emailUsuario: String,

    // columna de la contraseña del usuario
    @ColumnInfo(name = Estructura.Usuario.PASSWORD)
    val passwordUsuario: String,

    // columna del sexo del usuario
    @ColumnInfo(name = Estructura.Usuario.SEXO)
    val sexoUsuario: String,

    // columna de la fecha de nacimiento del usuario
    @ColumnInfo(name = Estructura.Usuario.FECHA_NACIMIENTO)
    val fnacUsuario: String,

    // columna de la foto del usuario
    @ColumnInfo(name = Estructura.Usuario.FOTO_PERFIL)
    val fotoUsuario: String,

    // columna de la marca temporal del último ticket enviado al servicio técnico con valor inicial de 0L
    @ColumnInfo(name = Estructura.Usuario.ULTIMO_ENVIO_TICKET)
    val ultimoEnvioTicket: Long = 0L
)