package com.juandeherrera.letskody.viewModels.euroBanderas

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juandeherrera.letskody.R
import com.juandeherrera.letskody.clasesAuxiliares.ResultadoJuegoCronometro
import com.juandeherrera.letskody.localdb.AppDB
import com.juandeherrera.letskody.localdb.BanderasEuropaData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// CONSTANTES DEL JUEGO
private const val TOTAL_PREGUNTAS = 12            // número de banderas que se mostrarán en la partida
private const val PUNTOS_ACIERTO = 100            // puntos que se suma al acertar la bandera
private const val PUNTOS_FALLO = -50              // puntos que se restan al fallar una bandera
private const val PENALIZACION_FALLO = 10         // segundos extras que se agregan al tiempo final por cada fallo
private const val TIEMPO_INACTIVIDAD = 30         // segundos sin tocar la pantalla antes de mostrar el aviso por inactividad
private const val TIEMPO_ALERTA_INACTIVIDAD = 30  // segundos que tiene el usuario para reaccionar al aviso por inactividad

// clase ViewModel que contiene la lógica del juego
// se recomienda porque sobrevive a cambios de configuración y separa la lógica de la interfaz gráfica
class EuroBanderasViewModel (private val db: AppDB, private val context: Context) : ViewModel() {

    // variables públicas que observa la pantalla
    // con StateFlow hace que sea un flujo de datos reactivo (si el valor cambia, todos los Composables que lo estén observando se recomponen)
    // las variables privadas _nombre solo las puede modificar el ViewModel y las publicas para que la pantalla solo pueda leer su valor

    // estado general del juego
    private val _estado = MutableStateFlow<EstadoEuroBanderas>(value = EstadoEuroBanderas.Cargando)
    val estado: StateFlow<EstadoEuroBanderas> = _estado.asStateFlow()

    // controla si el modal de inactividad está visible o no
    private val _mostrarModalInactividad = MutableStateFlow(value = false)
    val mostrarModalInactividad = _mostrarModalInactividad.asStateFlow()

    // segundos restantes de la cuenta atrás dentro del modal de inactividad
    private val _cuentaAtrasInactividad = MutableStateFlow(value = TIEMPO_INACTIVIDAD)
    val cuentaAtrasInactividad: StateFlow<Int> = _cuentaAtrasInactividad.asStateFlow()

    // resultado final de la partida (null durante la partida y al finalizar se rellena)
    private val _resultado = MutableStateFlow<ResultadoJuegoCronometro?>(value = null)
    val resultado: StateFlow<ResultadoJuegoCronometro?> = _resultado.asStateFlow()

    private var reproductor: MediaPlayer? = null  // reproductor de música del juego

    // variables internas privadas (solo las usa el ViewModel)
    private var banderas: List<BanderasEuropaData> = emptyList()  // lista de las 12 banderas seleccionadas aleatoriamente

    private var indicePregunta = 0  // índice de la pregunta que se está mostrando actualmente

    private var puntos = 0  // puntuación acumulada (se calcula internamente y se vuelca al estado)

    private var tiempoCronometro = 0  // segundos totales transcurridos desde el inicio (sin penalizaciones)

    private var penalizacion = 0   // segundos acumulados por fallos durante la partida

    private var jobCronometro: Job? = null  // job (referencia a una corrutina en ejecución) del cronómetro principal

    private var jobInactividad: Job? = null  // job (referencia a una corrutina en ejecución) del temporizador de inactividad

    private var jobCuentaAtras: Job? = null  // job (referencia a una corrutina en ejecución) de la cuenta atrás dentro del modal de inactividad

    private var esperandoSiguiente = false   // flag para evitar que el usuario pulse dos veces (true cuando el usuario responde y vuelve a falso cuando se carga la siguiente pregunta)

    // función para iniciar una partida nueva desde cero
    fun iniciarJuego() {
        // se abre una corrutina que existe mientras este el ViewModel, ya que al salir de la pantalla, este se destruye y la corrutina se cancela sola
        viewModelScope.launch(context = Dispatchers.IO) {
            _estado.value = EstadoEuroBanderas.Cargando  // se pone la pantalla de carga

            val todasBanderas = db.banderasEuropaDao().getListaBanderasEuropa() // se lee todas las banderas de la base de datos

            // se comprueba si el número de banderas es menor a las preguntas que se harán
            if (todasBanderas.size < TOTAL_PREGUNTAS) {
                _estado.value = EstadoEuroBanderas.ErrorSinBanderas  // se cambia el estado
                return@launch                                 // se sale de la corrutina sin continuar
            }

            banderas = todasBanderas.shuffled().take(n = TOTAL_PREGUNTAS) // se mezclan las banderas y se obtienen las 12 primeras

            // se reinician los controladores para empezar desde cero
            indicePregunta = 0
            puntos = 0
            tiempoCronometro = 0
            penalizacion = 0
            esperandoSiguiente = false

            withContext(Dispatchers.Main) {
                iniciarMusica()          // se inicia la reproducción de la música de fondo
                iniciarCronometro()      // se inicia el cronómetro
                mostrarPregunta()        // se carga la primera pregunta en la pantalla
                iniciarJobInactividad()  // se inicia el job de inactividad
            }
        }
    }

    // función para cargar el estado de pregunta correspondiente al índice actual
    private fun mostrarPregunta() {
        // se comprueba si ya se superaron el límite de preguntas para terminar
        if (indicePregunta >= TOTAL_PREGUNTAS) {
            finalizarJuego()
            return
        }

        val bandera = banderas[indicePregunta]  // se obtiene la bandera para la pregunta actual

        // se mezclan las 4 opciones aleatoriamente
        val opciones = listOf(bandera.opcion1, bandera.opcion2, bandera.opcion3, bandera.opcion4).shuffled()

        // se actualiza el estado con todos los datos de la pregunta actual
        _estado.value = EstadoEuroBanderas.Jugando(
            preguntaActual = bandera,
            opciones = opciones,
            numeroPregunta = indicePregunta + 1,
            puntos = puntos,
            tiempoCronometro = tiempoCronometro,
            respuestaSeleccionada = null, // null ya que no se ha respondido
            opcionCorrecta = bandera.opcionCorrecta,
            penalizacionAcumulada = penalizacion

        )
    }

    // función para comprobar la respuesta del jugador
    fun responder (opcion: String) {

        if (esperandoSiguiente) return  // si ya respondió esta pregunta, se ignora (evita doble pulsación)

        // se comprueba que el estado actual corresponda al de jugando el juego (si no devuelve null)
        val estadoActual = _estado.value as? EstadoEuroBanderas.Jugando ?: return

        esperandoSiguiente = true    // se bloquea por si hay más pulsaciones hasta la siguiente pregunta
        detenerJobInactividad()      // el usuario acaba de interactuar, ya no necesitamos el aviso

        val esCorrecta = opcion == estadoActual.opcionCorrecta // se comprueba si el jugador ha acertado

        // se ajusta la puntuación en función del resultado del jugador
        if (esCorrecta) {
            puntos += PUNTOS_ACIERTO  // se suman los puntos
        }
        else {
            // se restan los puntos y se añaden los segundos de penalización
            puntos += PUNTOS_FALLO
            penalizacion += PENALIZACION_FALLO
        }

        // se actualiza el estado con la respuesta seleccionada
        _estado.value = estadoActual.copy(
            respuestaSeleccionada = opcion,
            puntos = puntos,
            penalizacionAcumulada = penalizacion
        )

        // se espera 1.5 segundos para que el usuario vea la corrección y se avanza
        viewModelScope.launch {
            delay(timeMillis = 1500)  // pausa de 1.5 segundos

            indicePregunta++  // se avanza al siguiente índice de pregunta

            esperandoSiguiente = false  // ya se puede responder de nuevo

            if (indicePregunta >= TOTAL_PREGUNTAS) {
                finalizarJuego()  // si es la última pregunta se termina
            }
            else {
                mostrarPregunta()       // se carga la siguiente pregunta
                iniciarJobInactividad() // se reinicia el detector de inactividad para la nueva pregunta
            }
        }
    }

    // función para iniciar el cronómetro principal que mide el tiempo total de la partida (segundos)
    private fun iniciarCronometro() {
        jobCronometro?.cancel()  // cancelar el cronometro si existía anteriormente

        // se inicía el cronómetro
        jobCronometro = viewModelScope.launch {
            // bucle infinito (hasta que se cancele el Job)
            while(true) {
                delay(timeMillis = 1000)  // esperar 1 segundo

                tiempoCronometro++  // se incrementa el cronómetro

                // actualizar la interfaz de usuario si el juego sigue en marcha
                val actual = _estado.value
                if (actual is EstadoEuroBanderas.Jugando) {
                    _estado.value = actual.copy(tiempoCronometro = tiempoCronometro)
                }
            }
        }
    }

    // función para detectar un toque en la pantalla (evitar la inactividad)
    fun registrarActividad() {
        if (esperandoSiguiente) return // si el usuario acaba de responder se ignora
        reiniciarJobInactividad()      // cualquier toque reinicia el contador de inactividad
    }

    // función para cancelar el temporizador de inactividad actual y volverlo a iniciar desde cero
    private fun reiniciarJobInactividad() {
        detenerJobInactividad()
        iniciarJobInactividad()
    }

    // función para iniciar el temporizador de inactividad
    fun iniciarJobInactividad() {
        detenerJobInactividad()  // asegurarse de que no hay otro temporizador corriendo

        // se inicia el temporizador
        jobInactividad = viewModelScope.launch {
            delay(timeMillis = TIEMPO_INACTIVIDAD * 1000L) // esperar el tiempo determinado
            mostrarModalInactividad()                      // mostrar el aviso si llega aquí
        }
    }

    // función para cancelar el temporizador de inactividad y liberar la referencia
    private fun detenerJobInactividad() {
        jobInactividad?.cancel()  // cancelar la corrutina si existe
        jobInactividad = null     // limpiar la referencia
    }

    // función para mostrar el modal de aviso de inactividad e iniciar su cuenta atrás interna
    private fun mostrarModalInactividad() {
        _mostrarModalInactividad.value = true  // se muestra el modal

        _cuentaAtrasInactividad.value = TIEMPO_ALERTA_INACTIVIDAD  // tiempo que tiene el usuario para cancelar el modal

        jobCuentaAtras?.cancel()  // cancelar cuenta atrás anterior si existía

        // iniciar la cuenta atrás del modal
        jobCuentaAtras = viewModelScope.launch {
            // se cuentan x iteraciones de 1 segundo cada una
            repeat(times = TIEMPO_ALERTA_INACTIVIDAD) {
                delay(timeMillis = 1000)
                _cuentaAtrasInactividad.value -= 1  // decrementar el contador visible
            }
        }
    }

    // función para cerrar el modal de inactivada cuando el usuario pulsa el botón correspondiente
    fun cerrarModalInactividad() {
        jobCuentaAtras?.cancel()  // se detiene la cuenta atrás
        _mostrarModalInactividad.value  = false  // se oculta el modal
        _cuentaAtrasInactividad.value = TIEMPO_ALERTA_INACTIVIDAD  // se reinicia la cuenta atrás
        reiniciarJobInactividad()  // el usuario sigue jugando
    }

    // función para iniciar la música de fondo en bucle
    private fun iniciarMusica() {
        // solo inicia la música si no existe ya (evita reiniciar al repetir partida)
        if (reproductor == null) {
            reproductor = MediaPlayer.create(context, R.raw.musicageografia).apply {
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
        jobCronometro?.cancel()  // se detiene el cronómetro
        detenerJobInactividad()  // se detiene el detector de inactividad

        // se construye el resumen final de la partida
        _resultado.value = ResultadoJuegoCronometro(
            puntos = puntos,
            tiempoBase = tiempoCronometro,
            penalizacion = penalizacion,
            tiempoTotal = tiempoCronometro + penalizacion
        )

        _estado.value = EstadoEuroBanderas.Terminado // se cambia el estado a juego terminado
    }

    // función para limpiar todos los recursos del juego cuando el usuario salga de la pantalla
    fun limpiar() {
        jobCronometro?.cancel()
        jobInactividad?.cancel()
        jobCuentaAtras?.cancel()
        detenerMusica()
    }

    // función de limpieza al destruir el ViewModel (cancelar todas las corrutinas para evitar problemas de memoria)
    override fun onCleared() {
        super.onCleared()
        limpiar()
    }
}