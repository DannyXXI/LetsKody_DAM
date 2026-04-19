package com.juandeherrera.letskody.clasesAuxiliares

// clase para representar los resultados de los juegos contrarreloj
data class ResultadoJuegoContrarreloj (
    val puntos: Int,    // puntuación total obtenida
    val fallos: Int     // número total de fallos cometidos durante la partida
)