package com.juandeherrera.letskody.firebase

// clase para gestionar los datos de las puntuaciones del juego Numinario 1 almacenados en Firebase
data class PuntuacionNuminario1Firebase (
    val puntos: Int? = null,
    val fallos: Int? = null,
    val usuario: String? = null
)