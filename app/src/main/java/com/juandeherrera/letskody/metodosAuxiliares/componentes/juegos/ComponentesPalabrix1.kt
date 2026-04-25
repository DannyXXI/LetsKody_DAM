package com.juandeherrera.letskody.metodosAuxiliares.componentes.juegos

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.juandeherrera.letskody.R
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.formatearSegundos
import com.juandeherrera.letskody.viewModels.palabrix1.EstadoPalabrix1

// función auxiliar para cargar la parte superior de la pantalla del juego (cronometro y puntuación)
@Composable
fun CabeceraJuegoPalabrix1(segundosCronometro: Int, numeroPregunta: Int, preguntasTotales: Int, puntosActuales: Int, fuenteTipografica: FontFamily) {
    // columna con la parte superior del juego
    Column(
        modifier = Modifier.fillMaxWidth() // se ocupa el ancho disponible
            .padding(horizontal = 16.dp, vertical = 8.dp)  // padding interno
    ){
        // contenido de la columna (fila)
        Row(
            modifier = Modifier.fillMaxWidth(),  // se ocupa el ancho disponible
            horizontalArrangement = Arrangement.SpaceBetween,  // espaciado horizontal entre elementos
            verticalAlignment = Alignment.CenterVertically     // centrado vertical
        ){
            // tarjeta con el cronómetro
            ElevatedCard(
                shape = RoundedCornerShape(size = 12.dp),                              // bordes redondeados
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0))   // color de fondo de la tarjeta
            ){
                Text(
                    text = formatearSegundos(segundos = segundosCronometro),   // texto
                    color = Color.White,       // color del texto
                    style = TextStyle(
                        fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                        fontSize = 18.sp,                // tamaño del texto
                        fontWeight = FontWeight.Bold    // texto en negrita
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp) // padding interno
                )
            }

            // texto indicando el número de pregunta actual
            Text(
                text = "Pregunta $numeroPregunta / $preguntasTotales",   // texto
                color = Color.Black,       // color del texto
                style = TextStyle(
                    fontFamily = fuenteTipografica,    // fuente tipográfica del texto
                    fontSize = 16.sp,                  // tamaño del texto
                    fontWeight = FontWeight.SemiBold   // texto en negrita
                )
            )

            // tarjeta con la puntuación
            ElevatedCard(
                shape = RoundedCornerShape(size = 12.dp),                              // bordes redondeados
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0))   // color de fondo de la tarjeta
            ){
                Text(
                    text = "⭐ $puntosActuales",   // texto
                    color = Color.White,       // color del texto
                    style = TextStyle(
                        fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                        fontSize = 18.sp,                // tamaño del texto
                        fontWeight = FontWeight.Bold    // texto en negrita
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp) // padding interno
                )
            }
        }
    }
}

// función auxiliar para cargar la palabra a clasificar
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun TarjetaPalabraPalabrix1(palabra: String, fuenteTipografica: FontFamily) {
    // tarjeta que contiene la palabra a clasificar
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()  // se ocupa el ancho disponible
            .padding(horizontal = 24.dp)    // padding en los laterales horizontales
            .height(200.dp),                // altura de la tarjeta
        shape = RoundedCornerShape(size = 12.dp),  // bordes redondeados
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)  // sombreado de elevación
    ){
        // contenedor interno de la tarjeta
        Box(
            modifier = Modifier.fillMaxSize()  // ocupa el espacio disponible
        ){
            // imagen de fondo
            Image(
                painter = painterResource(id = R.drawable.papel),  // ruta al recurso (imagen)
                contentDescription = "Papel",                      // descripción de la imagen
                contentScale = ContentScale.Crop,                  // forma de escalar la imagen a la tarjeta contenedora
                modifier = Modifier.matchParentSize()              // la imagen tiene el mismo tamaño que el elemento padre (tarjeta)
            )

            // contenedor con la palabra
            Box(
                modifier = Modifier.fillMaxSize(),   // ocupa el espacio disponible
                contentAlignment = Alignment.Center  // contenido alineado en el centro
            ){
                // PALABRA
                Text(
                    text = palabra,   // texto
                    color = Color.Black,       // color del texto
                    style = TextStyle(
                        fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                        fontSize = 36.sp,                // tamaño del texto
                        fontWeight = FontWeight.Bold,    // texto en negrita
                        textAlign = TextAlign.Center     // texto centrado
                    )
                )
            }
        }
    }
}

// función auxiliar para cargar cada uno de los cuatro botones con las opciones de respuesta
@Composable
fun BotonOpcionPalabrix1 (texto: String, opcionSeleccionada: String?, opcionCorrecta: String, fuenteTipografica: FontFamily, pulsar: () -> Unit) {

    val opcionRespondida = opcionSeleccionada != null // se comprueba si la opcion ha sido seleccionada

    // variable para el color de fondo del botón (que intercambia el color según su estado)
    val colorFondo by animateColorAsState(
        targetValue = when {
            !opcionRespondida -> Color(0xFF1565C0)  // si no se ha seleccionado el botón

            texto == opcionCorrecta -> Color(0xFF2E7D32) // si es la opción correcta

            texto == opcionSeleccionada && texto != opcionCorrecta -> Color(0xFFC62828)  // si es la opción incorrecta

            else -> Color(0xFF1565C0).copy(alpha = 0.3f)  // si es el resto de opciones no seleccionadas
        },
        animationSpec = tween(durationMillis = 400),  // duración de la animación
        label = "colorBoton"
    )

    // variable para el color del texto del botón
    val colorTexto = if (opcionRespondida && texto != opcionCorrecta && texto != opcionSeleccionada) {
        Color.White.copy(alpha = 0.7f)
    } else {
        Color.White
    }

    // BOTÓN DE OPCIÓN
    Button(
        onClick = { if (!opcionRespondida) pulsar() }, // se reacciona a la pulsación si aún no se ha respondido
        modifier = Modifier.fillMaxWidth()             // se ocupa el ancho disponible
            .height(58.dp),                            // altura del botón
        shape = RoundedCornerShape(size = 14.dp),      // bordes redondeados
        colors = ButtonDefaults.buttonColors(
            containerColor = colorFondo,          // color de fondo del botón
            contentColor = colorTexto             // color del texto del botón
        )
    ){
        Text(
            text = texto,                         // texto del botón
            style = TextStyle(
                fontFamily = fuenteTipografica,   // fuente tipográfica del texto
                fontSize = 18.sp,                 // tamaño de fuente del texto
                fontWeight = FontWeight.Bold,     // texto en negrita
                textAlign = TextAlign.Center      // alinear en el centro
            )
        )
    }
}

// función auxiliar que agrupa la pantalla del juego mientras el usuario está jugándolo
@Composable
fun PantallaJugandoPalabrix1(estado: EstadoPalabrix1.Jugando, fuenteTipografica: FontFamily, respuesta: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),  // ocupa el espacio disponible
        horizontalAlignment = Alignment.CenterHorizontally,  // centrado horizontal
        verticalArrangement = Arrangement.spacedBy(20.dp)    // espaciado vertical
    ){
        val opciones = listOf("Sustantivo", "Adjetivo", "Adverbio", "Verbo")  // opciones fijas

        // cronómetro y puntuación
        CabeceraJuegoPalabrix1(
            segundosCronometro = estado.tiempoCronometro,
            numeroPregunta = estado.numeroPregunta,
            preguntasTotales = 12,
            puntosActuales = estado.puntos,
            fuenteTipografica = fuenteTipografica
        )

        // tarjeta con la palabra a clasificar
        TarjetaPalabraPalabrix1(palabra = estado.preguntaActual.palabra, fuenteTipografica = fuenteTipografica)

        Spacer(modifier = Modifier.height(8.dp))  // separación vertical entre componentes

        // columna que contiene a las cuatro opciones
        Column(
            modifier = Modifier.padding(horizontal = 24.dp),   // padding en los laterales horizontales
            verticalArrangement = Arrangement.spacedBy(10.dp)  // espaciado vertical
        ){
            // se recorre la lista de opciones para cargarlas en cada botón
            opciones.forEach { opcion ->
                BotonOpcionPalabrix1(
                    texto = opcion,
                    opcionSeleccionada = estado.respuestaSeleccionada,  // nulo si no ha respondido todavía
                    opcionCorrecta = estado.opcionCorrecta,
                    fuenteTipografica = fuenteTipografica,
                    pulsar = { respuesta(opcion) }
                )
            }
        }
    }
}