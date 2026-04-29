package com.juandeherrera.letskody.clasesAuxiliares

// clase para representar los datos del usuario en el ranking en juegos con cronómetro
data class FilaRankingJuegosCronometro(
    val posicion: Int,
    val puntos: Int,
    val tiempo: Int
)

// clase para representar los datos del usuario en el ranking en juegos de contrarreloj
data class FilaRankingJuegosContrarreloj(
    val posicion: Int,
    val puntos: Int,
    val fallos: Int
)

// clase genérica para representar los que reciben los componentes de cada tabla de ranking
data class FilaTablaRanking(
    val posicion: Int,
    val columna1: String,
    val columna2: String
)