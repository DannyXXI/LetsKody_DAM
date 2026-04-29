package com.juandeherrera.letskody.firebase

// clase para gestionar los datos del mensaje destinado al supuesto servicio técnico
data class MensajeServicioTecnico (
    val asunto: String? = null,
    val descripcion: String? = null,
    val usuario: String? = null,
    val estado: String? = null,
    val fecha: String? = null
)