package com.juandeherrera.letskody.screens.juegos.euroBanderas

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.juandeherrera.letskody.clasesAuxiliares.ResultadoJuego

// ═════════════════════════════════════════════════════════════════════════════
// COLORES DEL JUEGO
//
// Se definen aquí como constantes privadas del archivo para que todos los
// componentes usen exactamente los mismos colores sin repetir el código hex.
// Son privados porque solo se usan dentro de este archivo.
// ═════════════════════════════════════════════════════════════════════════════
private val ColorFondo       = Color(0xFFC2DAFD)  // azul claro del fondo general
private val ColorCorrecto    = Color(0xFF2E7D32)  // verde oscuro: respuesta correcta
private val ColorIncorrecto  = Color(0xFFC62828)  // rojo oscuro: respuesta incorrecta
private val ColorNeutro      = Color(0xFF1565C0)  // azul oscuro: botones sin responder y elementos de UI
private val ColorNeutroClaro = Color(0xFFE3F2FD)  // azul muy claro: fondo de filas de resultado
private val ColorTexto       = Color(0xFF0D1B2A)  // casi negro: texto principal
private val ColorBlanco      = Color.White


// ═════════════════════════════════════════════════════════════════════════════
// CABECERA DEL JUEGO
//
// Barra superior visible durante la partida que muestra:
//   · Cronómetro (esquina izquierda)
//   · Número de pregunta actual (centro)
//   · Puntuación acumulada (esquina derecha)
//   · Barra de progreso (debajo de todo lo anterior)
// ═════════════════════════════════════════════════════════════════════════════
@SuppressLint("DefaultLocale")
@Composable
fun CabeceraJuego(
    segundos         : Int,        // segundos totales del cronómetro (se convierten a mm:ss)
    numeroPregunta   : Int,        // pregunta actual (1..12)
    totalPreguntas   : Int,        // total de preguntas (12)
    puntos           : Int,        // puntuación actual del jugador
    fuenteTipografica: FontFamily, // fuente personalizada de la app
    modifier         : Modifier = Modifier
) {
    // Convertir los segundos totales a minutos y segundos para mostrarlos como mm:ss
    val minutos  = segundos / 60
    val segs     = segundos % 60

    // Calcular el progreso de la barra (valor entre 0.0f y 1.0f)
    val progreso = numeroPregunta.toFloat() / totalPreguntas.toFloat()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ColorFondo)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            // ── Cronómetro ────────────────────────────────────────────────────
            // String.format con "%02d" garantiza que siempre se muestren 2 dígitos (01, 09, 10...)
            Card(
                colors = CardDefaults.cardColors(containerColor = ColorNeutro),
                shape  = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text       = String.format("%02d:%02d", minutos, segs),
                    fontFamily = fuenteTipografica,
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color      = ColorBlanco,
                    modifier   = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            // ── Número de pregunta ────────────────────────────────────────────
            Text(
                text       = "Pregunta $numeroPregunta / $totalPreguntas",
                fontFamily = fuenteTipografica,
                fontSize   = 16.sp,
                color      = ColorTexto,
                fontWeight = FontWeight.SemiBold
            )

            // ── Puntos ────────────────────────────────────────────────────────
            Card(
                colors = CardDefaults.cardColors(containerColor = ColorNeutro),
                shape  = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text       = "⭐ $puntos",
                    fontFamily = fuenteTipografica,
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color      = ColorBlanco,
                    modifier   = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ── Barra de progreso ─────────────────────────────────────────────────
        // Muestra visualmente cuántas preguntas quedan. Se rellena de izquierda
        // a derecha conforme avanza el jugador.
        LinearProgressIndicator(
            progress   = { progreso },
            modifier   = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color      = ColorNeutro,      // color de la parte rellena
            trackColor = ColorNeutroClaro  // color de la parte vacía
        )
    }
}


// ═════════════════════════════════════════════════════════════════════════════
// IMAGEN DE LA BANDERA
//
// Muestra la imagen de la bandera cargándola desde su URL remota con Coil.
// ContentScale.Fit asegura que la imagen se vea completa sin recortes,
// respetando sus proporciones originales.
// ═════════════════════════════════════════════════════════════════════════════
@Composable
fun ImagenBandera(
    urlBandera: String,   // URL de la imagen almacenada en Firebase Storage
    modifier  : Modifier = Modifier
) {
    Card(
        modifier  = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape     = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)  // sombra para destacarla
    ) {
        // AsyncImage (Coil): descarga y muestra la imagen de forma asíncrona
        AsyncImage(
            model              = urlBandera,
            contentDescription = "Bandera",
            contentScale       = ContentScale.Crop,  // imagen completa sin recortes
            modifier           = Modifier
                .fillMaxWidth()
                .height(210.dp)
        )
    }
}


// ═════════════════════════════════════════════════════════════════════════════
// BOTÓN DE OPCIÓN
//
// Cada uno de los 4 botones de respuesta. Su comportamiento cambia según
// si el usuario ya respondió o no:
//
//   · Sin responder  → azul neutro, habilitado
//   · Después de responder:
//       - Opción correcta              → verde ✅
//       - Opción seleccionada incorrecta → rojo ❌
//       - Las otras dos opciones        → azul semitransparente (deshabilitadas visualmente)
//
// El cambio de color se anima suavemente con animateColorAsState (400ms).
// ═════════════════════════════════════════════════════════════════════════════
@Composable
fun BotonOpcion(
    texto                : String,   // texto de esta opción de respuesta
    respuestaSeleccionada: String?,  // lo que el usuario pulsó (null si aún no respondió)
    opcionCorrecta       : String,   // la respuesta correcta (para saber si colorear en verde)
    fuenteTipografica    : FontFamily,
    onClick              : () -> Unit  // acción al pulsar (envía la respuesta al ViewModel)
) {
    // LOG TEMPORAL para debug
    android.util.Log.d("EUROBANDERAS", "BotonOpcion '$texto' — respuestaSeleccionada = $respuestaSeleccionada")


    // true si el usuario ya eligió una opción en esta pregunta
    val respondido = respuestaSeleccionada != null

    // Animación del color de fondo del botón.
    // animateColorAsState interpola suavemente entre el color anterior y el nuevo
    // cuando cambia targetValue (en este caso, al responder).
    val colorFondo by animateColorAsState(
        targetValue = when {
            !respondido -> ColorNeutro

            texto == opcionCorrecta -> ColorCorrecto

            texto == respuestaSeleccionada && texto != opcionCorrecta -> ColorIncorrecto

            else -> ColorNeutro.copy(alpha = 0.3f) // 👈 más suave
        },
        animationSpec = tween(400),
        label = "colorBoton"
    )

    val colorTexto = if (respondido && texto != opcionCorrecta && texto != respuestaSeleccionada) {
        Color.White.copy(alpha = 0.7f)
    } else {
        Color.White
    }



    Button(
        onClick  = { if (!respondido) onClick() },   // solo reacciona si aún no se respondió
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        colors   = ButtonDefaults.buttonColors(containerColor = colorFondo),
        shape    = RoundedCornerShape(14.dp),
        //enabled  = !respondido   // deshabilitado visualmente tras responder
    ) {
        Text(
            text       = texto,
            fontFamily = fuenteTipografica,
            fontSize   = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorTexto,
            textAlign  = TextAlign.Center,
            maxLines   = 2
        )
    }
}
