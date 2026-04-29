package com.juandeherrera.letskody.notification

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.juandeherrera.letskody.R
import kotlin.random.Random

// clase encargada de crear y mostrar las notificaciones
class NotificationHandler(private val context: Context) {

    // se obtiene el controlador de notificaciones del sistema que nos permite mostrar, actualizar o eliminar notificaciones
    private val controladorNotificaciones = context.getSystemService(NotificationManager::class.java)

    // identificador único del canal de notificaciones que se usará en la aplicación
    private val canalID = "id_canal_notificaciones"

    // función para la creación de la notificación de la creación del usuario
    fun notificacionCreacionUsuario(nombre: String) {

        // builder que construye la notificación
        val notificacion = NotificationCompat.Builder(context, canalID)
            .setContentTitle("Usuario registrado") // título de la notificación
            .setContentText("$nombre te has registrado correctamente.")  // cuerpo de la notificación
            .setSmallIcon(R.drawable.ic_stat_kody)  // icono pequeño obligatorio que aparece en la barra de estado
            .setColor(0xFF017DB2.toInt())        // color de la barra lateral de la notificación
            .setAutoCancel(true)                 // hace que desaparezca la notificación cuando el usuario la pulse
            .build()                             // se construye la notificación

        // se envía la notificación al sistema (id aleatorio para que no sobrescriba otra notificación)
        controladorNotificaciones.notify(Random.nextInt(), notificacion)
    }

    // función para la creación de la notificación de la creación del usuario
    fun notificacionCreacionIncidencia() {

        // builder que construye la notificación
        val notificacion = NotificationCompat.Builder(context, canalID)
            .setContentTitle("Incidencia enviada") // título de la notificación
            .setContentText("Se ha enviado su incidencia al servicio técnico.")  // cuerpo de la notificación
            .setSmallIcon(R.drawable.ic_stat_kody)  // icono pequeño obligatorio que aparece en la barra de estado
            .setColor(0xFF017DB2.toInt())        // color de la barra lateral de la notificación
            .setAutoCancel(true)                 // hace que desaparezca la notificación cuando el usuario la pulse
            .build()                             // se construye la notificación

        // se envía la notificación al sistema (id aleatorio para que no sobrescriba otra notificación)
        controladorNotificaciones.notify(Random.nextInt(), notificacion)
    }
}