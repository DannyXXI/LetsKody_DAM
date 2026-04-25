package com.juandeherrera.letskody.metodosAuxiliares.componentes.miscelanea

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.juandeherrera.letskody.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.roundToInt
import kotlin.math.sign
import kotlin.math.sin

// objeto que centraliza las constantes configurables que afectan al comportamiento de la lógica
private object CONFIG {
    val IMAGE_SIZE = 150.dp  // tamaño de la imagen

    // velocidades iniciales (px/frame) en los ejes X e Y al iniciar un rebote
    const val INITIAL_VEL_X = 6f
    const val INITIAL_VEL_Y = 5f

    const val AMORTIGUACION = 0.88f  // factor de amortiguación en cada rebote (sensación física natural)

    const val MIN_VELOCITY = 2.5f    // velocidad mínima garantizada (px/frame)

    const val FRAMES_DELAY = 16L     // tiempo entre frames de la simulación (60 fps aproximadamente)

    const val MAX_SCALE_FACTOR = 1.6f   // rango máximo de escalado (160%) de la imagen durante el estiramiento

    const val MIN_SCALE_FACTOR = 0.6f   // rango mínimo de escalado (60%) de la imagen durante el estiramiento

    const val MULTIPLY_VELOCITY = 0.5f  // multiplicador de la velocidad al arrastrar la imagen

    const val VOLUMEN_SONIDO = 0.5f     // volumen de los efectos de sonido

    const val SONIDO_REBOTE_COOLDOWN = 150L  // tiempo mínimo entre dos reproducciones del efecto de sonido de rebote

    val SONIDO_REBOTE = R.raw.rebote    // efecto de sonido al rebotar

    val SONIDO_ESTIRAR = R.raw.estirar  // efecto de sonido al estirar

    // parámetros de la animación de estiramiento de vuelta a su escala original
    val ESTIRAR_STIFFNESS = Spring.StiffnessMedium          // rigidez media (provoca una respuesta ni lenta ni brusca)
    val ESTIRAR_DAMPING = Spring.DampingRatioMediumBouncy   // rebota un poco antes de estabilizarse (efecto gelatina)

    const val RATIO_COMPRESION = 0.35f   // controla la compresión perpendicular durante el estiramiento
}

// clase que se encarga de gestionar los efectos de sonido
private class SoundManager(context: Context) {

    // se crea un reproductor de efectos de sonido cortos de baja latencia
    private val reproductor = SoundPool.Builder()
        .setMaxStreams(4)  // hasta cuatro efectos de sonido sonando a la vez
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)                       // se usa como tipo juego
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)  // tipo de efecto
                .build()
        )
        .build()

    private var reboteId = 0  // Id del efecto de sonido de rebote

    private var estirarId = 0  // Id del efecto de sonido de estirar

    private var sonidosCargados = false  // comprueba si los sonidos están cargados en la memoria

    private var tiempoUltimoRebote = 0L  // timestamp de la última reproducción del efecto de sonido de rebote

    init {
        // se registra el reproductor antes de cargar los sonidos por si la carga fuera instantánea
        reproductor.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) sonidosCargados = true   // si el estado es 0 es que la carga fue exitosa
        }

        // se cargan los respectivos efectos de sonido
        reboteId = reproductor.load(context, CONFIG.SONIDO_REBOTE, 1)
        estirarId = reproductor.load(context, CONFIG.SONIDO_ESTIRAR, 1)
    }

    // función encargada de reproducir el sonido de rebote
    fun playRebote() {
        if (!sonidosCargados) return  // si los sonidos no están cargados, se sale

        val now = System.currentTimeMillis()  // se obtiene el timestamp actual

        if (now - tiempoUltimoRebote < CONFIG.SONIDO_REBOTE_COOLDOWN) return  // se controla el tiempo de espera

        tiempoUltimoRebote = now  // se actualiza el tiempo de la última reproducción del efecto de sonido

        // se reproduce el sonido
        reproductor.play(reboteId,CONFIG.VOLUMEN_SONIDO, CONFIG.VOLUMEN_SONIDO, 1, 0, 1f)
    }

    // función encargada de reproducir el sonido de estiramiento
    fun playEstiramiento() {
        if (!sonidosCargados) return  // si los sonidos no están cargados, se sale

        // se reproduce el sonido
        reproductor.play(estirarId,CONFIG.VOLUMEN_SONIDO, CONFIG.VOLUMEN_SONIDO, 1, 0, 1f)
    }

    fun liberarRecursos() = reproductor.release()  // función encargada de liberar los recursos de la memoria
}

// función auxiliar que carga la imagen de Kody interactiva en la pantalla
@Composable
fun KodyInteractivo (context: Context, scope: CoroutineScope) {

    val densidadPantalla = LocalDensity.current  // se obtiene la densidad de pantalla

    val imagenSizePx = with(receiver = densidadPantalla) { CONFIG.IMAGE_SIZE.toPx() }  // tamaño de la imagen en pixeles (px)

    var areaReboteImagen by remember { mutableStateOf(value = Offset.Zero) }  // tamaño del área de rebote (px)

    val gestorSonido = remember { SoundManager(context = context) }  // se crea el gestor de los efectos de sonido

    DisposableEffect(key1 = Unit) {
        onDispose { gestorSonido.liberarRecursos() }  // se liberan los recursos del gestor de sonido cuando se destruye
    }

    // coordenadas (px) de la posición de Kody (esquina superior-izquierda)
    val posicionX = remember { Animatable(initialValue = 0f) }
    val posicionY = remember { Animatable(initialValue = 0f) }

    // escala de la imagen para el efecto de estiramiento
    val escalaX = remember { Animatable(initialValue = 1f) }
    val escalaY = remember { Animatable(initialValue = 1f) }

    // velocidad (px/frame) del rebote de la imagen
    var velocidadReboteX by remember { mutableFloatStateOf(value = CONFIG.INITIAL_VEL_X) }
    var velocidadReboteY by remember { mutableFloatStateOf(value = CONFIG.INITIAL_VEL_Y) }

    var esArrastrado by remember { mutableStateOf(value = false) }     // se comprueba si se arrastra la imagen

    var rebotarJob by remember { mutableStateOf<Job?>(value = null) } // job (referencia a una corrutina en ejecución) del rebote

    var sonidoEstirar by remember { mutableStateOf(value = false) }   // se comprueba si se realiza el efecto de sonido al estirar la imagen

    // función interna para iniciar el rebote
    fun iniciarRebote() {
        if (rebotarJob?.isActive == true) return  // si hay una corrutina activa, no se activa

        // se lanza la corrutina del rebote
        rebotarJob = scope.launch {
            // mientras que la corrutina este activa
            while (isActive) {
                delay(timeMillis = CONFIG.FRAMES_DELAY)  // se espera al siguiente frame

                if (esArrastrado) continue  // se pausa si el usuario esta interactuando

                if (areaReboteImagen == Offset.Zero) continue  // se pausa si aún no hay una área de la imagen

                // limites máximos (esquina superior-izquierda de la imagen)
                val maxX = areaReboteImagen.x - imagenSizePx
                val maxY = areaReboteImagen.y - imagenSizePx

                // posición tentativa del siguiente frame (sin restricciones aún)
                var nx = posicionX.value + velocidadReboteX
                var ny = posicionY.value + velocidadReboteY

                var rebote = false // se comprueba si hay colisión con algún borde para que rebote la imagen

                // rebote horizontal
                if (nx <= 0f || nx >= maxX) {
                    velocidadReboteX = -velocidadReboteX * CONFIG.AMORTIGUACION // se invierte la velocidad y se amortigua

                    // se fuerza a una velocidad mínima para evitar que la imagen quede pegada al borde
                    if (abs(x = velocidadReboteX) < CONFIG.MIN_VELOCITY) velocidadReboteX = CONFIG.MIN_VELOCITY * sign(x = velocidadReboteX)

                    nx = nx.coerceIn(minimumValue = 0f, maximumValue = maxX)  // se evita salir del área

                    rebote = true  // se confirma el rebote
                }

                // rebote vertical
                if (ny <= 0f || ny >= maxY) {
                    velocidadReboteY = -velocidadReboteY * CONFIG.AMORTIGUACION // se invierte la velocidad y se amortigua

                    // se fuerza a una velocidad mínima para evitar que la imagen quede pegada al borde
                    if (abs(x = velocidadReboteY) < CONFIG.MIN_VELOCITY) velocidadReboteY = CONFIG.MIN_VELOCITY * sign(x = velocidadReboteY)

                    ny = ny.coerceIn(minimumValue = 0f, maximumValue = maxY)  // se evita salir del área

                    rebote = true  // se confirma el rebote
                }

                if (rebote) gestorSonido.playRebote()  // si hay rebote que suene su efecto de sonido

                // se actualiza la posición de la imagen
                posicionX.snapTo(targetValue = nx)
                posicionY.snapTo(targetValue = ny)
            }
        }
    }

    // inicializador que se ejecuta una sola vez para obtener el área de la imagen, centrarla y arrancar la corrutina
    LaunchedEffect(key1 = true) {
        snapshotFlow { areaReboteImagen }.first {it != Offset.Zero}  // se espera a se mida el área de rebote de la imagen

        // se centra a kody en el área disponible
        posicionX.snapTo(targetValue = (areaReboteImagen.x - imagenSizePx) / 2f)
        posicionY.snapTo(targetValue = (areaReboteImagen.y - imagenSizePx) / 2f)

        iniciarRebote()  // se inicia la corrutina
    }

    // inicializador que se ejecuta cada vez que el área de la imagen cambio y arranca la corrutina si se canceló
    LaunchedEffect(key1 = areaReboteImagen) {
        if (areaReboteImagen != Offset.Zero && rebotarJob?.isActive != true) { iniciarRebote() }
    }

    // contenedor de la imagen
    Box(
        modifier = Modifier.fillMaxSize()  // se ocupa el espacio disponible
            // se actualiza las dimensiones reales del área tras cada medición del layout
            .onGloballyPositioned{ coordenadas ->
                areaReboteImagen = Offset(
                    x = coordenadas.size.width.toFloat(),
                    y = coordenadas.size.height.toFloat()
                )
            }
    ){
        // imagen interactiva
        Image(
            painter = painterResource(id = R.drawable.kody_orange),  // ruta al recurso (imagen)
            contentDescription = "Kody",                             // descripción de la imagen
            contentScale = ContentScale.Fit,                         // forma de escalar la imagen
            modifier = Modifier.size(CONFIG.IMAGE_SIZE)              // tamaño de la imagen
                .offset {
                    // posición actualizada de la imagen
                    IntOffset(x = posicionX.value.roundToInt(), y = posicionY.value.roundToInt())
                }
                .graphicsLayer {
                    // modificación de la escala con el estiramiento
                    scaleX = escalaX.value
                    scaleY = escalaY.value
                }
                // detector de gestos de los dedos del usuario
                .pointerInput(key1 = Unit) {
                    // se reinicia el detector tras cada secuencia
                    awaitEachGesture {
                        awaitFirstDown()       // se espera al primer toque

                        esArrastrado = true    // se activa la interacción

                        rebotarJob?.cancel()   // se cancela la corrutina de rebote

                        sonidoEstirar = false  // se reinicia el sonido

                        var distanciaInicialDistorsion: Float? = null // distancia inicial de distorsión con los dedos

                        do {
                            val evento = awaitPointerEvent()  // evento táctil

                            val dedos = evento.changes.count { it.pressed }  // cantidad de dedos activos

                            // 1 DEDO -> ARRASTRAR
                            if (dedos == 1) {
                                val desplazamiento = evento.calculatePan()  // desplazamiento del dedo en este frame

                                // nueva posición de la imagen (evitando que se salga del área)
                                val newX = (posicionX.value + desplazamiento.x).coerceIn(minimumValue = 0f, maximumValue = areaReboteImagen.x - imagenSizePx)
                                val newY = (posicionY.value + desplazamiento.y).coerceIn(minimumValue = 0f, maximumValue = areaReboteImagen.y - imagenSizePx)

                                // se actualizan las posiciones
                                scope.launch { posicionX.snapTo(targetValue = newX) }
                                scope.launch { posicionY.snapTo(targetValue = newY) }

                                // velocidad de inercia para el rebote posterior
                                val velocidadX = desplazamiento.x * CONFIG.MULTIPLY_VELOCITY
                                val velocidadY = desplazamiento.y * CONFIG.MULTIPLY_VELOCITY

                                // actualizar la velocidad de rebote (forzar una velocidad mínima en caso de que el usuario no lo toque)
                                velocidadReboteX = if (abs(x = velocidadX) < CONFIG.MIN_VELOCITY) {
                                    CONFIG.MIN_VELOCITY * if (velocidadX >= 0f) 1f else -1f
                                }
                                else { velocidadX }
                                velocidadReboteY = if (abs(x = velocidadY) < CONFIG.MIN_VELOCITY) {
                                    CONFIG.MIN_VELOCITY * if (velocidadY >= 0f) 1f else -1f
                                }
                                else { velocidadY }

                                evento.changes.forEach { it.consume() }  // se consume el evento
                            }

                            // 2+ DEDOS -> ESTIRAMIENTO
                            if (dedos >= 2) {
                                // posición de los dos primeros dedos
                                val posicion1 = evento.changes[0].position
                                val posicion2 = evento.changes[1].position

                                // dirección del gesto de los dos dedos
                                val direccionX = posicion2.x - posicion1.x
                                val direccionY = posicion2.y - posicion1.y

                                val distancia = hypot(x = direccionX.toDouble(), y = direccionY.toDouble()).toFloat()  // distancia entre los dedos

                                // primer frame del gesto
                                if (distanciaInicialDistorsion == null) {
                                    distanciaInicialDistorsion = distancia   // se guarda la distancia base
                                    evento.changes.forEach { it.consume() }  // se consume el evento
                                    continue                                 // se espera al siguiente frame para calcular la deformación
                                }

                                val distanciaDelta = distancia - distanciaInicialDistorsion  // diferencia respecto al inicio del gesto

                                val intensidad = distanciaDelta / 300f  // intensidad del estiramiento

                                val angulo = atan2(y = direccionY.toDouble(), x = direccionX.toDouble())  // ángulo del vector entre los dedos

                                // peso en cada eje (coseno influencia en X y seno influencia en Y)
                                val coseno = abs(x = cos(x = angulo)).toFloat()
                                val seno = abs(x = sin(x = angulo)).toFloat()

                                val cantidadEstiramiento = intensidad  // cantidad de estiramiento (si es positivo se estira y si es negativo se comprime)

                                val compresion = cantidadEstiramiento * CONFIG.RATIO_COMPRESION  // compresión perpendicular (efecto gelatina)

                                // se obtiene la nueva escala de la imagen (limitando unos valores para evitar deformaciones)
                                val newEscalaX = (1f + cantidadEstiramiento * coseno - compresion * seno).coerceIn(
                                    minimumValue = CONFIG.MIN_SCALE_FACTOR,
                                    maximumValue = CONFIG.MAX_SCALE_FACTOR
                                )
                                val newEscalaY = (1f + cantidadEstiramiento * seno - compresion * coseno).coerceIn(
                                    minimumValue = CONFIG.MIN_SCALE_FACTOR,
                                    maximumValue = CONFIG.MAX_SCALE_FACTOR
                                )

                                // se actualiza la escala
                                scope.launch { escalaX.snapTo(targetValue = newEscalaX) }
                                scope.launch { escalaY.snapTo(targetValue = newEscalaY) }

                                // se reproduce el sonido de estiramiento al iniciar el gesto
                                if (!sonidoEstirar) {
                                    gestorSonido.playEstiramiento()
                                    sonidoEstirar = true
                                }

                                evento.changes.forEach { it.consume() }  // se consume el evento
                            }

                        }while (evento.changes.any { it.pressed })  // salir cuando se suelten los dedos

                        // SE REINICIA LA ESCALA CUANDO EL USUARIO DEJA DE PULSAR LA PANTALLA
                        esArrastrado = false  // se reinicia el indicado de arrastre

                        if (sonidoEstirar) gestorSonido.playEstiramiento()  // se reproduce el mismo sonido para la vuelta al estado inicial

                        // se realiza la animación de volver a la escala original
                        val animacionEscalaOriginal = spring<Float>(
                            dampingRatio = CONFIG.ESTIRAR_DAMPING,
                            stiffness = CONFIG.ESTIRAR_STIFFNESS
                        )

                        // se actualiza la escala de la imagen
                        scope.launch { escalaX.animateTo(targetValue = 1f, animationSpec = animacionEscalaOriginal) }
                        scope.launch { escalaY.animateTo(targetValue = 1f, animationSpec = animacionEscalaOriginal) }

                        iniciarRebote()  // se reanuda la corrutina del rebote
                    }
                }
        )
    }
}