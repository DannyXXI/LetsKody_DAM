package com.juandeherrera.letskody.clasesAuxiliares

import androidx.compose.ui.graphics.Color
import com.juandeherrera.letskody.navigation.AppScreens

// clase para representar los juegos de la sección de miscelánea
data class JuegoMiscelanea (
    val nombre: String,
    val color1: Color,
    val color2: Color,
    val color3: Color,
    val ruta: String
)

// lista inmutable con los juegos disponibles
val juegosMiscelanea = listOf(
    JuegoMiscelanea(
        nombre = "Estira y rebota",
        color1 = Color(0xFF10A812),
        color2 = Color(0xFF19C41B),
        color3 = Color(0xFF1DD71F),
        ruta = AppScreens.EstiraRebota.route
    ),
    JuegoMiscelanea(
        nombre = "Piano",
        color1 = Color(0xFF10A812),
        color2 = Color(0xFF19C41B),
        color3 = Color(0xFF1DD71F),
        ruta = AppScreens.Piano.route
    ),
    JuegoMiscelanea(
        nombre = "Draw Arena",
        color1 = Color(0xFF10A812),
        color2 = Color(0xFF19C41B),
        color3 = Color(0xFF1DD71F),
        ruta = AppScreens.DrawArena.route
    ),
)