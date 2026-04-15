package com.juandeherrera.letskody.metodosAuxiliares.operaciones.juegos

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.juandeherrera.letskody.firebase.PuntuacionEuroBanderasFirebase
import com.juandeherrera.letskody.localdb.AppDB
import com.juandeherrera.letskody.localdb.PuntuacionEuroBanderasData
import java.util.UUID

// se encarga de guardar o actualizar la puntuación del usuario al terminar una partida en la base de datos
object GestorPuntuacionEuroBanderas {

    private const val COLECCION_FIREBASE = "puntuaciones_EuroBanderas"  // nombre de la colección en Firebase

    // función principal encargada de guardar o actualizar la puntuación en local y lanza en paralelo la misma operacion en Firebase
    fun guardarPuntuacion(db: AppDB, uidUsuario: String, puntos: Int, tiempoTotal: Int) : AccionPuntuacion {

        // se realiza la acción en base de datos local y se obtiene el tipo de acción
        val accion = guardarEnLocal(db = db, uidUsuario = uidUsuario, puntos = puntos, tiempoTotal = tiempoTotal)

        // se realiza la acción en Firebase
        guardarEnFirebase(uidUsuario = uidUsuario, puntos = puntos, tiempoTotal = tiempoTotal, accion = accion)

        return accion  // se devuelve la acción para que se muestre en pantalla el mensaje correspondiente
    }

    // función encargada de la gestión de la puntuación en la base de datos local
    private fun guardarEnLocal (db: AppDB, uidUsuario: String, puntos: Int, tiempoTotal: Int) : AccionPuntuacion {

        val existente = db.puntuacionEuroBanderasDao().getPuntuacionEuroBanderas(uidUsuario = uidUsuario)  // se obtiene el registro de puntuación existente

        if (existente == null) {
            // si no hay ningún registro previo, se inserta
            db.puntuacionEuroBanderasDao().nuevaPuntuacionEuroBanderas(
                PuntuacionEuroBanderasData(
                    uidPuntosEuroBanderas = UUID.randomUUID().toString(),
                    puntos = puntos,
                    tiempo = tiempoTotal,
                    usuario = uidUsuario
                )
            )

            return AccionPuntuacion.INSERTADA
        }
        else {
            // se comprueba si se ha mejorado la puntuación o el tiempo
            val mejorPuntuacion = puntos > existente.puntos
            val mejorTiempo = tiempoTotal < existente.tiempo

            if (mejorPuntuacion || mejorTiempo) {
                // si ha habido alguna mejora en la puntuacion o tiempo, se actualiza
                db.puntuacionEuroBanderasDao().actualizarPuntuacionesEuroBanderas(
                    existente.copy(
                        puntos = if (mejorPuntuacion) puntos else existente.puntos,
                        tiempo = if (mejorTiempo) tiempoTotal else existente.tiempo
                    )
                )

                return AccionPuntuacion.ACTUALIZADA
            }
            else {
                return AccionPuntuacion.SIN_CAMBIOS  // si no hay cambios, no se modifica nada
            }
        }
    }

    // función encargada de la gestión de la puntuación en Firebase
    private fun guardarEnFirebase(uidUsuario: String, puntos: Int, tiempoTotal: Int, accion: AccionPuntuacion) {

        val dbfire = FirebaseFirestore.getInstance()  // instancia a la base de datos de Firebase asociada a la aplicación

        when (accion) {
            // si no hay ningún registro previo, se inserta
            AccionPuntuacion.INSERTADA -> {
                // se crea el documento con todos los campos
                val datos = PuntuacionEuroBanderasFirebase(
                    puntos = puntos,
                    tiempo = tiempoTotal,
                    usuario = uidUsuario
                )

                // se inserta el documento en Firebase
                dbfire.collection(COLECCION_FIREBASE).document(uidUsuario).set(datos)
                    .addOnFailureListener { ex ->
                        println("Error al crear la puntuación Euro-banderas en Firebase: ${ex.message}")
                    }
            }
            // si existe y hay alguna mejora, se actualizarán los campos mejorados
            AccionPuntuacion.ACTUALIZADA -> {
                // se obtiene el documento actual de Firebase
                dbfire.collection(COLECCION_FIREBASE).document(uidUsuario).get()
                    .addOnSuccessListener { document ->
                        // si funciona se extrae la puntuación de Firebase a modificar
                        val puntuacionFirebase = document.toObject(PuntuacionEuroBanderasFirebase::class.java)

                        if (puntuacionFirebase != null) {

                            // se comprueba si se ha mejorado la puntuación o el tiempo
                            val mejorPuntuacion = puntos > puntuacionFirebase.puntos!!
                            val mejorTiempo = tiempoTotal < puntuacionFirebase.tiempo!!

                            // se crea una copia de la puntuación de Firebase con los datos actualizados
                            val puntuacionFirebaseActualizado = puntuacionFirebase.copy(
                                puntos = if (mejorPuntuacion) puntos else puntuacionFirebase.puntos,
                                tiempo = if (mejorTiempo) tiempoTotal else puntuacionFirebase.tiempo
                            )

                            // se actualiza la puntuación en la base de datos de Firebase
                            dbfire.collection(COLECCION_FIREBASE).document(document.id).set(puntuacionFirebaseActualizado, SetOptions.merge())
                                .addOnFailureListener { ex ->
                                    println("Error al actualizar la puntuación Euro-banderas en Firebase: ${ex.message}")
                                }
                        }
                    }
                    .addOnFailureListener { ex ->
                        println("Error al obtener la puntuación Euro-banderas en Firebase: ${ex.message}")
                    }
            }
            // si existe, pero no hay ninguna mejora, no se hace nada
            AccionPuntuacion.SIN_CAMBIOS -> {}
        }
    }
}

// enumeración que devuelve el mensaje a mostrar tras guardar la puntuación
enum class AccionPuntuacion {
    INSERTADA,  // primera vez que el usuario termina el juego

    ACTUALIZADA,  // cuando el usuario mejora su puntuación o su tiempo

    SIN_CAMBIOS  // cuando el usuario no mejora su puntuación ni su tiempo
}