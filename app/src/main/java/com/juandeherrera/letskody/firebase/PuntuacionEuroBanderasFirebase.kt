package com.juandeherrera.letskody.firebase

// clase para gestionar los datos de las puntuaciones del juego Euro-Banderas almacenados en Firebase
data class PuntuacionEuroBanderasFirebase (
    val puntos: Int? = null,
    val tiempo: Int? = null,
    val usuario: String? = null
)