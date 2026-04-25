package com.juandeherrera.letskody.metodosAuxiliares.operaciones.juegos

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.juandeherrera.letskody.clasesAuxiliares.AccionPuntuacion
import com.juandeherrera.letskody.firebase.PuntuacionNuminario1Firebase
import com.juandeherrera.letskody.localdb.AppDB
import com.juandeherrera.letskody.localdb.PuntuacionNuminario1Data
import java.util.UUID

// se encarga de guardar o actualizar la puntuación del usuario al terminar una partida en la base de datos
object GestorPuntuacionNuminario1 {

    private const val COLECCION_FIREBASE = "puntuaciones_Numinario1"  // nombre de la colección en Firebase

    // función principal encargada de guardar o actualizar la puntuación en local y lanza en paralelo la misma operacion en Firebase
    fun guardarPuntuacion(db: AppDB, uidUsuario: String, puntos: Int, fallos: Int) {

        // se realiza la acción en base de datos local y se obtiene el tipo de acción
        val accion = guardarEnLocal(db = db, uidUsuario = uidUsuario, puntos = puntos, fallos = fallos)

        // se realiza la misma acción en Firebase
        guardarEnFirebase(uidUsuario = uidUsuario, puntos = puntos, fallos = fallos, accion = accion)
    }

    // función encargada de la gestión de la puntuación en la base de datos local
    private fun guardarEnLocal (db: AppDB, uidUsuario: String, puntos: Int, fallos: Int) : AccionPuntuacion {

        val existente = db.puntuacionNuminario1Dao().getPuntuacionNuminario1(uidUsuario = uidUsuario)  // se obtiene el registro de puntuación existente

        if (existente == null) {
            // si no hay ningún registro previo, se inserta
            db.puntuacionNuminario1Dao().nuevaPuntuacionNuminario1(
                PuntuacionNuminario1Data(
                    uidPuntosNuminario1 = UUID.randomUUID().toString(),
                    puntos = puntos,
                    fallos = fallos,
                    usuario = uidUsuario
                )
            )

            return AccionPuntuacion.INSERTADA
        }
        else {
            // se comprueba si se ha mejorado la puntuación o los fallos
            val mejorPuntuacion = puntos > existente.puntos
            val mejorFallos = fallos < existente.fallos

            if (mejorPuntuacion || mejorFallos) {
                // si ha habido alguna mejora en la puntuacion o en los fallos, se actualiza
                db.puntuacionNuminario1Dao().actualizarPuntuacionesNuminario1(
                    existente.copy(
                        puntos = if (mejorPuntuacion) puntos else existente.puntos,
                        fallos = if (mejorFallos) fallos else existente.fallos
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
    private fun guardarEnFirebase(uidUsuario: String, puntos: Int, fallos: Int, accion: AccionPuntuacion) {

        val dbfire = FirebaseFirestore.getInstance()  // instancia a la base de datos de Firebase asociada a la aplicación

        when (accion) {
            // si no hay ningún registro previo, se inserta
            AccionPuntuacion.INSERTADA -> {
                // se crea el documento con todos los campos
                val datos = PuntuacionNuminario1Firebase(
                    puntos = puntos,
                    fallos = fallos,
                    usuario = uidUsuario
                )

                // se inserta el documento en Firebase
                dbfire.collection(COLECCION_FIREBASE).add(datos)
                    .addOnFailureListener { ex ->
                        println("Error al crear la puntuación de Numinario 1 del usuario en Firebase: ${ex.message}")
                    }
            }
            // si existe y hay alguna mejora, se actualizarán los campos mejorados
            AccionPuntuacion.ACTUALIZADA -> {
                // se obtiene el documento actual de Firebase
                dbfire.collection(COLECCION_FIREBASE).document(uidUsuario).get()
                    .addOnSuccessListener { document ->
                        // si funciona se extrae la puntuación de Firebase a modificar
                        val puntuacionFirebase = document.toObject(PuntuacionNuminario1Firebase::class.java)

                        if (puntuacionFirebase != null) {

                            // se comprueba si se ha mejorado la puntuación o el tiempo
                            val mejorPuntuacion = puntos > puntuacionFirebase.puntos!!
                            val mejorFallos = fallos < puntuacionFirebase.fallos!!

                            // se crea una copia de la puntuación de Firebase con los datos actualizados
                            val puntuacionFirebaseActualizado = puntuacionFirebase.copy(
                                puntos = if (mejorPuntuacion) puntos else puntuacionFirebase.puntos,
                                fallos = if (mejorFallos) fallos else puntuacionFirebase.fallos
                            )

                            // se actualiza la puntuación en la base de datos de Firebase
                            dbfire.collection(COLECCION_FIREBASE).document(document.id).set(puntuacionFirebaseActualizado, SetOptions.merge())
                                .addOnFailureListener { ex ->
                                    println("Error al actualizar la puntuación de Numinario 1 del usuario en Firebase: ${ex.message}")
                                }
                        }
                    }
                    .addOnFailureListener { ex ->
                        println("Error al obtener la puntuación del usuario en Numinario 1 en Firebase: ${ex.message}")
                    }
            }
            // si existe, pero no hay ninguna mejora, no se hace nada
            AccionPuntuacion.SIN_CAMBIOS -> {}
        }
    }






}