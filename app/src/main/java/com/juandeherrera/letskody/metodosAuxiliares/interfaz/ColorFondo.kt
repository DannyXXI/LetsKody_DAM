package com.juandeherrera.letskody.metodosAuxiliares.interfaz

import androidx.compose.ui.graphics.Color

// función auxiliar para obtener el color de fondo del contenedor
fun colorFondo (genero: String): Color {
    // se devuelve el color de fondo según el género del usuario
    return when (genero) {
        "Hombre" -> {
            Color(0xFF9CC6FF)
        }
        "Mujer" -> {
            Color(0xFFFF9CE6)
        }
        else -> {
            Color(0xFFFFBF9C)
        }
    }
}