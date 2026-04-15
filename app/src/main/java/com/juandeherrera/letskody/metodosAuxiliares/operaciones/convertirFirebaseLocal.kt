package com.juandeherrera.letskody.metodosAuxiliares.operaciones

import com.google.firebase.firestore.QuerySnapshot
import com.juandeherrera.letskody.firebase.BanderasEuropaFirebase
import com.juandeherrera.letskody.firebase.PuntuacionEuroBanderasFirebase
import com.juandeherrera.letskody.firebase.UsuarioFirebase
import com.juandeherrera.letskody.localdb.BanderasEuropaData
import com.juandeherrera.letskody.localdb.PuntuacionEuroBanderasData
import com.juandeherrera.letskody.localdb.UsuarioData

// función auxiliar para transformar el usuario de Firebase en un usuario local
fun convertirUsuarioFirebaseLocal(usuarioFirebase: UsuarioFirebase, uid: String) : UsuarioData {
    return UsuarioData(
        idUsuario = 0,                                         // ID incremental del registro
        uidUsuario = uid,                                      // UID de Firebase del usuario
        nombreUsuario = usuarioFirebase.nombre ?: "",          // nombre del usuario en Firebase
        apellidosUsuario = usuarioFirebase.apellidos ?: "",    // apellidos del usuario en Firebase
        telefonoUsuario = usuarioFirebase.telefono ?: "",      // número de teléfono completo del usuario en Firebase
        emailUsuario = usuarioFirebase.email ?: "",            // email del usuario en Firebase
        passwordUsuario = "",                                  // contraseña como cadena vacía ya que la guarda Firebase
        sexoUsuario = usuarioFirebase.sexo ?: "",              // sexo del usuario en Firebase
        fnacUsuario = usuarioFirebase.fechaNacimiento ?: "",   // fecha de nacimiento del usuario en Firebase
        fotoUsuario = usuarioFirebase.foto ?: "",              // foto de perfil del usuario en Firebase
        ultimoEnvioTicket = usuarioFirebase.ultimoEnvioTicket  // marca temporal del último envío del ticket al servicio técnico del usuario
    )
}

// función auxiliar para transformar una lista de banderas de Europa de Firebase en banderas de Europa locales
fun convertirBanderasEuropaFirebaseLocal(listaBanderasEuropaFirebase: QuerySnapshot) : List<BanderasEuropaData> {
    return listaBanderasEuropaFirebase.documents.map { doc ->

        val banderaEuropa = doc.toObject(BanderasEuropaFirebase::class.java)!!  // se obtiene una bandera de Europa

        // constructor para banderas de Europa locales
        BanderasEuropaData(
            idBandera = 0,                                // ID incremental del registro
            uidBandera = doc.id,                          // UID de Firebase de la bandera de Europa
            urlBandera = banderaEuropa.url ?: "",         // URL de la foto de la bandera
            opcion1 = banderaEuropa.opcion1 ?: "",        // opcion 1
            opcion2 = banderaEuropa.opcion2 ?: "",        // opcion 2
            opcion3 = banderaEuropa.opcion3 ?: "",        // opcion 3
            opcion4 = banderaEuropa.opcion4 ?: "",        // opcion 4
            opcionCorrecta = banderaEuropa.correct ?: ""  // respuesta correcta
        )
    }
}

// función auxiliar para transformar una lista de puntuaciones de Euro-banderas de Firebase en puntuaciones de Euro-banderas locales
fun convertirPuntuacionesEuroBanderasFirebaseLocal(listaPuntuacionEuroBanderas: QuerySnapshot) : List<PuntuacionEuroBanderasData> {
    return listaPuntuacionEuroBanderas.documents.map { doc ->

        val puntuacionEuroBanderas = doc.toObject(PuntuacionEuroBanderasFirebase::class.java)!!  // se obtiene una puntuación de Euro-Banderas

        // constructor para puntuaciones de Euro-banderas locales
        PuntuacionEuroBanderasData(
            idPuntuacion = 0,                             // ID incremental del registro
            uidPuntosEuroBanderas = doc.id,               // UID de Firebase de la puntuación
            puntos = puntuacionEuroBanderas.puntos ?: 0,       // puntos obtenidos
            tiempo = puntuacionEuroBanderas.tiempo ?: 0,       // tiempo (segundos) que ha tardado el usuario
            usuario = puntuacionEuroBanderas.usuario ?: "",      // UID del usuario que ha realizado la puntuación
        )
    }
}