package com.juandeherrera.letskody.firebase

// clase para gestionar los datos de los usuarios almacenados en Firebase
data class UsuarioFirebase (
    val nombre: String? = null,
    val apellidos: String? = null,
    val telefono: String? = null,
    val email: String? = null,
    val sexo: String? = null,
    val fechaNacimiento: String? = null,
    val foto: String? = null,
    val ultimoEnvioTicket: Long = 0L
)