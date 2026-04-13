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

        const val ULTIMO_ENVIO_TICKET = "ultimoEnvioTicket"
    }

    // Tabla 2: Banderas de Europa
    object BanderasEuropa {
        const val TABLE_NAME = "banderasEuropa"  // nombre de la tabla

        // nombre de los campos de la tabla
        const val ID = "idBandera"
        const val URL_BANDERA = "urlBandera"
        const val OPCION1 = "opcion1"
        const val OPCION2 = "opcion2"
        const val OPCION3 = "opcion3"
        const val OPCION4 = "opcion4"
        const val OPCION_CORRECTA = "opcionCorrecta"
    }

    // Tabla 3: Puntuaciones del juego Euro-banderas
    object PuntuacionEuroBanderas {
        const val TABLE_NAME = "puntuacionEuroBanderas"  // nombre de la tabla

        // nombre de los campos de la tabla
        const val ID = "idPuntuacion"
        const val PUNTOS = "puntos"
        const val TIEMPO = "tiempo"
        const val USUARIO = "usuario"
    }



}