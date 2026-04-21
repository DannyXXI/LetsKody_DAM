package com.juandeherrera.letskody.metodosAuxiliares.operaciones

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.navigation.NavController
import androidx.room.Room
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.juandeherrera.letskody.firebase.UsuarioFirebase
import com.juandeherrera.letskody.localdb.AppDB
import com.juandeherrera.letskody.localdb.Estructura
import com.juandeherrera.letskody.localdb.UsuarioData
import com.juandeherrera.letskody.metodosAuxiliares.componentes.notificationSnackbar
import com.juandeherrera.letskody.navigation.AppScreens
import com.juandeherrera.letskody.notification.NotificationHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val WEB_CLIENT_ID = "471155651747-togjsf3ome69vggf8nb80ftaiidm93gd.apps.googleusercontent.com"  // id obtenido de Firebase

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
            notificationSnackbar(scope = scope, snackbarHostState = snackbarHostState, mensaje = "Error al registrar el usuario.")
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
        foto = usuarioTemporal.fotoUsuario,
        ultimoEnvioTicket = usuarioTemporal.ultimoEnvioTicket
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

// función auxiliar para la recuperación de la contraseña del usuario
fun recuperarPasswordUsuario(emailRecuperacion: String, exito: () -> Unit, error: (String) -> Unit) {

    val auth = FirebaseAuth.getInstance() // instancia al sistema de autenticación de Firebase

    // Firebase envía un email de para que el usuario pueda modificar su contraseña (si no existe en la base de datos, no se enviará a ese email)
    auth.sendPasswordResetEmail(emailRecuperacion)
        .addOnSuccessListener {
            exito()  // si envío salió bien se muestra un mensaje al usuario
        }
        .addOnFailureListener { ex ->
            // si falla el envió se muestra un mensaje de error en terminal y al usuario
            println("Error al enviar el enlace para modificar la contraseña: ${ex.message}")
            error("Error al enviar el email de recuperación.")
        }
}

// función auxiliar para que el usuario que el usuario inicie sesión en la aplicación
fun loguearUsuario(controladorNavegacion: NavController, context: Context, scope: CoroutineScope, snackbarHostState: SnackbarHostState, email: String, password: String) {

    // instancia a la base de datos local (en el mismo hilo)
    val db = Room.databaseBuilder(context, klass = AppDB::class.java, name = Estructura.DB.NAME).allowMainThreadQueries().build()

    val auth = FirebaseAuth.getInstance() // instancia al sistema de autenticación de Firebase

    val dbfire = FirebaseFirestore.getInstance()  // instancia a la base de datos de Firebase asociada a la aplicación

    // se autentica el usuario en Firebase con su email y contraseña
    auth.signInWithEmailAndPassword(email, password)
        .addOnSuccessListener { result ->
            val user = result.user  // si la autenticación salió bien, se obtiene el usuario resultante

            if (user == null) {
                // si el usuario no existe, se muestra un mensaje al usuario
                notificationSnackbar(scope = scope, snackbarHostState = snackbarHostState, mensaje = "Usuario no registrado.")
                return@addOnSuccessListener
            }
            else if (!user.isEmailVerified) {
                // si el usuario no tiene verificado su email, se cerrará su sesión y se muestra un mensaje al usuario
                auth.signOut()
                notificationSnackbar(scope = scope, snackbarHostState = snackbarHostState, mensaje = "Usuario con email no verificado.")
                return@addOnSuccessListener
            }
            else {
                // si el usuario verificado existe, se procede a obtener sus datos de Firebase a partir de su UID
                dbfire.collection("usuarios").document(user.uid).get()
                    .addOnSuccessListener { document ->

                        // se obtienen los datos del usuario de Firebase (que almacenamos en su clase correspondiente)
                        val usuarioFirebase = document.toObject(UsuarioFirebase::class.java) ?: return@addOnSuccessListener

                        // se convierten los datos de Firebase a datos locales
                        val usuarioLocal = convertirUsuarioFirebaseLocal(usuarioFirebase = usuarioFirebase, uid = user.uid)

                        // se cargan todas las banderas de Europa guardadas en Firebase a local
                        dbfire.collection("banderasEuropa").get()
                            .addOnSuccessListener { listaBanderasEuropa ->

                                // si se ha obtenido los datos, se convierten para la base de datos local
                                val listaBanderasEuropaLocal = convertirBanderasEuropaFirebaseLocal(listaBanderasEuropaFirebase = listaBanderasEuropa)

                                // se cargan todas las puntuaciones del juego Euro-banderas guardadas en Firebase a local
                                dbfire.collection("puntuaciones_EuroBanderas").get()
                                    .addOnSuccessListener { listaPuntuacionesEuroBanderas ->

                                        // si se ha obtenido los datos, se convierten para la base de datos local
                                        val puntuacionesEuroBanderasLocal = convertirPuntuacionesEuroBanderasFirebaseLocal(listaPuntuacionEuroBanderas = listaPuntuacionesEuroBanderas)

                                        // se cargan todas las puntuaciones del juego Numinario 1 guardadas en Firebase a local
                                        dbfire.collection("puntuaciones_Numinario1").get()
                                            .addOnSuccessListener { listaPuntuacionesNuminario1 ->

                                                // si se ha obtenido los datos, se convierten para la base de datos local
                                                val puntuacionesNuminario1Local = convertirPuntuacionesNuminario1FirebaseLocal(listaPuntuacionNuminario1 = listaPuntuacionesNuminario1)

                                                // si la lista de puntuaciones de Numinario 1 no está vacía, se agrega a la base de datos local
                                                if (puntuacionesNuminario1Local.isNotEmpty()) {
                                                    db.puntuacionNuminario1Dao().agregarPuntuacionesNuminario1(puntuaciones = puntuacionesNuminario1Local)
                                                }

                                                // si la lista de puntuaciones de Euro-banderas no está vacía, se agrega a la base de datos local
                                                if (puntuacionesEuroBanderasLocal.isNotEmpty()) {
                                                    db.puntuacionEuroBanderasDao().agregarPuntuacionesEuroBanderas(puntuaciones = puntuacionesEuroBanderasLocal)
                                                }

                                                db.banderasEuropaDao().agregarBanderas(banderas = listaBanderasEuropaLocal) // se agregan la lista de banderas de Europa

                                                db.usuarioDao().nuevoUsuario(usuarioData = usuarioLocal)  // se agrega el usuario a la base de datos local

                                                // se navega a la pantalla de inicio y se limpia el historial de navegación
                                                controladorNavegacion.navigate(AppScreens.Inicio.route) { popUpTo(AppScreens.Login.route) { inclusive = true } }

                                            }
                                            .addOnFailureListener { ex ->
                                                // si se falla al obtener los datos se muestra un mensaje de error por terminal y al usuario
                                                println("Error al obtener las puntuaciones de Numinario 1: ${ex.message}")
                                                notificationSnackbar(scope = scope, snackbarHostState = snackbarHostState, mensaje = "Error al obtener las puntuaciones de Numinario 1.")
                                            }
                                    }
                                    .addOnFailureListener { ex ->
                                        // si se falla al obtener los datos se muestra un mensaje de error por terminal y al usuario
                                        println("Error al obtener las puntuaciones de Euro-banderas: ${ex.message}")
                                        notificationSnackbar(scope = scope, snackbarHostState = snackbarHostState, mensaje = "Error al obtener las puntuaciones de Euro-banderas.")
                                    }
                            }
                            .addOnFailureListener { ex ->
                                // si se falla al obtener los datos se muestra un mensaje de error por terminal y al usuario
                                println("Error al obtener los datos de las banderas de Europa: ${ex.message}")
                                notificationSnackbar(scope = scope, snackbarHostState = snackbarHostState, mensaje = "Error al obtener las banderas de Europa.")
                            }
                    }
                    .addOnFailureListener { ex ->
                        // si se falla al obtener los datos se muestra un mensaje de error por terminal y al usuario
                        println("Error al obtener los datos del usuario: ${ex.message}")
                        notificationSnackbar(scope = scope, snackbarHostState = snackbarHostState, mensaje = "Error al obtener los datos del usuario.")
                    }
            }
        }
        .addOnFailureListener { ex ->
            // si fallo la autenticación, se obtiene el mensaje para mostrar al usuario
            val mensajeError = when (ex) {
                is FirebaseAuthInvalidCredentialsException -> "Credenciales incorrectas."  // sí se escriben mal las credenciales o el usuario no existe
                else -> "Error en el inicio de sesión."  // otro tipo de errores
            }

            println("Error al iniciar sesión: ${ex.message}")  // mensaje que se muestra en la terminal

            notificationSnackbar(scope = scope, snackbarHostState = snackbarHostState, mensaje = mensajeError)  // mensaje que se muestra al usuario
        }
}

// función auxiliar para cerrar la sesión del usuario
fun cerrarSesionUsuario(db: AppDB, usuario: UsuarioData) {
    FirebaseAuth.getInstance().signOut()                           // se cierra la sesión de Firebase
    db.usuarioDao().eliminarUsuario(email = usuario.emailUsuario)  // se elimina el usuario local
    db.banderasEuropaDao().eliminarTodasBanderasEuropa()           // se eliminan todas las banderas de Europa locales
    db.puntuacionEuroBanderasDao().eliminarTodasPuntuacionesEuroBanderas()  // se eliminan todas las puntuaciones de Euro-banderas locales
    db.puntuacionNuminario1Dao().eliminarTodasPuntuacionesNuminario1()      // se eliminan todas las puntuaciones de Numinario 1 locales
}

// función auxiliar para eliminar la cuenta del usuario
fun eliminarCuentaUsuario(usuario: UsuarioData, password: String, db: AppDB, controladorNavegacion: NavController, context: Context, scope: CoroutineScope, error: (String) -> Unit) {

    val auth = FirebaseAuth.getInstance() // instancia al sistema de autenticación de Firebase

    val dbfire = FirebaseFirestore.getInstance()  // instancia a la base de datos de Firebase asociada a la aplicación

    val user = auth.currentUser  // usuario autenticado de Firebase

    if (user == null) {
        error("No hay usuario autenticado.") // si no hay usuario autenticado se manda un mensaje de error
        return
    }

    // se comprueba si el usuario inicio sesión con Google
    val esUsuarioGoogle = user.providerData.any { it.providerId == "google.com" }

    // función interna que contiene toda la lógica de borrado
    fun borradoDatos() {
        // se obtiene el documento de la puntuación del juego Euro-Banderas del usuario para eliminarlo
        dbfire.collection("puntuaciones_EuroBanderas").whereEqualTo("usuario", user.uid).get()
            .addOnSuccessListener { documentos ->
                val batchPuntuacionEuroBanderas = dbfire.batch() // se crea un batch para eliminar la puntuación en una sola operacion

                // se agrega la referencia (UID del documento a eliminar) al batch
                for (doc in documentos.documents) {
                    val ref = dbfire.collection("puntuaciones_EuroBanderas").document(doc.id)
                    batchPuntuacionEuroBanderas.delete(ref)
                }

                batchPuntuacionEuroBanderas.commit() // se ejecuta el batch
                    .addOnSuccessListener {
                        // si se ha eliminado correctamente, se obtiene el documento de la puntuación del juego Numinario 1 del usuario para eliminarlo
                        dbfire.collection("puntuaciones_Numinario1").whereEqualTo("usuario", user.uid).get()
                            .addOnSuccessListener { documentos ->
                                val batchPuntuacionNuminario1 = dbfire.batch() // se crea un batch para eliminar la puntuación en una sola operacion

                                // se agrega la referencia (UID del documento a eliminar) al batch
                                for (doc in documentos.documents) {
                                    val ref = dbfire.collection("puntuaciones_Numinario1").document(doc.id)
                                    batchPuntuacionNuminario1.delete(ref)
                                }

                                batchPuntuacionNuminario1.commit() // se ejecuta el batch
                                    .addOnSuccessListener {
                                        // si se ha eliminado correctamente, se borra el usuario de la base de datos de Firebase
                                        dbfire.collection("usuarios").document(user.uid).delete()
                                            .addOnSuccessListener {
                                                // si ha funcionado el borrado de todos los datos relacionados con el usuario, se borra el usuario autenticado
                                                user.delete()
                                                    .addOnSuccessListener {
                                                        db.usuarioDao().eliminarUsuario(email = usuario.emailUsuario)  // se elimina el usuario local

                                                        db.banderasEuropaDao().eliminarTodasBanderasEuropa()           // se eliminan todas las banderas de Europa locales

                                                        db.puntuacionEuroBanderasDao().eliminarTodasPuntuacionesEuroBanderas()  // se eliminan todas las puntuaciones de Euro-banderas locales

                                                        db.puntuacionNuminario1Dao().eliminarTodasPuntuacionesNuminario1()      // se eliminan todas las puntuaciones de Numinario 1 locales

                                                        auth.signOut()  // se cierra la sesión de Firebase

                                                        controladorNavegacion.navigate(AppScreens.Login.route) { popUpTo(0) }  // se vuelve a la pantalla de login (se borra el historial de navegación)
                                                    }
                                                    .addOnFailureListener { ex ->
                                                        // si falla el borrado del usuario autenticado se muestra un mensaje en la terminal y al usuario
                                                        error("Error al eliminar el usuario autenticado.")
                                                        println("Error al eliminar el usuario autenticado: ${ex.message}")
                                                    }
                                            }
                                            .addOnFailureListener { ex ->
                                                // si falla se muestra un mensaje en la terminal y al usuario
                                                error("Error al eliminar el usuario de Firebase.")
                                                println("Error al eliminar el usuario de Firebase: ${ex.message}")
                                            }
                                    }
                                    .addOnFailureListener { ex ->
                                        // si falla se muestra un mensaje en la terminal y al usuario
                                        error("Error al eliminar la puntuación de Numinario 1 del usuario.")
                                        println("Error al eliminar la puntuación de Numinario 1 del usuario: ${ex.message}")
                                    }
                            }
                            .addOnFailureListener { ex ->
                                // si falla se muestra un mensaje en la terminal y al usuario
                                error("Error al obtener la puntuación de Numinario 1 del usuario.")
                                println("Error al obtener la puntuación de Numinario 1 del usuario: ${ex.message}")
                            }
                    }
                    .addOnFailureListener { ex ->
                        // si falla se muestra un mensaje en la terminal y al usuario
                        error("Error al eliminar la puntuación de Euro-banderas del usuario.")
                        println("Error al eliminar la puntuación de Euro-banderas del usuario: ${ex.message}")
                    }
            }
            .addOnFailureListener { ex ->
                // si falla se muestra un mensaje en la terminal y al usuario
                error("Error al obtener la puntuación de Euro-banderas del usuario.")
                println("Error al obtener la puntuación de Euro-banderas del usuario: ${ex.message}")
            }
    }

    if (esUsuarioGoogle) {
        // ─── Usuario de Google: se reautentica con Google antes de borrar ───
        // Firebase exige reautenticación reciente antes de eliminar una cuenta
        // se lanza en corrutina porque el CredentialManager es una función suspendida
        scope.launch {
            try {
                val credentialManager = CredentialManager.create(context)

                // se construye la solicitud de reautenticación con Google
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setServerClientId(WEB_CLIENT_ID)      // ID del cliente web de Firebase
                    .setFilterByAuthorizedAccounts(true)   // true = solo muestra la cuenta ya vinculada
                    .setAutoSelectEnabled(true)            // true = selecciona automáticamente si solo hay una cuenta
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                // se muestra el selector de cuentas de Google para reautenticar
                val result = credentialManager.getCredential(request = request, context = context)

                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
                val googleIdToken = googleIdTokenCredential.idToken

                // se construye la credencial de Firebase con el token de Google
                val credencialFirebase = GoogleAuthProvider.getCredential(googleIdToken, null)

                // se reautentica el usuario en Firebase con la credencial de Google
                user.reauthenticate(credencialFirebase)
                    .addOnSuccessListener {
                        borradoDatos()  // si la reautenticación fue correcta se procede con el borrado
                    }
                    .addOnFailureListener { ex ->
                        error("Error al reautenticar con Google.")
                        println("Error al reautenticar con Google: ${ex.message}")
                    }

            } catch (ex: GetCredentialException) {
                // si el usuario cancela o hay algún error en el selector de Google
                println("Reautenticación con Google cancelada: ${ex.message}")
            }
        }
    }
    else {
        // se obtiene las credenciales del usuario necesarias para la reautenticación
        val credencialesUsuario = EmailAuthProvider.getCredential(usuario.emailUsuario, password)

        // se reautentica el usuario
        user.reauthenticate(credencialesUsuario)
            .addOnSuccessListener {
                borradoDatos()
            }
            .addOnFailureListener { ex ->
                // si falla la reautenticación se muestra un mensaje en la terminal y al usuario
                error("Error al reautenticar el usuario.")
                println("Error al reautenticar el usuario: ${ex.message}")
            }
    }
}

// función auxiliar para actualizar los datos del usuario en Firebase y local
fun actualizarUsuario(usuarioActualizado: UsuarioData, passwordOriginal: String, passwordNueva: String, esUsuarioGoogle: Boolean, controladorNavegacion: NavController, db: AppDB, error: (String) -> Unit) {

    val auth = FirebaseAuth.getInstance() // instancia al sistema de autenticación de Firebase

    val dbfire = FirebaseFirestore.getInstance()  // instancia a la base de datos de Firebase asociada a la aplicación

    val user = auth.currentUser  // usuario autenticado de Firebase

    if (user == null) {
        error("No hay usuario autenticado.") // si no hay usuario autenticado se manda un mensaje de error
        return
    }

    // se realiza la actualización del usuario en función de si es usuario de Google o no
    if (esUsuarioGoogle) {
        // se accede a la colección de Firebase para obtener el usuario almacenado
        dbfire.collection("usuarios").document(usuarioActualizado.uidUsuario).get()
            .addOnSuccessListener { doc ->
                // si funciona se extrae el usuario de Firebase a modificar
                val usuarioFirebase = doc.toObject(UsuarioFirebase::class.java)

                if (usuarioFirebase != null) {
                    // se crea una copia del usuario de Firebase con los datos actualizados
                    val usuarioFirebaseActualizado = usuarioFirebase.copy(
                        nombre = usuarioActualizado.nombreUsuario,
                        apellidos = usuarioActualizado.apellidosUsuario,
                        telefono = usuarioActualizado.telefonoUsuario,
                        email = usuarioActualizado.emailUsuario,
                        sexo = usuarioActualizado.sexoUsuario,
                        fechaNacimiento = usuarioActualizado.fnacUsuario,
                        foto = usuarioActualizado.fotoUsuario,
                        ultimoEnvioTicket = usuarioActualizado.ultimoEnvioTicket
                    )

                    // se actualiza el usuario en la base de datos de Firebase
                    dbfire.collection("usuarios").document(usuarioActualizado.uidUsuario).set(usuarioFirebaseActualizado, SetOptions.merge())
                        .addOnSuccessListener {
                            // si funciona se actualiza el usuario local
                            db.usuarioDao().actualizarUsuario(usuarioActualizado)

                            controladorNavegacion.navigate(AppScreens.Perfil.route) // se vuelve a la pantalla de perfil inicial
                        }
                        .addOnFailureListener { ex ->
                            // si falla la actualización del usuario se muestra un mensaje en la terminal y al usuario
                            error("Error al actualizar el usuario en Firebase.")
                            println("Error al actualizar el usuario en Firebase: ${ex.message}")
                        }
                }
            }
            .addOnFailureListener { ex ->
                // si falla la obtención del usuario se muestra un mensaje en la terminal y al usuario
                error("Error al obtener el usuario de Firebase.")
                println("Error al obtener el usuario de Firebase: ${ex.message}")
            }
    }
    else {
        // se obtiene las credenciales del usuario necesarias para la reautenticación
        val credencialesUsuario = EmailAuthProvider.getCredential(usuarioActualizado.emailUsuario, passwordOriginal)

        // se reautentica el usuario
        user.reauthenticate(credencialesUsuario)
            .addOnSuccessListener {
                // si funciona la reautenticación, se actualiza la contraseña del usuario si la contraseña nueva no esta vacía
                if (passwordNueva.isNotBlank()) {
                    user.updatePassword(passwordNueva)
                        .addOnFailureListener { ex ->
                            // si la actualización sale mal se muestra un mensaje al usuario y en terminal, y se sale de la función
                            error("No se pudo actualizar la contraseña.")
                            println("Error al actualizar la contraseña: ${ex.message}")
                            return@addOnFailureListener
                        }
                }

                // se accede a la colección de Firebase para obtener el usuario almacenado
                dbfire.collection("usuarios").document(usuarioActualizado.uidUsuario).get()
                    .addOnSuccessListener { doc ->
                        // si funciona se extrae el usuario de Firebase a modificar
                        val usuarioFirebase = doc.toObject(UsuarioFirebase::class.java)

                        if (usuarioFirebase != null) {
                            // se crea una copia del usuario de Firebase con los datos actualizados
                            val usuarioFirebaseActualizado = usuarioFirebase.copy(
                                nombre = usuarioActualizado.nombreUsuario,
                                apellidos = usuarioActualizado.apellidosUsuario,
                                telefono = usuarioActualizado.telefonoUsuario,
                                email = usuarioActualizado.emailUsuario,
                                sexo = usuarioActualizado.sexoUsuario,
                                fechaNacimiento = usuarioActualizado.fnacUsuario,
                                foto = usuarioActualizado.fotoUsuario,
                                ultimoEnvioTicket = usuarioActualizado.ultimoEnvioTicket
                            )

                            // se actualiza el usuario en la base de datos de Firebase
                            dbfire.collection("usuarios").document(usuarioActualizado.uidUsuario).set(usuarioFirebaseActualizado, SetOptions.merge())
                                .addOnSuccessListener {
                                    // si funciona se actualiza el usuario local
                                    db.usuarioDao().actualizarUsuario(usuarioActualizado)

                                    controladorNavegacion.navigate(AppScreens.Perfil.route) // se vuelve a la pantalla de perfil inicial
                                }
                                .addOnFailureListener { ex ->
                                    // si falla la actualización del usuario se muestra un mensaje en la terminal y al usuario
                                    error("Error al actualizar el usuario en Firebase.")
                                    println("Error al actualizar el usuario en Firebase: ${ex.message}")
                                }
                        }
                    }
                    .addOnFailureListener { ex ->
                        // si falla la obtención del usuario se muestra un mensaje en la terminal y al usuario
                        error("Error al obtener el usuario de Firebase.")
                        println("Error al obtener el usuario de Firebase: ${ex.message}")
                    }
            }
            .addOnFailureListener { ex ->
                // si falla la reautenticación se muestra un mensaje en la terminal y al usuario
                when (ex) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        error("La contraseña original es incorrecta.")
                    }
                    is FirebaseAuthInvalidUserException -> {
                        error("El usuario no existe o ha sido eliminado.")
                    }
                    else -> {
                        error("Error de autenticación.")
                    }
                }
                println("Error al reautenticar el usuario: ${ex.message}")
            }
    }
}