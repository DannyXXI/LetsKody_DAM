package com.juandeherrera.letskody.metodosAuxiliares.interfaz

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.ceil

// función auxiliar para obtener un fondo con degradado diagonal
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

// función auxiliar para obtener un fondo con degradado diagonal e icono
@Composable
fun FondoDegradadoConIconos(color1: Color, color2: Color, color3: Color, icono: Painter, colorIcono: Color, duracion: Int, contenido: @Composable () -> Unit) {
    val tamanoIcono = 30.dp     // tamaño del icono

    val espaciadoIcono = 60.dp  // espaciado entre los iconos

    val opacidadIcono = 0.22f   // opacidad del icono

    val densidadPantalla = LocalDensity.current  // densidad de pantalla (para convertir dp a px)

    val degradado = fondoDegradadoDiagonal(color1 = color1, color2 = color2, color3 = color3)  // degradado diagonal animado de color

    // variable que crea una transición infinita que controlará el movimiento de los iconos
    val transicion = rememberInfiniteTransition(label = "iconos_diagonal")

    // variable que crea la animación encargada de desplazar el icono
    val desplazamiento by transicion.animateFloat(
        initialValue = 0f,  // valor inicial
        targetValue = 1f,   // valor final
        animationSpec = infiniteRepeatable(
            // se indica como será la animacion entre el valor inicial y el final
            animation = tween(
                durationMillis = duracion,  // duración de un ciclo completo (milisegundos)
                easing = LinearEasing       // velocidad constante
            ),
            repeatMode = RepeatMode.Restart  // al llegar al final vuelve al inicio (loop continuo sin rebote)
        ),
        label = "desplazamiento_iconos"  // se indica como se llama la animacion
    )

    // contenedor principal
    Box(
        modifier = Modifier.fillMaxSize()    // ocupa el espacio disponible
            .drawWithContent {
                drawRect(brush = degradado)  // se dibuja el degradado de fondo en el espacio disponible

                // se convierten las dimensiones de dp a píxeles
                val iconoPx = tamanoIcono.toPx()
                val espaciadoPx = espaciadoIcono.toPx()

                // se calcula el desplazamiento actual en píxeles dentro de una celda
                val offset = desplazamiento * espaciadoPx

                // se calcula las columnas y filas necesarias para cubrir la pantalla (se agrega dos más por los bordes)
                val columnas = ceil(x = size.width  / espaciadoPx).toInt() + 2
                val filas = ceil(x = size.height / espaciadoPx).toInt() + 2

                // se recorre cada celda de la cuadrícula para dibujar un icono
                with(receiver = icono) {
                    for (col in -1 until columnas) {
                        for (fila in -1 until filas) {
                            // posición base de cada celda en la cuadrícula sin animar
                            val baseX = col * espaciadoPx
                            val baseY = size.height - fila * espaciadoPx

                            // se aplica el desplazamiento diagonal al icono
                            val x = baseX + offset - iconoPx / 2f
                            val y = baseY - offset - iconoPx / 2f

                            // se dibuja el icono si está dentro o muy cerca de los límites visibles de la pantalla
                            if (x > -iconoPx && x < size.width  + iconoPx && y > -iconoPx && y < size.height + iconoPx) {
                                // --- CAMBIO AQUÍ ---
                                // Usamos withTransform para rotar SOLO el dibujo del icono
                                withTransform({
                                    // se rota el icono 45º a la derecha
                                    rotate(degrees = 45f, pivot = Offset(x = x + iconoPx / 2f, y = y + iconoPx / 2f))
                                }) {
                                    // se dibuja el icono
                                    translate(left = x, top = y) {
                                        draw(
                                            size = Size(width = iconoPx, height = iconoPx),  // tamaño del icono (px)
                                            alpha = opacidadIcono,                           // opacidad
                                            colorFilter = ColorFilter.tint(colorIcono)       // color
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                drawContent() // se dibuja el resto de la pantalla encima del fondo y los iconos
            }
    ){
        contenido()
    }
}