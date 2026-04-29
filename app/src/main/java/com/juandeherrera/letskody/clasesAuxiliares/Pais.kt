package com.juandeherrera.letskody.clasesAuxiliares

// clase para representar los datos de un país
data class Pais (
    val nombre: String,
    val prefijo: String,
    val bandera: String  // emoticono de la bandera
)

// lista inmutable con los datos de los países para el prefijo telefónico
val paises = listOf(
    Pais(nombre = "España", prefijo = "+34", bandera = "🇪🇸"),
    Pais(nombre = "Francia", prefijo = "+33", bandera = "🇫🇷"),
    Pais(nombre = "Alemania", prefijo = "+49", bandera = "🇩🇪"),
    Pais(nombre = "Italia", prefijo = "+39", bandera = "🇮🇹"),
    Pais(nombre = "Portugal", prefijo = "+351", bandera = "🇵🇹")
)