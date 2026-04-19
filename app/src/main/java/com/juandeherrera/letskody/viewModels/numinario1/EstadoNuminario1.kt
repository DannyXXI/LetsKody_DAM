package com.juandeherrera.letskody.viewModels.numinario1

// clase sellada para indicar los diferentes estados del juego (cargando, jugando, juego finalizado, error)
sealed class EstadoNuminario1 {

    object Cargando: EstadoNuminario1()  // pantalla de carga inicial antes de empezar

    // estado principal que guarda los principales datos durante la partida
    data class Jugando(
        val numero1: Int,          // primer número de la operación actual
        val numero2: Int,          // segundo número de la operación actual
        val esSuma: Boolean,       // comprueba si es suma (true) o resta (false)
        val puntos: Int,           // puntuación acumulada hasta el momento
        val fallos: Int,           // número de fallos cometidos hasta el momento
        val tiempoRestante: Int,   // segundos restantes que quedan en el cronómetro (cuenta atrás)
        val pista: Pista?          // pista a mostrar al usuario tras un fallo (es null si no hay pista)
    ): EstadoNuminario1()

    object Terminado: EstadoNuminario1()    // se muestra el modal con los resultados del jugador
}

// enumeración que devuelve si la respuesta fallida del usuario era menor o mayor a la correcta (pista orientativa)
enum class Pista {
    MAYOR,
    MENOR
}