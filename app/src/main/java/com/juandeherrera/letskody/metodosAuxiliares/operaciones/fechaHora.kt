package com.juandeherrera.letskody.metodosAuxiliares.operaciones

import java.time.LocalDate

// función auxiliar para obtener la edad del usuario
fun calcularEdad(fechaNacimiento: String): Int {
    try{
        val partes = fechaNacimiento.split("/")  // se divide la fecha de nacimiento en una lista

        // se guarda cada parte de la fecha de nacimiento como un entero
        val dia = partes[0].toInt()
        val mes = partes[1].toInt()
        val anio = partes[2].toInt()

        val fechaHoy = LocalDate.now()  // se obtiene la fecha actual

        var edad = fechaHoy.year - anio // se obtiene la edad

        // se comprueba si el usuario cumplió años este año (si no, se le resta uno)
        if ( fechaHoy.monthValue < mes || (fechaHoy.monthValue == mes && fechaHoy.dayOfMonth < dia) ) { edad-- }

        return edad  // se devuelve la edad del usuario
    }
    catch (ex: Exception){
        // si ocurre algún error, se muestra el mensaje por terminal y se devuelve -1
        println("Error al calcular la edad del usuario: ${ex.message}")
        return -1
    }
}