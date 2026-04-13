package com.juandeherrera.letskody.metodosAuxiliares.operaciones

import com.google.firebase.firestore.FirebaseFirestore
import com.juandeherrera.letskody.firebase.UsuarioFirebase
import com.juandeherrera.letskody.localdb.AppDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

// función auxiliar para sincronizar los datos locales de la aplicación con los de Firebase
suspend fun refrescarBaseDatos(uidUsuario: String, db: AppDB, error: (String) -> Unit) {

    withContext(Dispatchers.IO) {
        try {
            val dbfire = FirebaseFirestore.getInstance()  // instancia a la base de datos de Firebase asociada a la aplicación

            /* ----------USUARIO---------- */
            val usuarioDoc = dbfire.collection("usuarios").document(uidUsuario).get().await()  // se obtiene el documento del usuario de Firebase

            val usuarioFirebase = usuarioDoc.toObject(UsuarioFirebase::class.java) // convertimos el documento en un usuario de Firebase

            // se convierte el usuario de Firebase en un usuario local
            val usuarioLocal = convertirUsuarioFirebaseLocal(usuarioFirebase = usuarioFirebase!!, uid = uidUsuario)

            db.usuarioDao().refrescarUsuario(usuarioData = usuarioLocal)  // se actualiza el usuario en la base de datos local

            /* ----------BANDERAS DE EUROPA---------- */
            val banderasEuropaDoc = dbfire.collection("banderasEuropa").get().await()  // se obtiene la lista de documentos con todas las banderas de Europa de Firebase

            // se convierte la lista de banderas de Europa de firebase a una lista de banderas de Europa locales
            val listaBanderas = convertirBanderasEuropaFirebaseLocal(listaBanderasEuropaFirebase = banderasEuropaDoc)

            db.banderasEuropaDao().eliminarTodasBanderasEuropa()              // se eliminan todas las banderas de Europa anteriores

            db.banderasEuropaDao().agregarBanderas(banderas = listaBanderas)  // se agregan la nueva lista de banderas de Europa

            /* ----------PUNTUACIÓN DEL JUEGO EURO-BANDERAS---------- */
            val puntuacionesEuroBanderasDoc = dbfire.collection("puntuaciones_EuroBanderas").get().await()  // se obtiene la lista de documentos con todas las puntuaciones de Firebase

            // se convierte la lista de puntuaciones de Euro-banderas de firebase a una lista de puntuaciones de Euro-banderas locales
            val listaPuntuacionesEuroBanderas = convertirPuntuacionesEuroBanderasFirebaseLocal(listaPuntuacionEuroBanderas = puntuacionesEuroBanderasDoc)

            db.puntuacionEuroBanderasDao().eliminarTodasPuntuacionesEuroBanderas()       // se eliminan todas las puntuaciones anteriores

            db.puntuacionEuroBanderasDao().agregarPuntuacionesEuroBanderas(puntuaciones = listaPuntuacionesEuroBanderas)  // se agregan la nueva lista de puntuaciones

        }
        catch (ex: Exception) {
            // si hay algún error en la sincronización se muestra un mensaje al usuario y en la terminal
            error("Error al sincronizar los datos.")
            println("Error al sincronizar los datos: ${ex.message}")
        }
    }
}