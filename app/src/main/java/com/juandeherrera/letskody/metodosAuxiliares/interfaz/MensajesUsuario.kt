package com.juandeherrera.letskody.metodosAuxiliares.interfaz

// función auxiliar para mostrar el tipo mensaje según el tiempo de espera tras enviar el ticket al servicio técnico
fun mensajeTiempoRestante(msRestantes: Long): String {

    val segundos = (msRestantes / 1000).toInt()  // se obtienen los segundos restantes

    // se devuelve el tipo de mensaje en función del tiempo de espera
    when {
        segundos >= 120 -> {
            val minutos = segundos / 60  // se obtienen los minutos restantes

            return "Faltan $minutos minutos para enviar otro mensaje."
        }
        segundos >= 60 -> {
            return "Falta 1 minuto para enviar otro mensaje."
        }
        else -> {
            return "Falta menos de 1 minuto para enviar otro mensaje."
        }
    }
}