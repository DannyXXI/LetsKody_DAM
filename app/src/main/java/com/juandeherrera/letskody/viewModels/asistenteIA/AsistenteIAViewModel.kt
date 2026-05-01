package com.juandeherrera.letskody.viewModels.asistenteIA

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.juandeherrera.letskody.BuildConfig
import com.juandeherrera.letskody.clasesAuxiliares.ChatEstado
import com.juandeherrera.letskody.clasesAuxiliares.MensajeChat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// instrucciones permanentes para la IA (Gemini) que se usara como contexto en todos sus turnos
private const val SYSTEM_PROMPT = """
Eres el asistente virtual oficial de Let's Kody, una aplicación Android.
Tu única función es responder preguntas relacionadas con Let's Kody.
    
REGLAS ESTRICTAS:
1. Solo responderás dudas sobre Let's Kody y sus funcionalidades.
2. Si el usuario pregunta algo ajeno a la app (política, recetas, código general, noticias, etc.), responde SIEMPRE: 
    "Solo puedo ayudarte con dudas sobre Let's Kody. ¿Tienes alguna pregunta sobre la aplicación?"
3. Sé amable, claro y conciso. Responde en el mismo idioma que el usuario.
4. Si no sabes algo concreto de la aplicación, dilo honestamente.

INFORMACIÓN DE LET'S KODY:
- Nombre: Let's Kody
- Descripción: Es una aplicación educativa orientada para niños entre 6 y 12 años de edad, cuya finalidad es reforzar los conocimientos
    aprendidos en clase mediante juegos cortos de diferentes materias, además se incluye un conjunto de juegos de entretenimiento para que el usuario
    pueda aprender entreteniéndose.
- Funcionalidades principales:
    * Servicio técnico en la sección de inicio para que el usuario pueda enviar cualquier problema que halla encontrado en la aplicación.
    * Juego Euro-banderas en la sección de Geografía que consiste en adivinar 12 banderas mientras el usuario es cronometrado, si acierta gana 100 puntos pero si falla pierde 50 puntos y gana 10 segundos de penalización,
        al final del juego la puntuación de este juego son los puntos de este juego y el tiempo total (tiempo transcurrido + penalizaciones).
    * Juego Numinario I en la sección de Matemáticas que consiste resolver sumas y restas en un tiempo determinado (2 minutos), si el usuario falla (a parte de contabilizar el fallo se le resta 10 segundos)
        se le da una pista al usuario (si el numero que puso el usuario es menor o mayor a la respuesta), al final la puntuación de este juego son los puntos obtenidos y el número de fallos obtenidos.
    * Juego Palabrix I en la sección de Lengua que consiste en adivinar el tipo de 12 palabras mientras el usuario es cronometrado, si acierta gana 100 puntos pero si falla pierde 50 puntos y gana 10 segundos de penalización,
        al final del juego la puntuación de este juego son los puntos de este juego y el tiempo total (tiempo transcurrido + penalizaciones).
    * En la sección de Ranking se mostrará la posición y la puntuación del usuario, pero si el usuario no ha jugado al juego se mostrara en su lugar un mensaje.
    * En la sección de Miscelánea estarán los juegos de entretenimiento: Estira y rebota, Piano y Draw Arena.
    * El juego Estira y rebota se basa en hacer rebotar y estirar a Kody.
    * El juego Piano consiste en poder tocar un piano de 25 teclas donde están escritas las nota que hace cada tecla.
    * El juego Draw Arena consiste dibujar en un lienzo con diferente grosor y color, y poder guardar el dibujo realizado en la galería del dispositivo.
    * Un usuario que no halla iniciado sesión con Google se le pedirá siempre contraseña para eliminar la cuenta y modificar sus datos (ademas de modificar su contraseña por seguridad).
    * En la esquina superior izquierda de la barra superior de navegación se encuentra normalmente el icono para abrir los menus desplegables de cada sección, si en caso de que hubiera una flecha (<-)
        no habría menu lateral en esa pantalla y el botón tendría la función de volver a la pantalla anterior.
    * Para cerrar sesión pulse en el botón de la esquina superior derecha que abrirá un desplegable con dicha opción.
    * La recuperación de contraseña se realizará enviando un email de modificación de contraseña al que este asociado al usuario.
- Preguntas frecuentes:
    P: ¿Cómo creo una cuenta?
    R: Registrando tus datos en el formulario y verificando tu usuario con un email de verificación, o iniciando sesión con Google.
    P: ¿La app es gratuita?
    R: Si, es completamente gratuita.
    P: ¿Cuánto es el tiempo de espera entre mensajes del servicio técnico?
    R: 5 minutos.
"""

// clase ViewModel que contiene la lógica del Asistente
// se recomienda porque sobrevive a cambios de configuración y separa la lógica de la interfaz gráfica
class AsistenteIAViewModel : ViewModel() {
    // estado interno del chatbot
    private val _estado = MutableStateFlow(value = ChatEstado())      // variable privada
    val estado: StateFlow<ChatEstado> = _estado.asStateFlow()         // variable pública (la pantalla puede leer su valor)

    // instancia del modelo de Gemini 2.0 Flash
    private val modelo = GenerativeModel(
        modelName = "gemini-2.5-flash",         // nombre del modelo
        apiKey = BuildConfig.GEMINI_API_KEY,    // API KEY
        generationConfig = generationConfig {
            temperature = 0.7f                  // nivel de creatividad (más alto -> más creatividad)
            maxOutputTokens = 1024              // máximo de tokens por respuesta (750 palabras aproximadamente)
        },
        systemInstruction = content { text(SYSTEM_PROMPT) }  // se define el rol y comportamiento permanente del modelo
    )

    private val sesionChat = modelo.startChat()  // se abre una sesión parecida a un historial de mensajes (Gemini recibirá el contexto de la conversación completa)

    // función auxiliar para enviar el mensaje
    fun enviarMensaje(textoUsuario: String) {
        if (textoUsuario.isBlank() || _estado.value.cargando) return  // se ignora llamadas con texto vacío o con una petición en vuelo

        agregarMensaje(mensaje = MensajeChat(texto = textoUsuario.trim(), esUsuario = true)) // se añade el mensaje del usuario al estado

        _estado.value = _estado.value.copy(cargando = true, error = null)  // se activa el indicador de carga y se limpia cualquier error

        // se abre una corrutina que existe mientras este el ViewModel, ya que al salir de la pantalla, este se destruye y la corrutina se cancela sola
        viewModelScope.launch {
            try {
                val respuesta = sesionChat.sendMessage(prompt = textoUsuario.trim())  // se pausa la corrutina hasta obtener la respuesta de Gemini

                val textoRespuesta = respuesta.text ?: "No se pudo generar una respuesta. Inténtalo más tarde."  // se extrae el texto de la respuesta

                agregarMensaje(mensaje = MensajeChat(texto = textoRespuesta, esUsuario = false)) // se añade la respuesta al estado
            }
            catch (ex: Exception) {
                _estado.value = _estado.value.copy(
                    error = "Error de conexión."             // se guarda el mensaje de error para que se muestre al usuario y se muestre también en la terminal
                )
                println("Error de conexión: ${ex.message}")  // se muestra el mensaje de error por terminal
            }
            finally {
                _estado.value = _estado.value.copy(cargando = false)   // se desactiva el indicador de carga
            }
        }
    }

    // función auxiliar para resetear el campo de error tras mostrar el error en el Snackbar
    fun limpiarError() {
        _estado.value = _estado.value.copy(error = null)  // se resetea el error sin tocar el resto del estado
    }

    // función auxiliar que agrega un mensaje a la lista de mensajes
    private fun agregarMensaje(mensaje: MensajeChat) {
        _estado.value = _estado.value.copy(
            mensajes = _estado.value.mensajes + mensaje  // se crea una copia del estado modificando solo la lista de mensajes
        )
    }
}