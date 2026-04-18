package com.juandeherrera.letskody.clasesAuxiliares

// clase para representar los resultados de los juegos con cronómetro
data class ResultadoJuegoCronometro (
    val puntos: Int,       // puntuación total obtenida
    val tiempoBase: Int,   // segundos reales que ha tardado el jugador
    val penalizacion: Int, // segundos de penalización acumulados
    val tiempoTotal: Int   // tiempo que se guarda en la base de datos (tiempoBase + penalización)
)