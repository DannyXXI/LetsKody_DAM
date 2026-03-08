package com.juandeherrera.letskody.metodosAuxiliares.interfaz

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// metodo auxiliar para obtener un fondo con degradado diagonal
@Composable
fun fondoDegradadoDiagonal (color1: Color, color2: Color, color3: Color) : Brush {

    // variable que crea una transicion infinita del degradado
    val transicionInfinita = rememberInfiniteTransition(label = "gradient")

    // variable que crea la animacion encargada de desplazar el degradado (devuelve un float)
    val animacion by transicionInfinita.animateFloat(
        initialValue = 0f,    // valor inicial
        targetValue = 1000f,  // valor final
        animationSpec = infiniteRepeatable(
            // se indica como será la animacion entre el valor inicial y el final
            animation = tween(
                durationMillis = 4000, // duración de ida en milisegundos
                easing = LinearEasing  // velocidad constante
            ),
            repeatMode = RepeatMode.Reverse // al llegar al final, la animacion se realiza hacia atrás
        ),
        label = "move" // se indica como se llama la animacion
    )

    // variable que se encarga de realizar el degradado lineal con una inclinación de 45º
    val degradadoDiagonal = Brush.linearGradient(
        // colores del degradado
        colors = listOf(
            color1,
            color2,
            color3
        ),
        // punto inicial del degradado
        start = Offset(
            x = animacion, // se desplaza horizontalmente usando el valor de la animacion
            y = 0f         // sin movimiento vertical
        ),
        // punto final del degradado
        end = Offset(
            x = 1000f + animacion, // desplazamiento horizontal + el de la animacion
            y = 1000f              // desplazamiento vertical fijo
        )
    )

    return degradadoDiagonal // se devuelve el degradado
}