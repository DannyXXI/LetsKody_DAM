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

@Composable
fun PianoInteractivo(
    modifier: Modifier = Modifier,
    fuenteTipografica: FontFamily = FontFamily.Default
) {
    // ── Precalentamiento de la caché de audio ─────────────────────────────
    // LaunchedEffect(Unit) se ejecuta una sola vez al montar el composable.
    // prewarmAudio() lanza su propio hilo de baja prioridad y retorna
    // inmediatamente: el hilo de composición no se bloquea en ningún momento.
    // Cuando el usuario toca la primera tecla (normalmente >1 s después),
    // los 37 buffers ya estarán sintetizados y la latencia será mínima.
    LaunchedEffect(Unit) {
        prepararAudio()
    }

    // ── Estado de teclas iluminadas ───────────────────────────────────────
    val teclasPulsadas = remember { mutableStateOf(setOf<String>()) }

    // ── Separación en listas para iterar independientemente ──────────────
    val teclasBlancas = TECLAS_PIANO.filter { !it.esNegra }
    val teclasNegras  = TECLAS_PIANO.filter {  it.esNegra }

    // ── Mapa nombre_negra → índice_blanca_previa ──────────────────────────
    // Construido una sola vez en la composición inicial (no en cada frame).
    // Usado tanto para el posicionamiento visual de las negras como para
    // el hit-test en teclaEnPosicion().
    val indicePrevioBlanca: Map<String, Int> = buildMap {
        var contBlancas = -1
        TECLAS_PIANO.forEach { tecla ->
            if (!tecla.esNegra) contBlancas++
            else put(tecla.nombre, contBlancas)
        }
    }

    // ── Tracking de dedos activos ─────────────────────────────────────────
    // Mapa inmutable: cada cambio crea una nueva instancia.
    // Clave: PointerId.value (Long único por dedo en contacto).
    // Valor: nombre de la última tecla tocada por ese dedo.
    val teclaActivaPorPuntero = remember { mutableStateOf(mapOf<Long, String>()) }

    // ── Dimensiones reales del piano en píxeles ───────────────────────────
    // onGloballyPositioned las actualiza cada vez que el layout cambia
    // (rotación de pantalla, cambio de tamaño, etc.).
    var anchoPx by remember { mutableFloatStateOf(0f) }
    var altoPx  by remember { mutableFloatStateOf(0f) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            // Rotación 180°: Fa2 queda abajo y Fa5 arriba (piano vertical estándar).
            // Las coordenadas de toque siguen siendo las del Box sin rotar,
            // por lo que el hit-test no necesita ninguna corrección.
            .rotate(180f)
            .background(Color(0xFF1A1A2E), RoundedCornerShape(12.dp))
            .padding(4.dp)
    ) {
        val totalAncho = maxWidth
        val totalAlto  = maxHeight

        // Dimensiones de cada tecla en unidades Dp (para los Modifier de layout).
        val alturaBlanca = totalAlto  / teclasBlancas.size
        val altaNegra    = alturaBlanca * 0.62f   // 62 % de la altura de la blanca
        val anchaNegra   = totalAncho  * 0.58f    // 58 % del ancho total

        Box(
            modifier = Modifier
                .fillMaxSize()
                // Capturamos el tamaño real en píxeles para pasárselo al hit-test.
                .onGloballyPositioned { coords ->
                    anchoPx = coords.size.width.toFloat()
                    altoPx  = coords.size.height.toFloat()
                }
                // ── CAPA DE GLISSANDO ─────────────────────────────────────
                // Este pointerInput recibe TODOS los eventos de TODOS los dedos
                // sobre el Box entero. Solo actúa en Move y Release; los Down
                // los manejan las teclas hijas para evitar duplicar el sonido.
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val evento = awaitPointerEvent()

                            if (evento.type == PointerEventType.Move ||
                                evento.type == PointerEventType.Release
                            ) {
                                evento.changes.forEach { cambio ->
                                    val id = cambio.id.value

                                    // ── Dedo levantado ────────────────────────────
                                    if (!cambio.pressed) {
                                        val nombreAnterior = teclaActivaPorPuntero.value[id]
                                        if (nombreAnterior != null) {
                                            // Eliminamos el dedo del mapa de tracking.
                                            teclaActivaPorPuntero.value =
                                                teclaActivaPorPuntero.value - id

                                            // Apagamos la tecla SOLO si ningún OTRO dedo
                                            // sigue sobre ella (soporte multitouch correcto).
                                            val sigueActiva = teclaActivaPorPuntero.value
                                                .values.contains(nombreAnterior)
                                            if (!sigueActiva) {
                                                teclasPulsadas.value =
                                                    teclasPulsadas.value - nombreAnterior
                                            }
                                        }
                                        return@forEach
                                    }

                                    // ── Dedo en movimiento (glissando) ────────────
                                    val pos = cambio.position
                                    // Determinamos qué tecla hay bajo la posición actual del dedo.
                                    val tecla = teclaEnPosicion(
                                        x                  = pos.x,
                                        y                  = pos.y,
                                        totalAlturaPx      = altoPx,
                                        totalAnchoPx       = anchoPx,
                                        teclasBlancas      = teclasBlancas,
                                        teclasNegras       = teclasNegras,
                                        indicePrevioBlanca = indicePrevioBlanca
                                    ) ?: return@forEach  // dedo fuera del área del piano

                                    val nombreAnterior = teclaActivaPorPuntero.value[id]

                                    // Solo actuamos si el dedo ha cambiado de tecla.
                                    // Esto evita disparar el sonido repetidamente mientras
                                    // el dedo permanece quieto sobre la misma tecla.
                                    if (tecla.nombre != nombreAnterior) {

                                        // Apagamos la tecla anterior si ningún otro
                                        // dedo la sigue manteniendo presionada.
                                        if (nombreAnterior != null) {
                                            val sigueActiva = teclaActivaPorPuntero.value
                                                .entries
                                                .any { (k, v) -> k != id && v == nombreAnterior }
                                            if (!sigueActiva) {
                                                teclasPulsadas.value =
                                                    teclasPulsadas.value - nombreAnterior
                                            }
                                        }

                                        // Encendemos la nueva tecla, actualizamos el tracking
                                        // y disparamos el sonido. reproducirNota() no bloquea
                                        // el hilo UI: usa el audioExecutor internamente.
                                        teclaActivaPorPuntero.value =
                                            teclaActivaPorPuntero.value + (id to tecla.nombre)
                                        teclasPulsadas.value =
                                            teclasPulsadas.value + tecla.nombre
                                        reproducirNota(tecla.frecuencia)
                                        cambio.consume()
                                    }
                                }
                            }
                        }
                    }
                }
        ) {

            // ── TECLAS BLANCAS ────────────────────────────────────────────
            // Se dibujan primero (fondo del Box) para que las negras queden
            // visualmente encima gracias al orden de composición de Compose.
            teclasBlancas.forEachIndexed { idx, tecla ->
                // Desplazamiento vertical de esta tecla desde el borde superior.
                val yOffset = alturaBlanca * idx
                // Azul claro cuando está pulsada; blanco cuando está libre.
                val pulsada = teclasPulsadas.value.contains(tecla.nombre)

                Box(
                    modifier = Modifier
                        .offset(y = yOffset)
                        .fillMaxWidth()
                        .height(alturaBlanca - 2.dp)  // 2 dp de separación entre teclas
                        .clip(RoundedCornerShape(topEnd = 5.dp, bottomEnd = 5.dp))
                        .background(if (pulsada) Color(0xFFADD8E6) else Color.White)
                        .border(
                            width = 1.dp,
                            color = Color(0xFFCCCCCC),
                            shape = RoundedCornerShape(topEnd = 5.dp, bottomEnd = 5.dp)
                        )
                        // ── CAPA DE PULSACIÓN DIRECTA (blancas) ──────────
                        // La clave del pointerInput es el nombre de la tecla:
                        // Compose crea un listener distinto por cada tecla blanca.
                        // Solo gestiona Down y Release directos (no el arrastre).
                        .pointerInput(tecla.nombre) {
                            awaitPointerEventScope {
                                // Set local de IDs de dedos sobre ESTA tecla concreta.
                                val punterosActivos = mutableSetOf<Long>()
                                while (true) {
                                    val evento = awaitPointerEvent()
                                    evento.changes.forEach { cambio ->
                                        when {
                                            // Dedo nuevo presionado sobre esta tecla.
                                            cambio.pressed &&
                                                    !punterosActivos.contains(cambio.id.value) -> {
                                                punterosActivos.add(cambio.id.value)
                                                // Registramos en el mapa global para que la
                                                // capa de glissando sepa el punto de partida.
                                                teclaActivaPorPuntero.value =
                                                    teclaActivaPorPuntero.value +
                                                            (cambio.id.value to tecla.nombre)
                                                teclasPulsadas.value =
                                                    teclasPulsadas.value + tecla.nombre
                                                // Sin scope.launch: reproducirNota gestiona
                                                // su propio hilo a través del audioExecutor.
                                                reproducirNota(tecla.frecuencia)
                                                cambio.consume()
                                            }
                                            // Dedo levantado de esta tecla.
                                            !cambio.pressed &&
                                                    punterosActivos.contains(cambio.id.value) -> {
                                                punterosActivos.remove(cambio.id.value)
                                                // Apagamos el visual solo si no queda
                                                // ningún otro dedo sobre esta tecla.
                                                if (punterosActivos.isEmpty()) {
                                                    teclasPulsadas.value =
                                                        teclasPulsadas.value - tecla.nombre
                                                }
                                                cambio.consume()
                                            }
                                        }
                                    }
                                }
                            }
                        },
                    contentAlignment = Alignment.CenterEnd
                ) {
                    // Etiqueta de la nota. Rotada -90° para que se lea horizontalmente
                    // (el Box padre está rotado 180°, así que -90° resulta legible).
                    Text(
                        text       = tecla.nombre,
                        fontSize   = 8.sp,
                        color      = Color(0xFF444444),
                        fontWeight = FontWeight.Medium,
                        fontFamily = fuenteTipografica,
                        textAlign  = TextAlign.Center,
                        modifier   = Modifier
                            .padding(end = 6.dp)
                            .rotate(-90f)
                    )
                }
            }

            // ── TECLAS NEGRAS ─────────────────────────────────────────────
            // Se dibujan después de las blancas → quedan encima visualmente.
            teclasNegras.forEach { tecla ->
                val idxPrev = indicePrevioBlanca[tecla.nombre] ?: return@forEach
                // Azul oscuro cuando está pulsada; negro cuando está libre.
                val pulsada = teclasPulsadas.value.contains(tecla.nombre)

                // La negra se centra verticalmente en la unión entre la blanca
                // [idxPrev] y la siguiente, igual que en un piano acústico real.
                val yOffset = alturaBlanca * idxPrev + alturaBlanca - altaNegra / 2

                Box(
                    modifier = Modifier
                        .offset(y = yOffset)
                        .width(anchaNegra)
                        .height(altaNegra)
                        .shadow(4.dp, RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                        .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                        .background(if (pulsada) Color(0xFF3A3A6E) else Color(0xFF111122))
                        // ── CAPA DE PULSACIÓN DIRECTA (negras) ───────────
                        // Mismo mecanismo que en las blancas.
                        .pointerInput(tecla.nombre) {
                            awaitPointerEventScope {
                                val punterosActivos = mutableSetOf<Long>()
                                while (true) {
                                    val evento = awaitPointerEvent()
                                    evento.changes.forEach { cambio ->
                                        when {
                                            cambio.pressed &&
                                                    !punterosActivos.contains(cambio.id.value) -> {
                                                punterosActivos.add(cambio.id.value)
                                                teclaActivaPorPuntero.value =
                                                    teclaActivaPorPuntero.value +
                                                            (cambio.id.value to tecla.nombre)
                                                teclasPulsadas.value =
                                                    teclasPulsadas.value + tecla.nombre
                                                reproducirNota(tecla.frecuencia)
                                                cambio.consume()
                                            }
                                            !cambio.pressed &&
                                                    punterosActivos.contains(cambio.id.value) -> {
                                                punterosActivos.remove(cambio.id.value)
                                                if (punterosActivos.isEmpty()) {
                                                    teclasPulsadas.value =
                                                        teclasPulsadas.value - tecla.nombre
                                                }
                                                cambio.consume()
                                            }
                                        }
                                    }
                                }
                            }
                        },
                    contentAlignment = Alignment.CenterEnd
                ) {
                    // Etiqueta más pequeña en negras (menos espacio disponible).
                    Text(
                        text       = tecla.nombre,
                        fontSize   = 6.sp,
                        color      = Color(0xFFAAAAAA),
                        fontFamily = fuenteTipografica,
                        textAlign  = TextAlign.Center,
                        modifier   = Modifier
                            .padding(end = 3.dp)
                            .rotate(-90f)
                    )
                }
            }
        }
    }
}