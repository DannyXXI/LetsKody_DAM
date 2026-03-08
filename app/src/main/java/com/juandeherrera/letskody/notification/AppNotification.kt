package com.juandeherrera.letskody.notification

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi

// clase que se ejecuta por primera vez al iniciar la aplicación para crear el canal de notificaciones
class AppNotification: Application() {

    @RequiresApi(value = Build.VERSION_CODES.O) // se indica que esta clase funciona en Android 8 o superior (API 26+)
    // función sobrescrita que se ejecutará automáticamente al iniciar la aplicación
    override fun onCreate() {
        super.onCreate()

        crearCanalNotificaciones() // se crea el canal de notificaciones (si no existe previamente)
    }

    @RequiresApi(value = Build.VERSION_CODES.O) // se indica que esta clase funciona en Android 8 o superior (API 26+)
    // función privada para crear el canal de notificaciones
    private fun crearCanalNotificaciones() {

        // se crea el canal de notificaciones
        val canal = NotificationChannel(
            "id_canal_notificaciones", // id único del canal que servirá como una dirección interna
            "Notificaciones",       // nombre visible para el usuario en los ajustes del sistema
            NotificationManager.IMPORTANCE_HIGH  // nivel de importancia -> sonido, vibración, se muestra en pantalla bloqueada y prioridad en notificaciones
        )

        // se obtiene el servicio del sistema encargado de gestionar las notificaciones
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        manager.createNotificationChannel(canal) // se registra el canal en el sistema
    }
}