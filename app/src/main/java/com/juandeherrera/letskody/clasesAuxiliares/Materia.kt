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
        color1 = Color(0xFF5B2ACC),
        color2 = Color(0xFF6337CC),
        color3 = Color(0xFF8856E1),
        idRecursoImagen = R.drawable.kody_geografia,
        ruta = "${AppScreens.MenuJuegosMaterias.route}/geografía"
    ),
    Materia(
        nombre = "Matemáticas",
        color1 = Color(0xFFE31C1C),
        color2 = Color(0xFFD73939),
        color3 = Color(0xFFE04848),
        idRecursoImagen = R.drawable.kody_mates,
        ruta = "${AppScreens.MenuJuegosMaterias.route}/matemáticas"
    ),
    Materia(
        nombre = "Lengua",
        color1 = Color(0xFF1CC5E3),
        color2 = Color(0xFF39D7CA),
        color3 = Color(0xFF48DBE0),
        idRecursoImagen = R.drawable.kody_lengua,
        ruta = "${AppScreens.MenuJuegosMaterias.route}/lengua"
    )
)