package com.juandeherrera.letskody.metodosAuxiliares.interfaz

import androidx.compose.ui.graphics.Color

// función auxiliar para obtener el color de texto del nombre del usuario
fun colorTexto (genero: String): Color {
    // se devuelve el color del texto según el género del usuario
    return when (genero) {
        "Hombre" -> {
            Color(0xFF2364C9)
        }
        "Mujer" -> {
            Color(0xFFC923C1)
        }
        else -> {
            Color(0xFFC97023)
        }
    }
}