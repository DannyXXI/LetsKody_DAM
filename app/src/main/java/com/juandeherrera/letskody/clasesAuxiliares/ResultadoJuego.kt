package com.juandeherrera.letskody.clasesAuxiliares

// clase para representar los resultados de los juegos
data class ResultadoJuego (
    val puntos: Int,       // puntuación total obtenida
    val tiempoBase: Int,   // segundos reales que ha tardado el jugador (en contrarreloj se pone el tiempo inicial)
    val penalizacion: Int, // segundos de penalización acumulados (en contrarreloj se usa para contar los fallos)
    val tiempoTotal: Int   // tiempo que se guarda en la base de datos (tiempoBase + penalización)
)