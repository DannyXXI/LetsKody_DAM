package com.juandeherrera.letskody.clasesAuxiliares

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

// clase para representar los datos de un trazo del lienzo
data class TrazoLienzo(
    val puntos: List<Offset>,  // lista de coordenadas del punto que forma el trazo (X, Y)
    val color: Color,          // color del trazo (blanco si actúa como goma de borrar)
    val grosor: Float,         // grosor del trazo en pixeles
    val esGoma: Boolean        // indica si el trazo es de borrado
)

// paleta de colores básicos
val coloresBasicos = listOf(
    Color(0xFFE53935) to "Rojo",
    Color(0xFF1E88E5) to "Azul",
    Color(0xFF00BCD4) to "Cyan",
    Color(0xFF43A047) to "Verde",
    Color(0xFFFDD835) to "Amarillo",
    Color(0xFFFF7043) to "Naranja",
    Color(0xFF6D4C41) to "Marrón",
    Color(0xFFEC407A) to "Rosa",
    Color(0xFF8E24AA) to "Violeta",
    Color(0xFF212121) to "Negro",
    Color(0xFF9E9E9E) to "Gris",
    Color(0xFFFFFFFF) to "Blanco"
)
