package com.juandeherrera.letskody.clasesAuxiliares

// clase para representar los datos de los mensajes en el chatbot de asistencia
data class MensajeChat (
    val texto: String,      // contenido textual del mensaje
    val esUsuario: Boolean  // comprueba si lo ha escrito el usuario (true) o el asistente (false)
)

// clase para representar los datos del estado del chat
data class ChatEstado(
    val mensajes: List<MensajeChat> = emptyList(),   // lista completa de mensajes de la conversación (vacía al inicio)
    val cargando: Boolean = false,                   // comprueba si Gemini está procesando la respuesta (true)
    val error: String? = null                        // mensaje de error para el Snackbar (null si no hay error)
)