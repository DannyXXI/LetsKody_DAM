package com.juandeherrera.letskody.firebase

// clase para gestionar los datos de las puntuaciones del juego Palabrix 1 almacenados en Firebase
data class PuntuacionPalabrix1Firebase (
    val puntos: Int? = null,
    val tiempo: Int? = null,
    val usuario: String? = null
)