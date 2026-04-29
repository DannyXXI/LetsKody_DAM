package com.juandeherrera.letskody.clasesAuxiliares

import androidx.compose.ui.graphics.Color
import com.juandeherrera.letskody.navigation.AppScreens

// clase para representar los juegos de cada materia
data class Juego (
    val materia: String,
    val nombre: String,
    val color1: Color,
    val color2: Color,
    val color3: Color,
    val ruta: String
)

// lista inmutable con los juegos que tiene cada materia
val juegos = listOf(
    Juego(
        materia = "geografía",
        nombre = "Euro-banderas",
        color1 = Color(0xFF5B2ACC),
        color2 = Color(0xFF6337CC),
        color3 = Color(0xFF8856E1),
        ruta = AppScreens.MenuEuroBanderas.route
    ),
    Juego(
        materia = "matemáticas",
        nombre = "Numinario I",
        color1 = Color(0xFFE31C1C),
        color2 = Color(0xFFD73939),
        color3 = Color(0xFFE04848),
        ruta = AppScreens.MenuNuminario1.route
    ),
    Juego(
        materia = "lengua",
        nombre = "Palabrix I",
        color1 = Color(0xFF1CC5E3),
        color2 = Color(0xFF39D7CA),
        color3 = Color(0xFF48DBE0),
        ruta = AppScreens.MenuPalabrix1.route
    )
)