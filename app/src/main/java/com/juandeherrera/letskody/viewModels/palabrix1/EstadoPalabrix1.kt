package com.juandeherrera.letskody.viewModels.palabrix1

import com.juandeherrera.letskody.localdb.PalabrasPalabrix1Data

// clase sellada para indicar los diferentes estados del juego (cargando, jugando, juego finalizado, error)
sealed class EstadoPalabrix1 {

    object Cargando: EstadoPalabrix1()  // pantalla de carga mientras se leen las banderas de la base de datos

    // estado principal que guarda los principales datos durante la partida
    data class Jugando(
        val preguntaActual: PalabrasPalabrix1Data,  // objeto completo de la bandera que se muestra
        val numeroPregunta: Int,                    // número de la pregunta actual (desde 1 a hasta 12)
        val puntos: Int,                            // puntuación acumulada hasta el momento
        val tiempoCronometro: Int,                  // segundos que han pasado desde que empezó la partida
        val respuestaSeleccionada: String?,         // opción que el usuario ha escogido (es null si aún no ha respondido)
        val opcionCorrecta: String,                 // texto de la respuesta correcta
        val penalizacionAcumulada: Int              // segundos totales de penalización acumulados por fallos
    ): EstadoPalabrix1()

    object Terminado: EstadoPalabrix1()    // se muestra el modal con los resultados del jugador

    object ErrorSinPalabras: EstadoPalabrix1()  // error si la base de datos no tiene suficientes banderas para completar una partida
}