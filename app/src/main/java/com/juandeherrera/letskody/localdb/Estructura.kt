package com.juandeherrera.letskody.localdb

// clase que se usa para indicar la estructura de la base de datos local
class Estructura {

    // BASE DE DATOS
    object DB {
        const val NAME = "letskody.db"  // nombre de la bd (crea el archivo con ese nombre)
    }

    // Tabla 1: Usuario
    object Usuario {
        const val TABLE_NAME = "usuario"  // nombre de la tabla

        // nombre de los campos de la tabla
        const val ID = "idUsuario"
        const val UID_USUARIO = "uidUsuario"
        const val NOMBRE = "nombreUsuario"
        const val APELLIDOS = "apellidosUsuario"
        const val TELEFONO = "telefonoUsuario"
        const val EMAIL = "emailUsuario"
        const val PASSWORD = "passwordUsuario"
        const val SEXO = "sexoUsuario"
        const val FECHA_NACIMIENTO = "fnacUsuario"
        const val FOTO_PERFIL = "fotoUsuario"
    }





}