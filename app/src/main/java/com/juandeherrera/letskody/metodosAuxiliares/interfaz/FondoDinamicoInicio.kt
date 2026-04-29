package com.juandeherrera.letskody.metodosAuxiliares.interfaz

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import com.juandeherrera.letskody.clasesAuxiliares.MomentoDelDia

// función auxiliar para dibujar el árbol (su tronco y copa)
private fun dibujarArbol(drawScope: DrawScope, x: Float, y: Float, escala: Float, momento: MomentoDelDia) {
    with(receiver = drawScope) {
        val anchoTronco = 20f * escala
        val altoTronco = 60f * escala
        val radioCopa = 45f * escala

        // colores según el momento el día
        val colorTronco = when (momento) {
            MomentoDelDia.NOCHE -> Color(0xFF2D1B10)
            else -> Color(0xFF5D4037)
        }
        val colorHojas = when (momento) {
            MomentoDelDia.MANANA -> Color(0xFF2E7D32)
            MomentoDelDia.TARDE -> Color(0xFF1B5E20)
            MomentoDelDia.NOCHE -> Color(0xFF0A1A0A)
        }

        // dibujar Tronco
        drawRect(
            color = colorTronco,
            topLeft = Offset(x - anchoTronco / 2, y - altoTronco),
            size = Size(anchoTronco, altoTronco)
        )

        // dibujar la copa (hojas) - tres círculos para dar forma de "nube"
        drawCircle(
            color = colorHojas,
            radius = radioCopa,
            center = Offset(x, y - altoTronco - radioCopa * 0.5f)
        )
        drawCircle(
            color = colorHojas,
            radius = radioCopa * 0.8f,
            center = Offset(x - radioCopa * 0.6f, y - altoTronco - radioCopa * 0.3f)
        )
        drawCircle(
            color = colorHojas,
            radius = radioCopa * 0.8f,
            center = Offset(x + radioCopa * 0.6f, y - altoTronco - radioCopa * 0.3f)
        )
    }
}

// función auxiliar para dibujar la cabaña con chimenea y dos ventanas
private fun dibujarCabana(drawScope: DrawScope, x: Float, y: Float, escala: Float, momentoDelDia: MomentoDelDia, progresoHumo: Float) {
    with(receiver = drawScope) {
        val ancho = 170f * escala      // ancho de la cabaña
        val altoBase = 110f * escala   // alto de la pared
        val altoTejado = 85f * escala  // alto del tejado de la cabaña

        // paleta de colores según el momento del día
        val colorMaderaBase: Color = when (momentoDelDia) {
            MomentoDelDia.MANANA -> Color(0xFF8D6E63)  // marrón natural
            MomentoDelDia.TARDE  -> Color(0xFF7A4E3A)  // marrón oscurecido por sol bajo
            MomentoDelDia.NOCHE  -> Color(0xFF3A241D)  // madera casi negra
        }

        val colorMaderaOscura: Color = when (momentoDelDia) {
            MomentoDelDia.MANANA -> Color(0xFF5D4037)
            MomentoDelDia.TARDE  -> Color(0xFF4E2E20)
            MomentoDelDia.NOCHE  -> Color(0xFF1B1B1B)
        }

        val colorTejado: Color = when (momentoDelDia) {
            MomentoDelDia.MANANA -> Color(0xFF4E342E)  // teja rojo-marrón
            MomentoDelDia.TARDE  -> Color(0xFF3E2018)  // teja oscurecida
            MomentoDelDia.NOCHE  -> Color(0xFF1C1C1C)  // teja negra
        }

        val colorVentana: Color = when (momentoDelDia) {
            MomentoDelDia.MANANA -> Color(0xFFBBDEFB)  // cristal azul (reflejo del cielo)
            MomentoDelDia.TARDE  -> Color(0xFFFFCC80)  // cristal ámbar (reflejo del atardecer)
            MomentoDelDia.NOCHE  -> Color(0xFFFFE082)  // cristal amarillo (luz interior encendida)
        }

        val colorChimenea: Color = when (momentoDelDia) {
            MomentoDelDia.MANANA -> Color(0xFF6D4C41)  // ladrillo rojizo
            MomentoDelDia.TARDE  -> Color(0xFF5D3A28)  // ladrillo oscurecido
            MomentoDelDia.NOCHE  -> Color(0xFF2C1810)  // ladrillo casi negro
        }

        val colorChimeneaOscura: Color = when (momentoDelDia) {
            MomentoDelDia.MANANA -> Color(0xFF4E342E)
            MomentoDelDia.TARDE  -> Color(0xFF3E2518)
            MomentoDelDia.NOCHE  -> Color(0xFF1A0E08)
        }

        // pared principal visible
        drawRect(color = colorMaderaBase, topLeft = Offset(x = x - ancho/2, y = y - altoBase), size = Size(width = ancho, height = altoBase))

        val alturaTabla = altoBase / 6  // altura de las líneas que simulan las tablas de madera de la pared

        for (i in 1..5) {
            // se dibujan las líneas
            drawLine(color = colorMaderaOscura.copy(alpha = 0.5f),
                start = Offset(x = x - ancho/2, y = y - altoBase + i * alturaTabla),
                end = Offset(x = x + ancho/2, y = y - altoBase + i * alturaTabla),
                strokeWidth = 2f * escala
            )
        }

        // postes verticales en los laterales para reforzar visualmente la estructura visual
        drawRect(color = colorMaderaOscura, topLeft = Offset(x = x - ancho/2, y = y - altoBase), size = Size(width = 10f * escala, height = altoBase))
        drawRect(color = colorMaderaOscura, topLeft = Offset(x = x + ancho/2 - 10f * escala, y = y - altoBase), size = Size(width = 10f * escala, height = altoBase))

        // tejado triangular
        val pathTejado = Path().apply {
            moveTo(x = x - ancho/2 - 25f * escala, y = y - altoBase)  // esquina izquierda
            lineTo(x, y = y - altoBase - altoTejado)                // vértice superior (cima)
            lineTo(x = x + ancho/2 + 25f*escala, y = y - altoBase)  // esquina derecha
            close()
        }
        drawPath(path = pathTejado, color = colorTejado)

        // variables para la chimenea
        val chimX     = x + ancho * 0.20f
        val anchoChim = 18f * escala
        val altoChim  = 35f * escala
        val yPuntaTej = y - altoBase - altoTejado       // vértice del tejado
        val yBaseChim = yPuntaTej + altoTejado * 0.40f  // punto de apoyo de la chimenea

        // cuerpo principal de ladrillo
        drawRect(
            color   = colorChimenea,
            topLeft = Offset(x = chimX - anchoChim/2, y = yBaseChim - altoChim),
            size    = Size(width = anchoChim, height = altoChim)
        )

        // borde izquierdo más oscuro para simular profundidad/volumen
        drawRect(
            color   = colorChimeneaOscura,
            topLeft = Offset(chimX - anchoChim/2, yBaseChim - altoChim),
            size    = Size(4f * escala, altoChim)
        )

        // capuchón superior: más ancho que el cuerpo para coronar la chimenea
        drawRect(
            color   = colorChimeneaOscura,
            topLeft = Offset(chimX - anchoChim/2 - 3f*escala, yBaseChim - altoChim - 5f*escala),
            size    = Size(anchoChim + 6f*escala, 5f*escala)
        )

        // variables para el humo animado (volutas de humo)
        val bocaHumoX = chimX
        val bocaHumoY = yBaseChim - altoChim - 5f * escala  // borde superior del capuchón

        repeat(3) { indice ->
            val progresoVol = ((progresoHumo + indice / 3f) % 1f) // desfase de 1/3 por cada voluta para escalonarlas temporalmente

            val desplY = progresoVol * 75f * escala        // distancia vertical de subida
            val radio = (5f + progresoVol * 14f) * escala  // radio de crecimiento al subir
            val oscilaX = kotlin.math.sin(progresoVol * Math.PI * 2.5).toFloat() * 7f * escala // oscilación sinusoidal horizontal (imita la ondulación del humo)
            val alfa = (1f - progresoVol).coerceIn(0f, 0.65f)  // alpha de las volutas de humo (se va desvaneciendo al subir)

            val colorHumo = when (momentoDelDia) {
                MomentoDelDia.MANANA -> Color(0xFFB0BEC5) // gris azulado
                MomentoDelDia.TARDE  -> Color(0xFF9E9E9E) // gris medio
                MomentoDelDia.NOCHE  -> Color(0xFF757575) // gris visible sobre el cielo oscuro
            }

            drawCircle(
                color  = colorHumo.copy(alpha = alfa),
                radius = radio,
                center = Offset(bocaHumoX + oscilaX, bocaHumoY - desplY)
            )
        }

        // puerta de la cabaña
        drawRect(color = colorMaderaOscura, topLeft = Offset(x = x - 25f*escala, y = y - 55f*escala), size = Size(width = 50f*escala, height = 55f*escala))

        // pomo de la puerta
        drawCircle(color = Color(0xFFFFD54F), radius = 4f*escala, center = Offset(x + 10f*escala, y - 25f*escala))

        // variables para las ventanas
        val ventanaY     = y - 85f * escala
        val ventanaAncho = 38f * escala
        val ventanaAlto  = 38f * escala
        val mitadVY      = ventanaY + ventanaAlto / 2
        val espesorCruz  = 2f * escala

        // ventana izquierda
        val xVI = x - ancho/2 + 22f * escala
        drawRect(color = colorVentana, topLeft = Offset(xVI, ventanaY), size = Size(ventanaAncho, ventanaAlto))
        // cruz: travesaño horizontal
        drawLine(color = colorMaderaOscura, start = Offset(xVI, mitadVY), end = Offset(xVI + ventanaAncho, mitadVY), strokeWidth = espesorCruz)
        // cruz: palo vertical
        drawLine(color = colorMaderaOscura, start = Offset(xVI + ventanaAncho/2, ventanaY), end = Offset(xVI + ventanaAncho/2, ventanaY + ventanaAlto), strokeWidth = espesorCruz)

        // ventana derecha: 30*escala desde el centro de la cabaña hacia la derecha
        val xVD = x + 30f * escala
        drawRect(color = colorVentana, topLeft = Offset(xVD, ventanaY), size = Size(ventanaAncho, ventanaAlto))
        drawLine(color = colorMaderaOscura, start = Offset(xVD, mitadVY), end = Offset(xVD + ventanaAncho, mitadVY), strokeWidth = espesorCruz)
        drawLine(color = colorMaderaOscura, start = Offset(xVD + ventanaAncho/2, ventanaY), end = Offset(xVD + ventanaAncho/2, ventanaY + ventanaAlto), strokeWidth = espesorCruz)
    }
}

// función auxiliar para dibujar el camino
private fun dibujarCamino(drawScope: DrawScope, anchoCanvas: Float, altoCanvas: Float, yInicio: Float, momento: MomentoDelDia) {
    with(drawScope) {
        val color = if (momento == MomentoDelDia.NOCHE) Color(0xFF455A64) else Color(0xFFF4A460)
        drawPath(Path().apply {
            moveTo(anchoCanvas * 0.85f, yInicio)
            cubicTo(anchoCanvas * 0.80f, yInicio + 100f, anchoCanvas * 0.60f, altoCanvas * 0.80f, anchoCanvas * 0.55f, altoCanvas)
            lineTo(anchoCanvas * 0.40f, altoCanvas)
            cubicTo(anchoCanvas * 0.45f, altoCanvas * 0.80f, anchoCanvas * 0.75f, yInicio + 100f, anchoCanvas * 0.80f, yInicio)
            close()
        }, color = color.copy(alpha = 0.8f))
    }
}

// función auxiliar para dibujar el sol
private fun dibujarSol(drawScope: DrawScope, centro: Offset, radio: Float, rotacion: Float, colorSol: Color, colorRayos: Color) {
    with(drawScope) {
        drawCircle(color = colorSol.copy(alpha = 0.15f), radius = radio * 1.6f,  center = centro)
        drawCircle(color = colorSol.copy(alpha = 0.25f), radius = radio * 1.35f, center = centro)
        drawCircle(color = colorSol, radius = radio, center = centro)
        rotate(degrees = rotacion, pivot = centro) {
            repeat(12) { i ->
                val a = Math.toRadians((360.0 / 12) * i).toFloat()
                val iX = centro.x + (radio + radio*0.18f) * kotlin.math.cos(a)
                val iY = centro.y + (radio + radio*0.18f) * kotlin.math.sin(a)
                val fX = iX + radio * 0.65f * kotlin.math.cos(a)
                val fY = iY + radio * 0.65f * kotlin.math.sin(a)
                drawLine(color = colorRayos, start = Offset(iX, iY), end = Offset(fX, fY), strokeWidth = 5f)
            }
        }
    }
}

// función auxiliar para dibujar la luna
private fun dibujarLuna(drawScope: DrawScope, centro: Offset, radio: Float, colorFondo: Color) {
    with(drawScope) {
        drawCircle(color = Color(0xFFFFF8DC).copy(alpha = 0.12f), radius = radio * 1.45f, center = centro)
        drawCircle(color = Color(0xFFFFF8DC), radius = radio, center = centro)
        drawCircle(color = colorFondo, radius = radio * 0.88f, center = Offset(centro.x + radio * 0.60f, centro.y - radio * 0.08f))
    }
}

// función auxiliar para dibujar las nubes
private fun dibujarNube(drawScope: DrawScope, centroX: Float, centroY: Float, escala: Float, colorNube: Color) {
    with(drawScope) {
        drawOval(color = colorNube, topLeft = Offset(centroX - 80f*escala, centroY - 30f*escala), size = Size(160f*escala, 70f*escala))
        drawOval(color = colorNube, topLeft = Offset(centroX - 65f*escala, centroY - 55f*escala), size = Size(90f*escala,  60f*escala))
        drawOval(color = colorNube, topLeft = Offset(centroX - 10f*escala, centroY - 50f*escala), size = Size(110f*escala, 65f*escala))
    }
}

// función auxiliar para dibujar las estrellas
private fun dibujarEstrella(drawScope: DrawScope, centroX: Float, centroY: Float, radio: Float, alfa: Float) {
    with(drawScope) {
        drawPath(Path().apply {
            moveTo(centroX, centroY - radio)
            lineTo(centroX + radio * 0.3f, centroY - radio * 0.3f); lineTo(centroX + radio, centroY)
            lineTo(centroX + radio * 0.3f, centroY + radio * 0.3f); lineTo(centroX, centroY + radio)
            lineTo(centroX - radio * 0.3f, centroY + radio * 0.3f); lineTo(centroX - radio, centroY)
            lineTo(centroX - radio * 0.3f, centroY - radio * 0.3f); close()
        }, color = Color.White.copy(alpha = alfa))
    }
}

// función auxiliar para dibujar los arbustos
private fun dibujarArbusto(drawScope: DrawScope, centroX: Float, centroY: Float, escala: Float, colorArbusto: Color) {
    with(drawScope) {
        val r = 30f * escala
        drawCircle(color = colorArbusto, radius = r * 0.8f, center = Offset(centroX - r*0.5f, centroY - r*0.3f))
        drawCircle(color = colorArbusto, radius = r * 0.8f, center = Offset(centroX + r*0.5f, centroY - r*0.3f))
        drawCircle(color = colorArbusto, radius = r,         center = Offset(centroX,           centroY - r*0.6f))
    }
}

// función auxiliar para dibujar el césped
private fun dibujarCesped(drawScope: DrawScope, centroX: Float, centroY: Float, colorCesped: Color) {
    with(drawScope) {
        repeat(3) { i ->
            val a = Math.toRadians((i - 1) * 20.0).toFloat()
            val s = Offset(centroX + (i - 1) * 5f, centroY)
            val e = Offset(s.x + 15f * kotlin.math.sin(a), s.y - 20f * kotlin.math.cos(a))
            drawLine(color = colorCesped, start = s, end = e, strokeWidth = 5f)
        }
    }
}

// función auxiliar para cargar el fondo dinámico
@Composable
fun FondoDinamico(momento: MomentoDelDia) {

    val transicionInfinita = rememberInfiniteTransition(label = "fondo")

    val rotacionSol by transicionInfinita.animateFloat(
        initialValue  = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(12_000, easing = LinearEasing)),
        label = "rotacionSol"
    )

    val progresoNubeA by transicionInfinita.animateFloat(
        initialValue = 0.00f, targetValue = 1.00f,
        animationSpec = infiniteRepeatable(tween(6_500, easing = LinearEasing), RepeatMode.Restart),
        label = "nubeA"
    )
    val progresoNubeB by transicionInfinita.animateFloat(
        initialValue = 0.40f, targetValue = 1.40f,
        animationSpec = infiniteRepeatable(tween(11_000, easing = LinearEasing), RepeatMode.Restart),
        label = "nubeB"
    )
    val progresoNubeC by transicionInfinita.animateFloat(
        initialValue = 0.70f, targetValue = 1.70f,
        animationSpec = infiniteRepeatable(tween(8_500, easing = LinearEasing), RepeatMode.Restart),
        label = "nubeC"
    )
    val progresoNubeD by transicionInfinita.animateFloat(
        initialValue = 0.20f, targetValue = 1.20f,
        animationSpec = infiniteRepeatable(tween(14_000, easing = LinearEasing), RepeatMode.Restart),
        label = "nubeD"
    )
    val progresoNubeE by transicionInfinita.animateFloat(
        initialValue = 0.55f, targetValue = 1.55f,
        animationSpec = infiniteRepeatable(tween(9_000, easing = LinearEasing), RepeatMode.Restart),
        label = "nubeE"
    )

    val alfaEstrellas by transicionInfinita.animateFloat(
        initialValue = 0.2f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2_500
                0.2f at 0    using LinearEasing
                1.0f at 800  using LinearEasing
                0.6f at 1400 using LinearEasing
                1.0f at 2000 using LinearEasing
                0.2f at 2500 using LinearEasing
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "estrellas"
    )

    // Humo: progreso 0->1 con Restart para subida continua de volutas
    val progresoHumo by transicionInfinita.animateFloat(
        initialValue  = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3_000, easing = LinearEasing), RepeatMode.Restart),
        label = "humo"
    )

    val colorCielo: Color = when (momento) {
        MomentoDelDia.MANANA -> Color(0xFF58C4F1)
        MomentoDelDia.TARDE  -> Color(0xFF777AAB)
        MomentoDelDia.NOCHE  -> Color(0xFF0A0E27)
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val anchoCanvas = size.width
        val altoCanvas  = size.height
        val yHorizonte  = altoCanvas * 0.60f

        // 1. Cielo
        drawRect(color = colorCielo, size = size)

        // 2. Astro
        when (momento) {
            MomentoDelDia.MANANA -> dibujarSol(
                this, Offset(anchoCanvas * 0.78f, altoCanvas * 0.14f),
                52f, rotacionSol, Color(0xFFFFD700), Color(0xFFFFA500)
            )
            MomentoDelDia.TARDE -> {
                val centro = Offset(anchoCanvas * 0.63f, altoCanvas * 0.30f)
                clipRect(bottom = centro.y) {
                    dibujarSol(this, centro, 67f, rotacionSol, Color(0xFFFFC030), Color(0xFFFFD447))
                }
            }
            MomentoDelDia.NOCHE -> dibujarLuna(
                this, Offset(anchoCanvas * 0.75f, altoCanvas * 0.15f), 62f, Color(0xFF0A0E27)
            )
        }

        // 3. Nubes / estrellas
        when (momento) {
            MomentoDelDia.MANANA, MomentoDelDia.TARDE -> {
                val esTarde = momento == MomentoDelDia.TARDE
                val c1 = if (esTarde) Color(0xFFFFE4B5) else Color.White
                val c2 = if (esTarde) Color(0xFFFFD59E) else Color(0xFFF0F8FF)
                val c3 = if (esTarde) Color(0xFFFFCC80) else Color.White
                val c4 = if (esTarde) Color(0xFFFFB74D) else Color(0xFFE8F4FD)
                val c5 = if (esTarde) Color(0xFFFFF3E0) else Color(0xFFF5FBFF)

                fun posX(p: Float, aN: Float) = -aN + (p % 1f) * (anchoCanvas + aN * 2f)

                dibujarNube(this, posX(progresoNubeA, 200f), altoCanvas * 0.10f, 1.2f,  c1)
                dibujarNube(this, posX(progresoNubeB, 160f), altoCanvas * 0.22f, 0.85f, c2)
                dibujarNube(this, posX(progresoNubeC, 130f), altoCanvas * 0.04f, 0.65f, c3)
                dibujarNube(this, posX(progresoNubeD, 180f), altoCanvas * 0.36f, 1.0f,  c4)
                dibujarNube(this, posX(progresoNubeE, 140f), altoCanvas * 0.48f, 0.75f, c5)
            }
            MomentoDelDia.NOCHE -> {
                val pos = listOf(
                    0.10f to 0.08f, 0.25f to 0.05f, 0.40f to 0.12f,
                    0.55f to 0.04f, 0.68f to 0.10f, 0.82f to 0.06f,
                    0.15f to 0.20f, 0.35f to 0.18f, 0.50f to 0.22f,
                    0.72f to 0.24f, 0.88f to 0.17f, 0.05f to 0.30f,
                    0.30f to 0.32f, 0.60f to 0.28f, 0.90f to 0.33f,
                    0.18f to 0.42f, 0.45f to 0.38f, 0.78f to 0.40f,
                    0.08f to 0.48f, 0.38f to 0.50f, 0.65f to 0.46f,
                    0.85f to 0.52f, 0.22f to 0.54f, 0.55f to 0.56f
                )
                pos.forEachIndexed { i, (px, py) ->
                    val alfa = ((alfaEstrellas + i * 0.15f) % 1.0f).coerceIn(0.1f, 1.0f)
                    dibujarEstrella(this, anchoCanvas * px, altoCanvas * py, 6f + (i % 4) * 2f, alfa)
                }
            }
        }

        // 4. Montanas
        val colorML = when (momento) {
            MomentoDelDia.MANANA -> Color(0xFF7FB3C8)
            MomentoDelDia.TARDE  -> Color(0xFFB8724A)
            MomentoDelDia.NOCHE  -> Color(0xFF1A2540)
        }
        val colorMC = when (momento) {
            MomentoDelDia.MANANA -> Color(0xFF4A8FA8)
            MomentoDelDia.TARDE  -> Color(0xFF7A4A28)
            MomentoDelDia.NOCHE  -> Color(0xFF0F1A2E)
        }
        drawPath(Path().apply {
            moveTo(0f, yHorizonte)
            cubicTo(anchoCanvas*0.15f, altoCanvas*0.22f, anchoCanvas*0.35f, altoCanvas*0.26f, anchoCanvas*0.50f, altoCanvas*0.30f)
            cubicTo(anchoCanvas*0.65f, altoCanvas*0.34f, anchoCanvas*0.85f, altoCanvas*0.18f, anchoCanvas*1.00f, altoCanvas*0.22f)
            lineTo(anchoCanvas, yHorizonte); close()
        }, color = colorML)
        drawPath(Path().apply {
            moveTo(0f, yHorizonte)
            cubicTo(anchoCanvas*0.10f, altoCanvas*0.38f, anchoCanvas*0.25f, altoCanvas*0.42f, anchoCanvas*0.40f, altoCanvas*0.32f)
            cubicTo(anchoCanvas*0.55f, altoCanvas*0.22f, anchoCanvas*0.70f, altoCanvas*0.38f, anchoCanvas*0.85f, altoCanvas*0.28f)
            cubicTo(anchoCanvas*0.92f, altoCanvas*0.22f, anchoCanvas*0.98f, altoCanvas*0.32f, anchoCanvas*1.00f, altoCanvas*0.38f)
            lineTo(anchoCanvas, yHorizonte); close()
        }, color = colorMC)

        // 5. Suelo
        val colorSuelo = when (momento) {
            MomentoDelDia.MANANA -> Color(0xFF68D068)
            MomentoDelDia.TARDE  -> Color(0xFF8B6914)
            MomentoDelDia.NOCHE  -> Color(0xFF102234)
        }
        drawPath(Path().apply {
            moveTo(0f, altoCanvas); lineTo(0f, yHorizonte)
            cubicTo(anchoCanvas*0.25f, yHorizonte - altoCanvas*0.05f, anchoCanvas*0.75f, yHorizonte + altoCanvas*0.03f, anchoCanvas, yHorizonte - altoCanvas*0.03f)
            lineTo(anchoCanvas, altoCanvas); close()
        }, color = colorSuelo)

        // 5.5 Camino
        dibujarCamino(this, anchoCanvas, altoCanvas, yHorizonte + 20f, momento)

        // 5.6 Cabana con chimenea y humo animado
        dibujarCabana(
            drawScope    = this,
            x            = anchoCanvas * 0.83f,
            y            = yHorizonte + 20f,
            escala       = 0.8f,
            momentoDelDia  = momento,
            progresoHumo = progresoHumo
        )

        dibujarArbol(
            drawScope = this,
            x         = anchoCanvas * 0.94f, // Posicionado a la derecha de la cabaña
            y         = yHorizonte + 35f,    // Un poco más abajo para dar perspectiva
            escala    = 1.1f,                // Un poco más grande para que destaque
            momento   = momento
        )


        // 6. Detalles suelo
        val cD1 = when (momento) {
            MomentoDelDia.MANANA -> Color(0xFF2E8B57)
            MomentoDelDia.TARDE  -> Color(0xFFCD853F)
            MomentoDelDia.NOCHE  -> Color(0xFF233B57)
        }
        val cD2 = when (momento) {
            MomentoDelDia.MANANA -> Color(0xFF228B22)
            MomentoDelDia.TARDE  -> Color(0xFFD2691E)
            MomentoDelDia.NOCHE  -> Color(0xFF233B57)
        }
        dibujarArbusto(this, anchoCanvas*0.10f, altoCanvas*0.85f, 0.9f, cD1)
        dibujarCesped(this,  anchoCanvas*0.05f, altoCanvas*0.90f, cD2)
        dibujarCesped(this,  anchoCanvas*0.15f, altoCanvas*0.88f, cD2)
        dibujarArbusto(this, anchoCanvas*0.90f, altoCanvas*0.82f, 0.7f, cD1)
        dibujarArbusto(this, anchoCanvas*0.85f, altoCanvas*0.88f, 1.0f, cD1)
        dibujarCesped(this,  anchoCanvas*0.95f, altoCanvas*0.85f, cD2)
        dibujarCesped(this,  anchoCanvas*0.80f, altoCanvas*0.92f, cD2)
        dibujarCesped(this,  anchoCanvas*0.40f, altoCanvas*0.90f, cD2)
        dibujarCesped(this,  anchoCanvas*0.30f, altoCanvas*0.93f, cD2)
        dibujarCesped(this,  anchoCanvas*0.65f, altoCanvas*0.89f, cD2)
    }
}