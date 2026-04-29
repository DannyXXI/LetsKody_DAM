package com.juandeherrera.letskody.clasesAuxiliares

// enumeración que indica la acción que se realizara a la puntuación del jugador al guardar y salir de un juego
enum class AccionPuntuacion {
    INSERTADA,    // primera vez que el usuario termina el juego

    ACTUALIZADA,  // cuando el usuario mejora su puntuación o su tiempo

    SIN_CAMBIOS  // cuando el usuario no mejora su puntuación ni su tiempo
}