package com.juandeherrera.letskody.clasesAuxiliares

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// clase encargada de detectar la conexión a Internet del usuario en tiempo real
class DetectorRed (context: Context) {

    // se obtiene el servicio del sistema que gestiona las conexiones
    private val conexionManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // estado interno mutable que indica si existe conexion a Internet (solo modificable desde la propia clase)
    private val existeConexion = MutableStateFlow(value = comprobarConexion())

    // estado público observable de solo lectura del estado de la conexion a Internet
    val hayInternet: StateFlow<Boolean> = existeConexion

    // bloque de código que se ejecutará inicialmente al crear una instancia de la clase
    init {
        // función callback que se ejecuta cuando cambia el estado de red
        val callback = object : ConnectivityManager.NetworkCallback() {

            // si hay conexion disponible
            override fun onAvailable(network: Network){
                existeConexion.value = true
            }

            // si no hay conexion disponible
            override fun onLost(network: Network){
                existeConexion.value = false
            }

        }

        // se registra el callback para escuchar cambios en la red en tiempo real
        conexionManager.registerDefaultNetworkCallback(callback)
    }

    // función interna para comprobar el estado de la conexion al arrancar la aplicación
    private fun comprobarConexion(): Boolean {

        // se obtiene la red activa actual del dispositivo, si no hay ninguna, no hay conexion
        val red = conexionManager.activeNetwork ?: return false

        // se obtienen las capacidades de esa red, si no hay ninguna, no hay conexion
        val capacidades = conexionManager.getNetworkCapabilities(red) ?: return false

        // se comprueba si la red tiene la capacidad de acceso a Internet
        return capacidades.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}