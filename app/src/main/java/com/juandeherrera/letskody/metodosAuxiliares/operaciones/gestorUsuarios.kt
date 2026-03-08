package com.juandeherrera.letskody.metodosAuxiliares.operaciones

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.juandeherrera.letskody.firebase.UsuarioFirebase
import com.juandeherrera.letskody.localdb.AppDB
import com.juandeherrera.letskody.localdb.Estructura
import com.juandeherrera.letskody.localdb.UsuarioData
import com.juandeherrera.letskody.metodosAuxiliares.componentes.notificationSnackbar
import com.juandeherrera.letskody.navigation.AppScreens
import com.juandeherrera.letskody.notification.NotificationHandler
import kotlinx.coroutines.CoroutineScope

// función auxiliar para registrar en la base de datos local un usuario temporal previo a ser registrado en Firebase
fun crearUsuarioTemporal(controladorNavegacion: NavController, scope: CoroutineScope, snackbarHostState: SnackbarHostState, context: Context, nombre: String, apellidos: String, telefono: String, email: String, password: String, sexo: String, fechaNacimiento: String, foto: String) {

    // instancia a la base de datos local (en el mismo hilo)
    val db = Room.databaseBuilder(context, klass = AppDB::class.java, name = Estructura.DB.NAME).allowMainThreadQueries().build()

    val auth = FirebaseAuth.getInstance() // instancia al sistema de autenticación de Firebase

    // se almacenan los datos en un usuario local temporal que será registrado
    val userTemporal = UsuarioData(
        uidUsuario = "",             // este campo está vacío porque todavía no está registrado en Firebase
        nombreUsuario = nombre,
        apellidosUsuario = apellidos,
        telefonoUsuario = telefono,
        emailUsuario = email,
        passwordUsuario = password,
        sexoUsuario = sexo,
        fnacUsuario = fechaNacimiento,
        fotoUsuario = foto
    )

    db.usuarioDao().nuevoUsuario(usuarioData = userTemporal) // se guarda el usuario temporal en la base de datos local

    // se crea un usuario (no verificado todavía) en Firebase con el email y la contraseña
    auth.createUserWithEmailAndPassword(email, password)
        .addOnSuccessListener {
            // si la creación del usuario salió bien se navega a la pantalla de verificación de email (se limpia el historial de navegación)
            controladorNavegacion.navigate(route = AppScreens.VerificarEmailUsuario.route) { popUpTo(AppScreens.CrearUsuario.route) { inclusive = true } }
        }
        .addOnFailureListener { ex ->
            // si falla la creación del usuario se muestra un mensaje de error y se muestra el error por consola
            notificationSnackbar(scope = scope, snackbarHostState = snackbarHostState, mensaje = "Error al registrar el usuario.", tipo = "error")
            println("Error al registrar el usuario con Auth: ${ex.message}")
        }
}

// función auxiliar para cancelar el registro en Firebase del usuario temporal y eliminarle
fun cancelarRegistroUsuarioFirebase(controladorNavegacion: NavController, db: AppDB, auth: FirebaseAuth, email: String) {

    db.usuarioDao().eliminarUsuario(email = email) // se elimina el usuario temporal de la base de datos local

    auth.currentUser?.delete() // se elimina el usuario de Firebase (no verificado)

    auth.signOut()  // se cierra la sesión de Firebase

    // se vuelve a la pantalla de login (se limpia el historial de navegación)
    controladorNavegacion.navigate(route = AppScreens.Login.route) { popUpTo(id = 0) }
}

// función auxiliar para registrar los datos del usuario temporal en Firebase
fun registrarUsuarioFirebase(uid: String, usuarioTemporal: UsuarioData, db: AppDB, notificationHandler: NotificationHandler, exito: () -> Unit, error: (String) -> Unit) {

    val dbfire = FirebaseFirestore.getInstance()  // instancia a la base de datos de Firebase asociada a la aplicación

    // se almacenan los datos del usuario local en un usuario de Firebase
    val userFirebase = UsuarioFirebase(
        nombre = usuarioTemporal.nombreUsuario,
        apellidos = usuarioTemporal.apellidosUsuario,
        telefono = usuarioTemporal.telefonoUsuario,
        email = usuarioTemporal.emailUsuario,
        sexo = usuarioTemporal.sexoUsuario,
        fechaNacimiento = usuarioTemporal.fnacUsuario,
        foto = usuarioTemporal.fotoUsuario
    )

    // se almacena el usuario de Firebase en la colección 'usuarios' como un documento identificado por su UID
    dbfire.collection("usuarios").document(uid).set(userFirebase)
        .addOnSuccessListener {
            // si se realizo correctamente el registro del usuario
            notificationHandler.notificacionCreacionUsuario(nombre = usuarioTemporal.nombreUsuario + " " +usuarioTemporal.apellidosUsuario) // notificación de registro de usuario
            db.usuarioDao().eliminarUsuario(email = usuarioTemporal.emailUsuario)  // se eliminan los datos temporales ya innecesarios
            exito()
        }
        .addOnFailureListener { ex ->
            // si ocurre algún error, se muestra por consola el error y se manda un mensaje al usuario
            println("Error al guardar el usuario en Firebase: ${ex.message}")
            error("No se logró registrar el usuario.")
        }
}