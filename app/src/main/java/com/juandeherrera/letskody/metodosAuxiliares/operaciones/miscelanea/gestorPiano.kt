package com.juandeherrera.letskody.metodosAuxiliares.operaciones.miscelanea

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
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

    val B = 0.00012 * (frecuencia / 110.0)  // coeficiente de inharmonicidad B (los armónicos de un piano no son exactos)

    fun freqArmonico(n: Double) = frecuencia * n * sqrt(1.0 + B * n * n)  // frecuencia real de cada armónico

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

/**
 * Presintetiza los buffers de todas las teclas en un hilo de baja prioridad.
 *
 * Debe llamarse una sola vez al montar el composable (LaunchedEffect(Unit)).
 * Retorna inmediatamente sin bloquear el hilo UI; la síntesis ocurre en
 * background. En dispositivos de gama media tarda ~500-800 ms en completarse.
 *
 * Tras esta función, [cacheBuffers] contendrá los 37 buffers y cualquier
 * pulsación posterior tendrá latencia mínima.
 */
fun prewarmAudio() {
    Thread {
        // Mínima prioridad: no competimos con el hilo UI ni con el audio.
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND)
        TECLAS_PIANO.forEach { tecla ->
            synchronized(cacheBuffers) {
                // Solo sintetizamos si el buffer no existe aún en la caché.
                // El synchronized garantiza que dos hilos no sinteticen la
                // misma frecuencia a la vez (aunque en la práctica solo hay
                // un hilo de prewarm).
                if (!cacheBuffers.containsKey(tecla.frecuencia)) {
                    cacheBuffers[tecla.frecuencia] =
                        sintetizarBuffer(tecla.frecuencia, DURACION_NOTA)
                }
            }
        }
    }.start()
}

/**
 * Reproduce una nota de piano a la frecuencia indicada.
 *
 * Funcionamiento interno:
 * 1. Obtiene el buffer PCM de la caché en O(1) (si prewarmAudio() ya corrió).
 * 2. Comprueba el semáforo atómico; descarta la nota si hay demasiadas activas.
 * 3. Encola la reproducción en el [audioExecutor] sin bloquear el hilo UI.
 * 4. El hilo del executor crea un AudioTrack MODE_STATIC, lo reproduce
 *    y lo libera al terminar, decrementando el semáforo.
 *
 * Esta función retorna inmediatamente (~0 ms); nunca bloquea al llamador.
 *
 * @param frecuencia Frecuencia en Hz de la nota a reproducir.
 * @param duracionMs Duración en milisegundos (por defecto [DURACION_NOTA_MS]).
 */
fun reproducirNota(frecuencia: Double, duracionMs: Int = DURACION_NOTA) {

    // ── Paso 1: obtener buffer de la caché ───────────────────────────────
    // Si prewarmAudio() terminó, esta operación es un HashMap.get() = O(1).
    // Si el buffer no estuviera en caché (p.ej. la tecla se pulsa antes de
    // que termine el precalentamiento), lo sintetizamos aquí. Habrá latencia
    // en esa primera pulsación concreta, pero solo esa vez.
    val buffer = synchronized(cacheBuffers) {
        cacheBuffers.getOrPut(frecuencia) {
            sintetizarBuffer(frecuencia, duracionMs)
        }
    }

    // ── Paso 2: semáforo de polyphony ────────────────────────────────────
    // Leemos el valor actual y, si hay hueco, intentamos incrementarlo
    // atómicamente con compareAndSet. Si otro hilo llegó antes y ya llenó
    // el contador, compareAndSet falla y descartamos esta nota.
    val actual = notasActivas.get()
    if (actual >= MAX_NOTAS_SIMULTANEAS) return
    if (!notasActivas.compareAndSet(actual, actual + 1)) return

    // ── Paso 3: encolar reproducción en el executor ──────────────────────
    // La lambda se ejecuta en un hilo del pool, nunca en el hilo UI.
    // El hilo UI queda libre inmediatamente después de este submit().
    audioExecutor.execute {
        try {
            android.os.Process.setThreadPriority(
                android.os.Process.THREAD_PRIORITY_URGENT_AUDIO
            )

            // Tamaño mínimo de buffer exigido por el hardware de audio.
            // Usamos el mayor entre ese mínimo y el tamaño real de los datos PCM
            // × 2 (porque son bytes, no shorts: cada short ocupa 2 bytes).
            val minBufSize = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufSize = maxOf(buffer.size * 2, minBufSize)

            // Creamos el AudioTrack en MODE_STATIC.
            // Cada nota obtiene su propio AudioTrack: sin reutilización,
            // sin gestión de estados, sin riesgo de IllegalStateException.
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(SAMPLE_RATE)
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY)
                .build()

            // Cargamos todos los datos PCM en el buffer del hardware.
            // En MODE_STATIC, write() es no bloqueante: copia los datos y retorna.
            track.write(buffer, 0, buffer.size)
            // play() arranca la reproducción desde el inicio del buffer.
            // Retorna inmediatamente; el hardware reproduce en paralelo.
            track.play()

            // Esperamos a que el hardware termine de reproducir el buffer.
            // duracionMs es el tiempo real de audio; +150 ms de margen para que
            // el driver consuma los últimos frames sin corte audible.
            Thread.sleep(duracionMs.toLong() + 150)

            // Liberamos los recursos de audio del sistema operativo.
            // stop() detiene la reproducción; release() libera el objeto.
            // Importante: en MODE_STATIC siempre se puede llamar stop()
            // desde estado PLAYING sin excepción.
            track.stop()
            track.release()

        } catch (_: Exception) {
            // Capturamos cualquier excepción de audio (estado inválido,
            // recursos insuficientes del sistema, etc.) para no crashear la app.
            // El finally siempre decrementa el semáforo aunque haya error.
        } finally {
            // Decrementamos el semáforo: el slot queda libre para la siguiente nota.
            // Este finally se ejecuta SIEMPRE, incluso si hubo excepción,
            // garantizando que el contador nunca se queda "bloqueado" en el máximo.
            notasActivas.decrementAndGet()
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
//  HIT-TEST: QUÉ TECLA HAY BAJO UN DEDO
//
//  El piano está rotado 180° visualmente, pero Compose entrega las coordenadas
//  de toque en el sistema local del Box sin rotar (Y crece hacia abajo,
//  0 en el borde superior del componente). Por eso el cálculo es directo
//  y coincide con el orden de teclasBlancas (de arriba = Fa5 a abajo = Fa2
//  en pantalla, pero el orden lógico de la lista no cambia).
//
//  PRIORIDAD DE DETECCIÓN:
//  1. Teclas negras: se comprueban primero porque visualmente están encima.
//     Solo se evalúan si el dedo está en la zona horizontal de las negras
//     (x < anchaNegraPx), optimización que evita iterar todas las negras
//     cuando el dedo está claramente en la zona de blancas.
//  2. Teclas blancas: fallback por división entera de Y entre alturaBlancaPx.
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Devuelve la [TeclaPiano] que se encuentra bajo la posición (x, y) en píxeles,
 * o null si el dedo está fuera del área del piano.
 *
 * @param x                  Coordenada X del dedo en píxeles (0 = izquierda).
 * @param y                  Coordenada Y del dedo en píxeles (0 = arriba).
 * @param totalAlturaPx      Altura total del piano en píxeles.
 * @param totalAnchoPx       Ancho total del piano en píxeles.
 * @param teclasBlancas      Lista de teclas blancas en orden de arriba a abajo.
 * @param teclasNegras       Lista de teclas negras en orden de arriba a abajo.
 * @param indicePrevioBlanca Mapa nombre_negra → índice de la blanca previa.
 */
fun teclaEnPosicion(
    x: Float,
    y: Float,
    totalAlturaPx: Float,
    totalAnchoPx: Float,
    teclasBlancas: List<TeclaPiano>,
    teclasNegras: List<TeclaPiano>,
    indicePrevioBlanca: Map<String, Int>
): TeclaPiano? {
    // Fuera del área del piano: ignoramos el evento.
    if (y < 0f || y > totalAlturaPx) return null

    // Altura de cada tecla blanca en píxeles (todas iguales entre sí).
    val alturaBlancaPx = totalAlturaPx / teclasBlancas.size
    // Las teclas negras son el 62 % de la altura de las blancas.
    val altaNegraPx    = alturaBlancaPx * 0.62f
    // Las teclas negras ocupan el 58 % del ancho total (lado izquierdo del piano).
    val anchaNegraPx   = totalAnchoPx  * 0.58f

    // ── Comprobación de teclas negras (prioridad alta) ───────────────────
    // Solo tiene sentido si el dedo está en la zona horizontal de las negras.
    if (x < anchaNegraPx) {
        for (negra in teclasNegras) {
            val idxPrev = indicePrevioBlanca[negra.nombre] ?: continue

            // La negra se centra en la unión entre la blanca [idxPrev] y la siguiente.
            // Su borde superior (yTop) y borde inferior (yBot) en el eje Y.
            val yTop = alturaBlancaPx * idxPrev + alturaBlancaPx - altaNegraPx / 2
            val yBot = yTop + altaNegraPx

            if (y in yTop..yBot) return negra   // ← dedo sobre esta negra
        }
    }

    // ── Comprobación de teclas blancas (fallback) ────────────────────────
    // Si no hay ninguna negra bajo el dedo, la blanca se determina por división
    // entera: qué "celda" de altura alturaBlancaPx contiene la coordenada Y.
    val idxBlanca = (y / alturaBlancaPx).toInt().coerceIn(0, teclasBlancas.size - 1)
    return teclasBlancas[idxBlanca]
}



