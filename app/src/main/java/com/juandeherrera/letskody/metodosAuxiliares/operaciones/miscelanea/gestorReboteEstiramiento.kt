package com.juandeherrera.letskody.metodosAuxiliares.operaciones.miscelanea

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.animation.core.Spring
import androidx.compose.ui.unit.dp
import com.juandeherrera.letskody.R

// objeto que centraliza las constantes configurables que afectan al comportamiento de la lógica
object CONFIG {
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
class SoundManager(context: Context) {

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
