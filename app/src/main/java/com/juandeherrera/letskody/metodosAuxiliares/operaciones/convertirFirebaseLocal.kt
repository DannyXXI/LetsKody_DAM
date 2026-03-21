package com.juandeherrera.letskody.metodosAuxiliares.operaciones

import com.juandeherrera.letskody.firebase.UsuarioFirebase
import com.juandeherrera.letskody.localdb.UsuarioData

// función auxiliar para transformar el usuario de Firebase en un usuario local
fun convertirUsuarioFirebaseLocal(usuarioFirebase: UsuarioFirebase, uid: String) : UsuarioData {
    return UsuarioData(
        uidUsuario = uid,                                     // UID de Firebase del usuario
        nombreUsuario = usuarioFirebase.nombre ?: "",         // nombre del usuario en Firebase
        apellidosUsuario = usuarioFirebase.apellidos ?: "",   // apellidos del usuario en Firebase
        telefonoUsuario = usuarioFirebase.telefono ?: "",     // número de teléfono completo del usuario en Firebase
        emailUsuario = usuarioFirebase.email ?: "",           // email del usuario en Firebase
        passwordUsuario = "",                                 // contraseña como cadena vacía ya que la guarda Firebase
        sexoUsuario = usuarioFirebase.sexo ?: "",             // sexo del usuario en Firebase
        fnacUsuario = usuarioFirebase.fechaNacimiento ?: "",  // fecha de nacimiento del usuario en Firebase
        fotoUsuario = usuarioFirebase.foto ?: ""              // foto de perfil del usuario en Firebase
    )
}