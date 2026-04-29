package com.juandeherrera.letskody.clasesAuxiliares

// clase para representar los datos de una tecla del piano
data class TeclaPiano(
    val nombre: String,
    val frecuencia: Double,
    val esNegra: Boolean
)

// lista con todas las teclas del piano
val TECLAS_PIANO = listOf(
    TeclaPiano(nombre = "Fa2", frecuencia = 87.31, esNegra = false),
    TeclaPiano(nombre = "Fa2#", frecuencia = 92.50, esNegra = true ),
    TeclaPiano(nombre = "Sol2", frecuencia = 98.00, esNegra = false),
    TeclaPiano(nombre = "Sol2#", frecuencia = 103.83, esNegra = true ),
    TeclaPiano(nombre = "La2", frecuencia = 110.00, esNegra = false),
    TeclaPiano(nombre = "La2#", frecuencia = 116.54, esNegra = true ),
    TeclaPiano(nombre = "Si2", frecuencia = 123.47, esNegra = false),
    TeclaPiano(nombre = "Do3", frecuencia = 130.81, esNegra = false),
    TeclaPiano(nombre = "Do3#", frecuencia = 138.59, esNegra = true ),
    TeclaPiano(nombre = "Re3", frecuencia = 146.83, esNegra = false),
    TeclaPiano(nombre = "Re3#", frecuencia = 155.56, esNegra = true ),
    TeclaPiano(nombre = "Mi3", frecuencia = 164.81, esNegra = false),
    TeclaPiano(nombre = "Fa3", frecuencia = 174.61, esNegra = false),
    TeclaPiano(nombre = "Fa3#", frecuencia = 185.00, esNegra = true ),
    TeclaPiano(nombre = "Sol3", frecuencia = 196.00, esNegra = false),
    TeclaPiano(nombre = "Sol3#", frecuencia = 207.65, esNegra = true ),
    TeclaPiano(nombre = "La3", frecuencia = 220.00, esNegra = false),
    TeclaPiano(nombre = "La3#", frecuencia = 233.08, esNegra = true ),
    TeclaPiano(nombre = "Si3", frecuencia = 246.94, esNegra = false),
    TeclaPiano(nombre = "Do4", frecuencia = 261.63, esNegra = false),
    TeclaPiano(nombre = "Do4#", frecuencia = 277.18, esNegra = true ),
    TeclaPiano(nombre = "Re4", frecuencia = 293.66, esNegra = false),
    TeclaPiano(nombre = "Re4#", frecuencia = 311.13, esNegra = true ),
    TeclaPiano(nombre = "Mi4", frecuencia = 329.63, esNegra = false),
    TeclaPiano(nombre = "Fa4", frecuencia = 349.23, esNegra = false),
    TeclaPiano(nombre = "Fa4#", frecuencia = 369.99, esNegra = true ),
    TeclaPiano(nombre = "Sol4", frecuencia = 392.00, esNegra = false),
    TeclaPiano(nombre = "Sol4#", frecuencia = 415.30, esNegra = true ),
    TeclaPiano(nombre = "La4", frecuencia = 440.00, esNegra = false),
    TeclaPiano(nombre = "La4#", frecuencia = 466.16, esNegra = true ),
    TeclaPiano(nombre = "Si4", frecuencia = 493.88, esNegra = false),
    TeclaPiano(nombre = "Do5", frecuencia = 523.25, esNegra = false),
    TeclaPiano(nombre = "Do5#", frecuencia = 554.37, esNegra = true ),
    TeclaPiano(nombre = "Re5", frecuencia = 587.33, esNegra = false),
    TeclaPiano(nombre = "Re5#", frecuencia = 622.25, esNegra = true ),
    TeclaPiano(nombre = "Mi5", frecuencia = 659.25, esNegra = false),
    TeclaPiano(nombre = "Fa5", frecuencia = 698.46, esNegra = false)
)