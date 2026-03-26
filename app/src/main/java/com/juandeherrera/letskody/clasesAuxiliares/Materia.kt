package com.juandeherrera.letskody.clasesAuxiliares

import androidx.compose.ui.graphics.Color
import com.juandeherrera.letskody.R
import com.juandeherrera.letskody.navigation.AppScreens

// clase para representar los datos de cada materia en el carrusel
data class Materia (
    val nombre: String,
    val color1: Color,
    val color2: Color,
    val color3: Color,
    val idRecursoImagen: Int,
    val ruta: String
)

// lista inmutable con los datos de las materias para el carrusel
val materias = listOf(
    Materia(
        nombre = "Geografía",
        color1 = Color(0xFF311B92),
        color2 = Color(0xFF512DA8),
        color3 = Color(0xFF673AB7),
        idRecursoImagen = R.drawable.kody_geografia,
        ruta = AppScreens.Perfil.route
    ),
    Materia(
        nombre = "Matemáticas",
        color1 = Color(0xFF921B1B),
        color2 = Color(0xFFA82D2D),
        color3 = Color(0xFFB73A3A),
        idRecursoImagen = R.drawable.kody_mates,
        ruta = AppScreens.Perfil.route
    )
)