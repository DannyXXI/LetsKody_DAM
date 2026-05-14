package com.juandeherrera.letskody.metodosAuxiliares.operaciones

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.navigation.NavController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.juandeherrera.letskody.R
import com.juandeherrera.letskody.firebase.UsuarioFirebase
import com.juandeherrera.letskody.localdb.AppDB
import com.juandeherrera.letskody.navigation.AppScreens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

private const val WEB_CLIENT_ID = "471155651747-togjsf3ome69vggf8nb80ftaiidm93gd.apps.googleusercontent.com"  // id obtenido de Firebase

// función auxiliar principal para la gestión del inicio de sesión con Google
fun iniciarSesionGoogle(context: Context, scope: CoroutineScope, db: AppDB, controladorNavegacion: NavController, error: (String) -> Unit) {

    val auth = FirebaseAuth.getInstance()  // instancia al sistema de autenticación de Firebase

    val dbfire = FirebaseFirestore.getInstance()  // instancia a la base de datos de Firebase asociada a la aplicación

    val gestorCredenciales = CredentialManager.create(context)  // se crea el gestor de credenciales de Android

    // función interna para generar un nonce seguro cifrado en SHA-256
    fun generarNonce(): String {
        val raw = UUID.randomUUID().toString()  // valor aleatorio
        val bytes = MessageDigest.getInstance("SHA-256").digest(raw.toByteArray())  // cifrado en SHA-256
        return bytes.joinToString("") { "%02x".format(it) }   // se devuelve convertido en cadena hexadecimal
    }

    // se configura la opción de Google con el WEB CLIENT ID
    val opcionGoogle = GetGoogleIdOption.Builder()
        .setServerClientId(WEB_CLIENT_ID)      // id del cliente web de Firebase
        .setFilterByAuthorizedAccounts(false)  // se permite cuentas nuevas aparte de las autorizadas
        .setAutoSelectEnabled(false)           // se muestra siempre el selector de cuentas al usuario
        .setNonce(generarNonce())              // se incluye el nonce (valor aleatorio generado para garantizar que cada petición es única)
        .build()

    // se construye la solicitud de credenciales con la opción de Google
    val solicitud = GetCredentialRequest.Builder().addCredentialOption(opcionGoogle).build()

    // se lanza una corrutina porque el gestor de credenciales es una operación asíncrona
    scope.launch {
        try {
            // se muestra el selector de cuentas de Google y se espera la respuesta del usuario
            val respuesta = gestorCredenciales.getCredential(request = solicitud, context = context)

            // se extrae los token de Google de la respuesta
            val tokenCredencialesGoogle = GoogleIdTokenCredential.createFrom(respuesta.credential.data)
            val tokenIdGoogle = tokenCredencialesGoogle.idToken

            // se construye las credenciales de Firebase a partir del token de Google
            val credencialesFirebase = GoogleAuthProvider.getCredential(tokenIdGoogle, null)

            // se autentica el usuario en Firebase con la credencial de Google
            auth.signInWithCredential(credencialesFirebase)
                .addOnSuccessListener {  authResult ->
                    val usuarioFirebase = authResult.user  // usuario autenticado de Firebase

                    if (usuarioFirebase == null) {
                        error("No se pudo obtener el usuario de Google.")
                        return@addOnSuccessListener
                    }

                    // se comprueba si el usuario ya existe en Firestore se busca su documento por UID
                    dbfire.collection("usuarios").document(usuarioFirebase.uid).get()
                        .addOnSuccessListener { documento ->

                            if (documento.exists()) {
                                // si el usuario ya existe en Firestore, se inicia su sesión

                                // se convierte el documento de Firestore en un objeto de Usuario de Firebase
                                val datosUsuario = documento.toObject(UsuarioFirebase::class.java) ?: run {
                                    error("Error al leer los datos del usuario.")
                                    return@addOnSuccessListener
                                }

                                // se cargan los datos necesarios desde Firebase para tenerlos en local
                                cargarDatos(
                                    uid = usuarioFirebase.uid,
                                    datosUsuario = datosUsuario,
                                    dbfire = dbfire,
                                    db = db,
                                    controladorNavegacion = controladorNavegacion,
                                    error = error
                                )
                            }
                            else {
                                // si el usuario no existe en Firestore, se registra

                                // se obtienen los datos del perfil de Google
                                val nombreGoogle = usuarioFirebase.displayName ?: ""
                                val emailGoogle = usuarioFirebase.email ?: ""
                                val fotoUrlGoogle = usuarioFirebase.photoUrl?.toString()

                                // se obtiene el nombre y los apellidos del nombre completo de Google (se divide el primer espacio)
                                val partes = nombreGoogle.trim().split(" ", limit = 2)
                                val nombre = partes.getOrElse(index = 0) { "" }     // primera palabra (nombre) si existe
                                val apellidos = partes.getOrElse(index = 1) { "" }  // resto del nombre (apellidos) si existen

                                scope.launch {
                                    // se descarga la foto de perfil de Google a base64 (en función si el usuario tiene imagen, si no se usa una por defecto)
                                    val fotoBase64 = if (fotoUrlGoogle != null) {
                                        descargarFotoGoogleBase64(context = context, url = fotoUrlGoogle)
                                    }
                                    else {
                                        convertirImagenDefectoBase64(context = context, recursoId = R.drawable.kody_orange)
                                    }

                                    // se construye el objeto de usuario de Firebase con los datos disponibles (los demás se dejan vacíos o con valor por defecto)
                                    val nuevoUsuario = UsuarioFirebase(
                                        nombre = nombre,
                                        apellidos = apellidos,
                                        telefono = "",         // Google no proporciona el teléfono se dejará vacío
                                        email = emailGoogle,
                                        sexo = "Otro",         // Google no proporciona el sexo por lo que se establece como Otro
                                        fechaNacimiento = "",  // Google no proporciona la fecha de nacimiento se dejará vacío
                                        foto = fotoBase64,
                                        ultimoEnvioTicket = 0L
                                    )

                                    // se guarda el nuevo usuario en la base de datos de Firebase
                                    dbfire.collection("usuarios").document(usuarioFirebase.uid).set(nuevoUsuario)
                                        .addOnSuccessListener {
                                            // si el registro fue exitoso, se procede a cargar los datos
                                            cargarDatos(
                                                uid = usuarioFirebase.uid,
                                                datosUsuario = nuevoUsuario,
                                                dbfire = dbfire,
                                                db = db,
                                                controladorNavegacion = controladorNavegacion,
                                                error = error
                                            )
                                        }
                                        .addOnFailureListener { ex ->
                                            // si falla el registro del nuevo usuario en Firebase se muestra un mensaje de error al usuario y en la terminal
                                            error("Error al registrar el nuevo usuario en Firebase.")
                                            println("Error al registrar el nuevo usuario en Firebase: ${ex.message}")
                                        }
                                }
                            }
                        }
                        .addOnFailureListener { ex ->
                            // si falla la comprobación del usuario en Firebase se muestra un mensaje de error al usuario y en la terminal
                            error("Error al comprobar el usuario en Firebase.")
                            println("Error al comprobar el usuario en Firebase: ${ex.message}")
                        }
                }
                .addOnFailureListener { ex ->
                    // si falla la autenticación con Firebase se muestra un mensaje de error al usuario y en la terminal
                    error("Error al autenticar con Google.")
                    println("Error al autenticar en Firebase con Google: ${ex.message}")
                }
        }
        catch (ex: GetCredentialException) {
            // si el usuario cancela la selección de cuenta o hay un error en el selector, se muestra en la terminal
            println("Selector de Google cancelado o error: ${ex.message}")
        }
    }
}


// función auxiliar para cargar los datos necesarios de Firebase a local y luego navegar al inicio (el login para usuarios de Google)
private fun cargarDatos(uid: String, datosUsuario: UsuarioFirebase, dbfire: FirebaseFirestore, db: AppDB, controladorNavegacion: NavController, error: (String) -> Unit) {
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

                            // se cargan todas las palabras guardadas en Firebase a local
                            dbfire.collection("palabrasPalabrix").get()
                                .addOnSuccessListener { listaPalabras ->

                                    // si se han obtenido los datos, se convierten para la base de datos local
                                    val listaPalabrasPalabrix1Local = convertirPalabrasPalabrix1FirebaseLocal(listaPalabrasPalabrix1 = listaPalabras)

                                    // se cargan todas las puntuaciones del juego Euro-banderas guardadas en Firebase a local
                                    dbfire.collection("puntuaciones_Palabrix1").get()
                                        .addOnSuccessListener { listaPuntuacionesPalabrix1 ->

                                            // si se han obtenido los datos, se convierten para la base de datos local
                                            val puntuacionesPalabrix1Local = convertirPuntuacionesPalabrix1FirebaseLocal(listaPuntuacionPalabrix1 = listaPuntuacionesPalabrix1)

                                            // si la lista de puntuaciones de Palabrix 1 no está vacía, se agrega a la base de datos local
                                            if (puntuacionesPalabrix1Local.isNotEmpty()) {
                                                db.puntuacionPalabrix1Dao().agregarPuntuacionesPalabrix1(puntuaciones = puntuacionesPalabrix1Local)
                                            }

                                            // si la lista de puntuaciones de Numinario 1 no está vacía, se agrega a la base de datos local
                                            if (puntuacionesNuminario1Local.isNotEmpty()) {
                                                db.puntuacionNuminario1Dao().agregarPuntuacionesNuminario1(puntuaciones = puntuacionesNuminario1Local)
                                            }

                                            // si la lista de puntuaciones de Euro-banderas no está vacía, se agrega a la base de datos local
                                            if (puntuacionesEuroBanderasLocal.isNotEmpty()) {
                                                db.puntuacionEuroBanderasDao().agregarPuntuacionesEuroBanderas(puntuaciones = puntuacionesEuroBanderasLocal)
                                            }

                                            db.banderasEuropaDao().agregarBanderas(banderas = listaBanderasEuropaLocal)        // se agregan la lista de banderas de Europa

                                            db.palabrasPalabrix1Dao().agregarPalabras(palabras = listaPalabrasPalabrix1Local)  // se agregan la lista de palabras del juego Palabrix 1

                                            // se convierten los datos de Firebase a datos locales
                                            val usuarioLocal = convertirUsuarioFirebaseLocal(usuarioFirebase = datosUsuario, uid = uid)

                                            db.usuarioDao().nuevoUsuario(usuarioData = usuarioLocal)  // se agrega el usuario a la base de datos local

                                            // se navega a la pantalla de inicio y se limpia el historial de navegación
                                            controladorNavegacion.navigate(AppScreens.Inicio.route) { popUpTo(AppScreens.Login.route) { inclusive = true } }

                                        }
                                        .addOnFailureListener { ex ->
                                            // si se falla al obtener los datos se muestra un mensaje de error por terminal y al usuario
                                            error("Error al obtener las puntuaciones de Palabrix 1.")
                                            println("Error al obtener las puntuaciones de Palabrix 1: ${ex.message}")
                                        }
                                }
                                .addOnFailureListener { ex ->
                                    // si se falla al obtener los datos se muestra un mensaje de error por terminal y al usuario
                                    error("Error al obtener las palabras de Palabrix 1.")
                                    println("Error al obtener las palabras de Palabrix 1: ${ex.message}")
                                }
                        }
                        .addOnFailureListener { ex ->
                            // si se falla al obtener los datos se muestra un mensaje de error por terminal y al usuario
                            error("Error al obtener las puntuaciones de Numinario 1.")
                            println("Error al obtener las puntuaciones de Numinario 1: ${ex.message}")
                        }
                }
                .addOnFailureListener { ex ->
                    // si se falla al obtener los datos se muestra un mensaje de error por terminal y al usuario
                    error("Error al obtener las puntuaciones de Euro-banderas.")
                    println("Error al obtener las puntuaciones de Euro-banderas: ${ex.message}")
                }
        }
        .addOnFailureListener { ex ->
            // si se falla al obtener los datos se muestra un mensaje de error por terminal y al usuario
            error("Error al obtener las banderas de Europa.")
            println("Error al obtener los datos de las banderas de Europa: ${ex.message}")
        }
}