package com.juandeherrera.letskody.viewModels.euroBanderas

import com.juandeherrera.letskody.localdb.BanderasEuropaData

// clase sellada para indicar los diferentes estados del juego (cargando, jugando, juego finalizado, error)
sealed class EstadoEuroBanderas {

    object Cargando: EstadoEuroBanderas()  // pantalla de carga mientras se leen las banderas de la base de datos

    // estado principal que guarda los principales datos durante la partida
    data class Jugando(
        val preguntaActual: BanderasEuropaData,  // objeto completo de la bandera que se muestra
        val opciones: List<String>,              // lista con las 4 opciones de respuesta ya mezcladas aleatoriamente
        val numeroPregunta: Int,                 // número de la pregunta actual (desde 1 a hasta 12)
        val puntos: Int,                         // puntuación acumulada hasta el momento
        val tiempoCronometro: Int,               // segundos que han pasado desde que empezó la partida
        val respuestaSeleccionada: String?,      // opción que el usuario ha escogido (es null si aún no ha respondido)
        val opcionCorrecta: String,              // texto de la respuesta correcta
        val penalizacionAcumulada: Int           // segundos totales de penalización acumulados por fallos
    ): EstadoEuroBanderas()

    object Terminado: EstadoEuroBanderas()    // se muestra el modal con los resultados del jugador

    object ErrorSinBanderas: EstadoEuroBanderas()  // error si la base de datos no tiene suficientes banderas para completar una partida
}