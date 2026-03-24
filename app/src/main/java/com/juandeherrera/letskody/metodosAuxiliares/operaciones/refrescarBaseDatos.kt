package com.juandeherrera.letskody.metodosAuxiliares.operaciones

import com.google.firebase.firestore.FirebaseFirestore
import com.juandeherrera.letskody.firebase.UsuarioFirebase
import com.juandeherrera.letskody.localdb.AppDB
import com.juandeherrera.letskody.localdb.UsuarioData
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
            val usuarioLocal = UsuarioData(
                idUsuario = 0,
                uidUsuario = uidUsuario,
                nombreUsuario = usuarioFirebase!!.nombre ?: "",
                apellidosUsuario = usuarioFirebase.apellidos ?: "",
                telefonoUsuario = usuarioFirebase.telefono ?: "",
                emailUsuario = usuarioFirebase.email ?: "",
                passwordUsuario = "",  // se guarda en Firebase Authentication
                sexoUsuario = usuarioFirebase.sexo ?: "",
                fnacUsuario = usuarioFirebase.fechaNacimiento ?: "",
                fotoUsuario = usuarioFirebase.foto ?: ""
            )

            db.usuarioDao().refrescarUsuario(usuarioData = usuarioLocal)  // se actualiza el usuario en la base de datos local

        }
        catch (ex: Exception) {
            // si hay algún error en la sincronización se muestra un mensaje al usuario y en la terminal
            error("Error al sincronizar los datos.")
            println("Error al sincronizar los datos: ${ex.message}")
        }
    }
}