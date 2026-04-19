package com.juandeherrera.letskody.viewModels.numinario1

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juandeherrera.letskody.R
import com.juandeherrera.letskody.clasesAuxiliares.ResultadoJuegoContrarreloj
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// CONSTANTES DEL JUEGO
private const val DURACION_PARTIDA = 120      // segundos totales del temporizador
private const val PUNTOS_ACIERTO = 10         // puntos que se suma al acertar una operación
private const val PENALIZACION_TIEMPO = 10    // segundos que se restan al temporizador por cada fallo
private const val RANGO_MAX = 20              // valor máximo posible de cada número en la operación
private const val RANGO_MIN = 5               // valor mínimo posible de cada número en la operación

// clase ViewModel que contiene la lógica del juego
// se recomienda porque sobrevive a cambios de configuración y separa la lógica de la interfaz gráfica
class Numinario1ViewModel(private val context: Context) : ViewModel() {

    // variables públicas que observa la pantalla
    // con StateFlow hace que sea un flujo de datos reactivo (si el valor cambia, todos los Composables que lo estén observando se recomponen)
    // las variables privadas _nombre solo las puede modificar el ViewModel y las publicas para que la pantalla solo pueda leer su valor

    // estado general del juego
    private val _estado = MutableStateFlow<EstadoNuminario1>(value = EstadoNuminario1.Cargando)
    val estado: StateFlow<EstadoNuminario1> = _estado.asStateFlow()

    // resultado final de la partida (null durante la partida y al finalizar se rellena)
    private val _resultado = MutableStateFlow<ResultadoJuegoContrarreloj?>(value = null)
    val resultado: StateFlow<ResultadoJuegoContrarreloj?> = _resultado.asStateFlow()

    // variables internas privadas (solo las usa el ViewModel)
    private var puntos = 0                         // puntuación acumulada (se calcula internamente y se vuelca al estado)

    private var fallos = 0                         // fallos acumulados (se calcula internamente y se vuelca al estado)

    private var tiempoRestante = DURACION_PARTIDA  // segundos que quedan en el temporizador desde el inicio

    private var jobCronometro: Job? = null         // job (referencia a una corrutina en ejecución) del temporizador

    private var reproductor: MediaPlayer? = null   // reproductor de música del juego

    // función para iniciar una partida nueva desde cero
    fun iniciarJuego() {
        // se reinician los controladores para empezar desde cero
        puntos = 0
        fallos = 0
        tiempoRestante = DURACION_PARTIDA

        iniciarMusica()           // se inicia la reproducción de la música de fondo
        iniciarTemporizador()     // se inicia el temporizador
        generarOperacion()        // se genera la primera operación
    }

    // función para generar una nueva operación aleatoria y actualiza el estado
    private fun generarOperacion() {
        val esSuma = (0..1).random() == 0  // variable para generar el tipo de operación (true para suma y falso para resta)

        // variables con los números para la operación
        val numero1: Int
        val numero2: Int

        if (esSuma) {
            // en la suma cualquiera de los dos números puede ser un valor dentro del rango
            numero1 = (RANGO_MIN..RANGO_MAX).random()
            numero2 = (RANGO_MIN..RANGO_MAX).random()
        }
        else {
            // en la resta se garantiza que numero1 >= numero2 para evitar números negativos
            numero2 = (RANGO_MIN..RANGO_MAX).random()
            numero1 = (numero2..RANGO_MAX).random()
        }

        // se actualiza el estado con todos los datos de la operación actual
        _estado.value = EstadoNuminario1.Jugando(
            numero1 = numero1,
            numero2 = numero2,
            esSuma = esSuma,
            puntos = puntos,
            fallos = fallos,
            tiempoRestante =  tiempoRestante,
            pista = null    // sin pista ya que es una operación nueva
        )
    }

    // función para comprobar la respuesta introducida por el usuario en el campo de texto
    fun comprobarRespuesta(respuesta: String) {

        // se comprueba que el estado actual corresponda al de jugando el juego (si no devuelve null)
        val estadoActual = _estado.value as? EstadoNuminario1.Jugando ?: return

        val respuestaUsuario = respuesta.toInt()  // se convierte el texto recibido a número entero

        // se calcula la respuesta correcta según el tipo de operación
        val respuestaCorrecta = if (estadoActual.esSuma) { estadoActual.numero1 + estadoActual.numero2 } else { estadoActual.numero1 - estadoActual.numero2 }

        if (respuestaUsuario == respuestaCorrecta) {
            // si el usuario ha acertado la operación, se le suma puntos y se genera una nueva operación
            puntos += PUNTOS_ACIERTO
            generarOperacion()
        }
        else {
            // si el usuario falla la operación

            fallos++  // se incrementa la cantidad de fallos

            tiempoRestante = maxOf(a = 0, b = tiempoRestante - PENALIZACION_TIEMPO)  // se penaliza avanzando el temporizador x segundos

            val pista = if (respuestaUsuario > respuestaCorrecta) { Pista.MAYOR } else { Pista.MENOR }  // se determina si el número introducido es mayor/menor que la respuesta correcta

            // se actualiza el estado con la pista y los nuevos datos
            _estado.value = estadoActual.copy(
                puntos = puntos,
                fallos = fallos,
                tiempoRestante =  tiempoRestante,
                pista = pista
            )

            if (tiempoRestante <= 0) finalizarJuego()  // si la penalización agotó el tiempo, se termina el juego
        }
    }


    // función para iniciar el temporizador de la cuenta atrás (segundos)
    private fun iniciarTemporizador() {
        jobCronometro?.cancel()  // cancelar el temporizador si existía anteriormente

        // se inicía el temporizador
        jobCronometro = viewModelScope.launch {
            // bucle infinito (hasta que la cuenta atrás llegue a cero)
            while(tiempoRestante >= 0) {
                delay(timeMillis = 1000)  // esperar 1 segundo

                tiempoRestante--  // se decrementa el temporizador

                // actualizar la interfaz de usuario si el juego sigue en marcha
                val actual = _estado.value
                if (actual is EstadoNuminario1.Jugando) {
                    _estado.value = actual.copy(tiempoRestante = tiempoRestante)
                }
            }

            finalizarJuego()  // se finaliza el juego a llegar a cero el temporizador (salir del bucle)
        }
    }

    // función para iniciar la música de fondo en bucle
    private fun iniciarMusica() {
        // solo inicia la música si no existe ya (evita reiniciar al repetir partida)
        if (reproductor == null) {
            reproductor = MediaPlayer.create(context, R.raw.musicamatematicas).apply {
                isLooping = true  // reproducción en bucle continuo
                start()
            }
        }
    }

    // función para detener y liberar el reproductor de música
    private fun detenerMusica() {
        reproductor?.stop()
        reproductor?.release() // se liberan los recursos de audio del sistema
        reproductor = null
    }

    // función para cuando se ha respondido todas las preguntas correspondientes
    private fun finalizarJuego() {
        jobCronometro?.cancel()  // se detiene el temporizador

        // se construye el resumen final de la partida
        _resultado.value = ResultadoJuegoContrarreloj(
            puntos = puntos,
            fallos = fallos
        )

        _estado.value = EstadoNuminario1.Terminado // se cambia el estado a juego terminado
    }

    // función de limpieza al destruir el ViewModel (cancelar todas las corrutinas para evitar problemas de memoria)
    override fun onCleared() {
        super.onCleared()
        jobCronometro?.cancel()
        detenerMusica()
    }
}