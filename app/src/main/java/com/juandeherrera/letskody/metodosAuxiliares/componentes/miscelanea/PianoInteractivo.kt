package com.juandeherrera.letskody.metodosAuxiliares.componentes.miscelanea

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.juandeherrera.letskody.clasesAuxiliares.TECLAS_PIANO
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.miscelanea.prepararAudio
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.miscelanea.reproducirNota
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.miscelanea.teclaEnPosicion
import kotlin.collections.component1
import kotlin.collections.component2

// función auxiliar para cargar el piano interactivo en la pantalla
@Composable
fun PianoInteractivo(fuenteTipografica: FontFamily) {
    // bloque que se ejecuta una sola vez al cargar la pantalla
    LaunchedEffect(key1 = Unit) {
        prepararAudio() // se prepara el audio de todas las teclas del piano un hilo de baja prioridad
    }

    // variable de estado que guarda las teclas que hayan sido pulsadas
    val teclasPulsadas = remember { mutableStateOf(value = setOf<String>()) }

    // variables para agrupar las teclas blancas y las teclas negras
    val teclasBlancas = TECLAS_PIANO.filter { !it.esNegra }
    val teclasNegras  = TECLAS_PIANO.filter {  it.esNegra }

    // variable para el mapa de posición de las teclas negras
    val indicePrevioBlanca: Map<String, Int> = buildMap {
        var contBlancas = -1
        TECLAS_PIANO.forEach { tecla ->
            if (!tecla.esNegra) contBlancas++
            else put(tecla.nombre, contBlancas)
        }
    }

    // variable para el tracking de teclas
    val teclaActivaPorPuntero = remember { mutableStateOf(value = mapOf<Long, String>()) }

    // dimensiones reales del piano (pixeles)
    var anchoPx by remember { mutableFloatStateOf(value = 0f) }
    var altoPx  by remember { mutableFloatStateOf(value = 0f) }

    // contenedor principal
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()  // se ocupa el espacio disponible
            .rotate(degrees = 180f)        // se rota 180º
            .background(Color(0xFF17172D), shape = RoundedCornerShape(size = 12.dp))  // fondo con bordes redondeados
            .padding(all = 4.dp)  // padding interno
    ){
        // dimensiones máximas del contenedor
        val totalAncho = maxWidth
        val totalAlto = maxHeight

        // Dimensiones de cada tecla en unidades Dp (para los Modifier de layout).

        // dimensiones de cada tecla
        val alturaBlanca = totalAlto  / teclasBlancas.size  // altura de tecla blanca
        val altaNegra = alturaBlanca * 0.62f    // altura de tecla negra (62% de tecla blanca)
        val anchaNegra = totalAncho  * 0.58f    // ancho de tecla negra (58% del ancho total)

        Box(
            modifier = Modifier.fillMaxSize() // se ocupa el espacio disponible
                // se actualiza el tamaño real del piano
                .onGloballyPositioned { coordenadas ->
                    anchoPx = coordenadas.size.width.toFloat()
                    altoPx  = coordenadas.size.height.toFloat()
                }
                // detector de los gestos del usuario (capa de glissando)
                .pointerInput(Unit) {
                    // se reinicia el detector tras cada secuencia
                    awaitPointerEventScope {
                        // bucle infinitos de eventos
                        while (true) {
                            val evento = awaitPointerEvent()  // se obtiene el evento táctil

                            // sole se permite eventos de movimiento de dedo o levantar el dedo
                            if (evento.type == PointerEventType.Move || evento.type == PointerEventType.Release) {

                                // se recorre el número de dedos
                                evento.changes.forEach { cambio ->
                                    val id = cambio.id.value

                                    // si se levanta el dedo
                                    if (!cambio.pressed) {
                                        val nombreAnterior = teclaActivaPorPuntero.value[id]  // se obtiene el nombre de la tecla anterior

                                        if (nombreAnterior != null) {
                                            teclaActivaPorPuntero.value -= id  // se elimina el dedo del mapa de tracking

                                            // se apaga la tecla si no hay otro dedo sobre ella
                                            val sigueActiva = teclaActivaPorPuntero.value.values.contains(nombreAnterior)
                                            if (!sigueActiva) {
                                                teclasPulsadas.value -= nombreAnterior
                                            }
                                        }
                                        return@forEach
                                    }

                                    val pos = cambio.position // posición del dedo

                                    // se determina la tecla que está en la posición actual del dedo
                                    val tecla = teclaEnPosicion(
                                        x = pos.x,
                                        y = pos.y,
                                        totalAlturaPx = altoPx,
                                        totalAnchoPx = anchoPx,
                                        teclasBlancas = teclasBlancas,
                                        teclasNegras = teclasNegras,
                                        indicePrevioBlanca = indicePrevioBlanca
                                    ) ?: return@forEach

                                    val nombreAnterior = teclaActivaPorPuntero.value[id]  // se obtiene el nombre de la tecla anterior

                                    // se comprueba si el dedo cambió de tecla para evitar disparar el sonido continuamente mientras permanece en la misma tecla
                                    if (tecla.nombre != nombreAnterior) {

                                        // se apaga la tecla anterior si no hay otro dedo que la mantenga presionada
                                        if (nombreAnterior != null) {
                                            val sigueActiva = teclaActivaPorPuntero.value.entries.any { (k, v) -> k != id && v == nombreAnterior }
                                            if (!sigueActiva) {
                                                teclasPulsadas.value -= nombreAnterior
                                            }
                                        }

                                        teclaActivaPorPuntero.value += (id to tecla.nombre) // se enciende la nueva tecla

                                        teclasPulsadas.value += tecla.nombre                // se actualiza el tracking

                                        reproducirNota(frecuencia = tecla.frecuencia)       // se dispara el sonido

                                        cambio.consume() // se consume el evento
                                    }
                                }
                            }
                        }
                    }
                }
        ){
            // se dibujan las teclas blancas
            teclasBlancas.forEachIndexed { idx, tecla ->
                val yOffset = alturaBlanca * idx  // desplazamiento vertical de esta tecla desde el borde superior

                val pulsada = teclasPulsadas.value.contains(tecla.nombre)  // se comprueba que la tecla esta pulsada

                // contenedor de la tecla
                Box(
                    modifier = Modifier.offset(y = yOffset) // posicionamiento vertical
                        .fillMaxWidth()   // se ocupa el espacio disponible
                        .height(alturaBlanca - 2.dp)  // separación de 2.dp entre teclas
                        .clip(shape = RoundedCornerShape(topEnd = 5.dp, bottomEnd = 5.dp)) // bordes redondeados al final
                        .background(if (pulsada) Color(0xFFADD8E6) else Color.White) // color de fondo
                        .border(width = 1.dp, color = Color(0xFFCCCCCC), shape = RoundedCornerShape(topEnd = 5.dp, bottomEnd = 5.dp)) // color de borde, grosor y redondeo
                        // detector de los gestos del usuario (capa de pulsación directa)
                        .pointerInput(key1 = tecla.nombre) {
                            // se reinicia el detector tras cada secuencia
                            awaitPointerEventScope {
                                val punterosActivos = mutableSetOf<Long>()  // set local con los ids de dedos sobre esta tecla concreta

                                // bucle infinito de eventos
                                while (true) {
                                    val evento = awaitPointerEvent()  // evento táctil

                                    // se recorre el número de dedos
                                    evento.changes.forEach { cambio ->
                                        when {
                                            // dedo nuevo presionado sobre esta tecla
                                            cambio.pressed && !punterosActivos.contains(element = cambio.id.value) -> {
                                                punterosActivos.add(element = cambio.id.value)  // se registra en el mapa global

                                                teclaActivaPorPuntero.value += (cambio.id.value to tecla.nombre)  // se enciende la nueva

                                                teclasPulsadas.value += tecla.nombre           // se actualiza el tracking

                                                reproducirNota(frecuencia = tecla.frecuencia)  // se dispara el sonido

                                                cambio.consume()  // se consume el evento
                                            }

                                            // dedo levantado de esta tecla
                                            !cambio.pressed && punterosActivos.contains(element = cambio.id.value) -> {
                                                punterosActivos.remove(element = cambio.id.value)  // se elimina el dedo del mapa global

                                                // se desactiva el efecto visual si no hay otro dedo sobre esa tecla
                                                if (punterosActivos.isEmpty()) {
                                                    teclasPulsadas.value -= tecla.nombre
                                                }

                                                cambio.consume()  // se consume el evento
                                            }
                                        }
                                    }
                                }
                            }
                        },
                    contentAlignment = Alignment.CenterEnd  // contenido centrado en la parte inferior
                ){
                    // ETIQUETA DE LA NOTA BLANCA
                    Text(
                        text = tecla.nombre,                     // texto
                        color = Color.Black,                     // color del texto
                        style = TextStyle(
                            fontFamily = fuenteTipografica,      // fuente tipográfica
                            fontSize = 8.sp,                     // tamaño de fuente
                            fontWeight = FontWeight.Medium,      // texto en negrita media
                            textAlign = TextAlign.Center         // texto alineado en el centro
                        ),
                        modifier = Modifier.padding(end = 6.dp)  // padding en el lateral derecho
                            .rotate(degrees = -90f)              // rotación del texto
                    )
                }
            }

            // se dibujan después las teclas negras
            teclasNegras.forEach { tecla ->
                val idxPrev = indicePrevioBlanca[tecla.nombre] ?: return@forEach     // se obtiene el índice de la tecla blanca previa

                val pulsada = teclasPulsadas.value.contains(tecla.nombre)            // se comprueba cuando la tecla esté pulsada

                val yOffset = alturaBlanca * idxPrev + alturaBlanca - altaNegra / 2  // desplazamiento vertical de esta tecla desde el borde superior

                // contenedor de la tecla
                Box(
                    modifier = Modifier.offset(y = yOffset)  // posicionamiento vertical
                        .width(anchaNegra)  // ancho
                        .height(altaNegra)  // alto
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))  // sombreado
                        .clip(shape = RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)) // bordes redondeados en la parte inferior
                        .background(color = if (pulsada) Color.Black else Color(0xFF111122))  // color de fondo
                        // detector de los gestos del usuario (capa de pulsación directa)
                        .pointerInput(key1 = tecla.nombre) {
                            // se reinicia el detector tras cada secuencia
                            awaitPointerEventScope {
                                val punterosActivos = mutableSetOf<Long>()  // set local con los ids de dedos sobre esta tecla concreta

                                // bucle infinito de eventos
                                while (true) {
                                    val evento = awaitPointerEvent()  // evento táctil

                                    // se recorre el número de dedos
                                    evento.changes.forEach { cambio ->
                                        when {
                                            // dedo nuevo presionado sobre esta tecla
                                            cambio.pressed && !punterosActivos.contains(element = cambio.id.value) -> {
                                                punterosActivos.add(element = cambio.id.value)  // se registra en el mapa global

                                                teclaActivaPorPuntero.value += (cambio.id.value to tecla.nombre)  // se enciende la nueva

                                                teclasPulsadas.value += tecla.nombre           // se actualiza el tracking

                                                reproducirNota(frecuencia = tecla.frecuencia)  // se dispara el sonido

                                                cambio.consume()  // se consume el evento
                                            }

                                            // dedo levantado de esta tecla
                                            !cambio.pressed && punterosActivos.contains(element = cambio.id.value) -> {
                                                punterosActivos.remove(element = cambio.id.value)  // se elimina el dedo del mapa global

                                                // se desactiva el efecto visual si no hay otro dedo sobre esa tecla
                                                if (punterosActivos.isEmpty()) {
                                                    teclasPulsadas.value -= tecla.nombre
                                                }

                                                cambio.consume()  // se consume el evento
                                            }
                                        }
                                    }
                                }
                            }
                        },
                    contentAlignment = Alignment.CenterEnd  // contenido centrado en parte inferior
                ){
                    // ETIQUETA DE LA NOTA NEGRA
                    Text(
                        text = tecla.nombre,                     // texto
                        color = Color.White,                     // color del texto
                        style = TextStyle(
                            fontFamily = fuenteTipografica,      // fuente tipográfica
                            fontSize = 6.sp,                     // tamaño de fuente
                            textAlign = TextAlign.Center         // texto alineado en el centro
                        ),
                        modifier = Modifier.padding(end = 3.dp)  // padding en el lateral derecho
                            .rotate(degrees = -90f)              // rotación del texto
                    )
                }
            }
        }
    }
}