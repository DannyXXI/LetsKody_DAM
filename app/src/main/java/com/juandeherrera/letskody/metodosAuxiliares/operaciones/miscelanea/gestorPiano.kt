package com.juandeherrera.letskody.metodosAuxiliares.operaciones.miscelanea

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Process
import com.juandeherrera.letskody.clasesAuxiliares.TECLAS_PIANO
import com.juandeherrera.letskody.clasesAuxiliares.TeclaPiano
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.math.sqrt

private const val SAMPLE_RATE = 44100  // frecuencia de muestreo estándar de CD

private const val DURACION_NOTA = 600  // duración (milisegundos) de cada nota

private val cacheBuffers = HashMap<Double, ShortArray>()  // caché de los buffers PCM ya sintetizados

private const val MAX_NOTAS_SIMULTANEAS = 8  // número máximo de notas que pueden sonar simultáneamente

private val notasActivas = AtomicInteger( 0)  // contador atómico de notas activas en el momento (si llega al maximo las demás se descartan)

private val audioExecutor = Executors.newFixedThreadPool(MAX_NOTAS_SIMULTANEAS)  // pool de hilos de tamaño fijo para la reproducción de audio

// función auxiliar para sintetizar y crear un buffer PCM 16-bit que representará el sonido de una nota de piano
private fun sintetizarBuffer(frecuencia: Double, duracionMs: Int): ShortArray {
    val numSamples = (SAMPLE_RATE * duracionMs / 1000.0).toInt()  // número de samples que se van a generar

    val buf = ShortArray(size = numSamples)                       // buffer donde se guardará el sonido

    // lista de armonicos base (timbre del piano aproximado)
    val armonicosBase = listOf(
        1.0 to 1.000,
        2.0 to 0.480,
        3.0 to 0.280,
        4.0 to 0.150,
        5.0 to 0.090,
        6.0 to 0.050,
        7.0 to 0.025,
        8.0 to 0.012
    )

    val brilloDinamico = (frecuencia / 440.0).coerceIn(0.5, 2.5)  // ajusta el brillo de la nota (a más brillo las notas son más agudas)

    // se recorre la lista de armónicos para aplicarles el brillo dinámico y aumentarles su amplitud según el brillo -> mejor calidad del sonido
    val armonicos = armonicosBase.mapIndexed { idx, (mult, amp) ->
        val factorBrillo = if (idx % 2 == 1) brilloDinamico * 0.15 else 0.0
        mult to amp * (1.0 + factorBrillo)
    }

    val ampTotal = armonicos.sumOf { it.second }  // se suma las amplitudes para que el sonido no sature

    val b = 0.00012 * (frecuencia / 110.0)  // coeficiente de inharmonicidad B (los armónicos de un piano no son exactos)

    fun freqArmonico(n: Double) = frecuencia * n * sqrt(1.0 + b * n * n)  // frecuencia real de cada armónico

    // Parámetros ADSR (forma de la nota)
    val attSamples   = (SAMPLE_RATE * 0.005).toInt()   // ataque (subida rápida del sonido)
    val decaySamples = (SAMPLE_RATE * 0.080).toInt()   // decay (desciende el sonido hasta un nivel estable)
    val sustainLevel = (0.55 - frecuencia / 2000.0).coerceIn(0.25, 0.55)  // sustain (nivel se mantiene la nota)
    val decayRate    = 2.8 + (frecuencia / 440.0) * 1.8                   // release (controla cuanto tarda en apagarse)

    val vibratoActivo = frecuencia < 150.0  // vibrato (solo para notas graves)
    val vibratoFreq   = 4.5                 // frecuencia del vibrato
    val vibratoAmp    = 0.004               // intensidad del vibrato

    // se simula el golpe inicial del martillo del piano
    val ruidoMartillo = (SAMPLE_RATE * 0.008).toInt()
    val rng = java.util.Random()

    // bucle donde se genera el sonido
    for (i in 0 until numSamples) {
        val t = i.toDouble() / SAMPLE_RATE   // se obtiene el tiempo en segundos

        val faseAtaque = if (i < attSamples) i.toDouble() / attSamples else 1.0  // ataque (subida progresiva)

        // decay (se baja hasta mantenerse estable)
        val faseDecay = if (i < attSamples + decaySamples) {
            val p = (i - attSamples).coerceAtLeast(0).toDouble() / decaySamples
            1.0 - (1.0 - sustainLevel) * p
        } else sustainLevel

        val release = exp(-decayRate * t)  // release (decaimiento exponencial)

        val env = faseAtaque * faseDecay * release  // envolvente total (forma final del sonido)

        val vibrato = if (vibratoActivo) 1.0 + vibratoAmp * sin(2.0 * PI * vibratoFreq * t) else 1.0  // vibrato de amplitud

        // se genera el sonido (se suma todas las ondas sinusoidales)
        var muestra = 0.0
        for ((mult, amp) in armonicos) {
            muestra += amp * sin(2.0 * PI * freqArmonico(mult) * t)
        }

        muestra = (muestra / ampTotal) * env * vibrato  // se aplica los efectos y el envolvente al sonido

        // se agrega ruido de martillo al inicio para darle un toque mas realista
        if (i < ruidoMartillo) {
            val p = i.toDouble() / ruidoMartillo
            muestra += rng.nextGaussian() * 0.05 * exp(-5.0 * p)
        }

        // se convierte a PCM 16-bit con 85 % de headroom para evitar clipping
        buf[i] = (muestra * Short.MAX_VALUE * 0.85).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
    }

    return buf  // se devuelve el buffer listo con el audio listo para reproducir
}

// función auxiliar para presintetizar los buffers de todas las teclas en un hilo de baja prioridad
fun prepararAudio() {
    // se crea un hilo manualmente
    Thread {
        // se le dice al sistema que priorice la UI y el audio
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)

        // se recorre cada tecla del piano
        TECLAS_PIANO.forEach { tecla ->
            // se bloque al acceso al cache de los buffers sintetizados
            synchronized(lock = cacheBuffers) {
                // se sintetiza lo que no exista en la caché de los buffers
                if (!cacheBuffers.containsKey(tecla.frecuencia)) {
                    cacheBuffers[tecla.frecuencia] = sintetizarBuffer(frecuencia = tecla.frecuencia, duracionMs = DURACION_NOTA)  // se genera el sonido
                }
            }
        }
    }.start()  // se inicia el hilo
}

// función auxiliar para reproducir una nota de piano a una frecuencia determinada
fun reproducirNota(frecuencia: Double, duracionMs: Int = DURACION_NOTA) {
    // se obtiene el buffer
    val buffer = synchronized(lock = cacheBuffers) {
        cacheBuffers.getOrPut(key = frecuencia) { sintetizarBuffer(frecuencia, duracionMs) }
    }

    val actual = notasActivas.get()  // se lee cuantas notas están sonando ahora mismo

    if (actual >= MAX_NOTAS_SIMULTANEAS)  // si se supera el límite, se descarta la nota

    if (!notasActivas.compareAndSet(actual, actual + 1)) return   // solo se incrementa si nadie cambio el valor antes

    // se ejecuta el hilo del audio
    audioExecutor.execute {
        try {
            // se da prioridad al audio
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO)

            // tamaño mínimo del buffer que exige el hardware
            val minBufSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT)

            val bufSize = maxOf(buffer.size * 2, minBufSize)  // tamaño del buffer

            // se crea el audio (AudioTrack)
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        // se le comunica al sistema que es música
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(SAMPLE_RATE)                   // frecuencia de muestreo
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)  // PCM de 16 bit
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO) // mono
                        .build()
                )
                .setBufferSizeInBytes(bufSize)
                .setTransferMode(AudioTrack.MODE_STATIC)                     // se carga el audio de golpe
                .setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY) // se minimiza la latencia
                .build()

            track.write(buffer, 0, buffer.size)  // se carga el audio en el hardware

            track.play()  // se reproduce el audio

            Thread.sleep(duracionMs.toLong() + 150)  // se espera lo que dure la nota + 150 milisegundos

            track.stop()      // se detiene el audio

            track.release()   // se libera el recurso de la memoria

        }
        catch (ex: Exception) {
            // se captura cualquier excepción con el audio y se muestra por terminal
            println("Error al reproducir el audio: ${ex.message}")
        }
        finally {
            notasActivas.decrementAndGet()  // se libera el slot de polifonía para la siguiente nota
        }
    }
}

// función auxiliar que devuelve la tecla del piano que se está en cierta posición (x, y) en pixeles
fun teclaEnPosicion(x: Float, y: Float, totalAlturaPx: Float, totalAnchoPx: Float, teclasBlancas: List<TeclaPiano>, teclasNegras: List<TeclaPiano>, indicePrevioBlanca: Map<String, Int>): TeclaPiano? { // Fuera del área del piano: ignoramos el evento.

    // si el dedo está fuera del área del piano, se ignora el evento
    if (y !in 0f..totalAlturaPx) return null

    val alturaBlancaPx = totalAlturaPx / teclasBlancas.size  // altura de las teclas blancas

    val altaNegraPx = alturaBlancaPx * 0.62f  // altura de las teclas negras (62% de la altura de las blancas)

    val anchaNegraPx = totalAnchoPx  * 0.58f // ancho de las negras (58% del ancho total)

    // se comprueba si el dedo está en la zona horizontal de las negras
    if (x < anchaNegraPx) {
        // se recorren todas las teclas negras
        for (negra in teclasNegras) {
            // se busca la blanca anterior a la negra, si no existe se salta
            val idxPrev = indicePrevioBlanca[negra.nombre] ?: continue

            val yTop = alturaBlancaPx * idxPrev + alturaBlancaPx - altaNegraPx / 2  // posición vertical de la negra

            val yBot = yTop + altaNegraPx  // parte inferior de la negra

            if (y in yTop..yBot) return negra // si el dedo está dentro del rango vertical se confirma que se toca la negra
        }
    }

    // se obtiene el índice de la blanca
    val idxBlanca = (y / alturaBlancaPx).toInt().coerceIn(0, teclasBlancas.size - 1)

    return teclasBlancas[idxBlanca] // se confirma que se tocó la blanca
}