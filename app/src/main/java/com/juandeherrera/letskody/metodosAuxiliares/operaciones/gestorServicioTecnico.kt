package com.juandeherrera.letskody.metodosAuxiliares.operaciones

import com.google.firebase.firestore.FirebaseFirestore
import com.juandeherrera.letskody.firebase.MensajeServicioTecnico
import com.juandeherrera.letskody.localdb.AppDB

// función auxiliar para registrar los mensajes de incidencia del usuario en Firebase
fun registrarTicketServicioTecnico(uidUsuario: String, db: AppDB, asunto: String, descripcion: String, exito: () -> Unit, error: (String) -> Unit) {

    val dbfire = FirebaseFirestore.getInstance()  // instancia a la base de datos de Firebase asociada a la aplicación

    val mensaje = MensajeServicioTecnico(
        asunto = asunto,
        descripcion = descripcion,
        usuario = uidUsuario,
        estado = "Pendiente",
        fecha = obtenerFechaCompletaActual()
    )

    // se almacena el mensaje de incidencia del usuario de Firebase en la colección 'servicioTecnico' como un documento identificado por su UID
    dbfire.collection("servicioTecnico").add(mensaje)
        .addOnSuccessListener {
            // si se realizó correctamente el registro de la incidencia, se obtiene el tiempo en el que se ha realizado el envío (ms)
            val marcaTemporal = System.currentTimeMillis()

            // se actualiza el campo de cuando el usuario realizo su último envío en Firebase
            dbfire.collection("usuarios").document(uidUsuario).update("ultimoEnvioTicket", marcaTemporal)
                .addOnSuccessListener {
                    // si se realizó correctamente la actualización en Firebase, se sincroniza con la base de datos local
                    db.usuarioDao().actualizarUltimoEnvioTicket(uid = uidUsuario, marca = marcaTemporal)

                    exito()
                }
                .addOnFailureListener { ex ->
                    // si ocurre algún error, se muestra por consola el error y se manda un mensaje al usuario
                    println("Error al actualizar la marca temporal del usuario en Firebase: ${ex.message}")
                    error("No se logró actualizar la marca temporal.")
                }
        }
        .addOnFailureListener { ex ->
            // si ocurre algún error, se muestra por consola el error y se manda un mensaje al usuario
            println("Error al guardar la incidencia en Firebase: ${ex.message}")
            error("No se logró registrar la incidencia.")
        }
}