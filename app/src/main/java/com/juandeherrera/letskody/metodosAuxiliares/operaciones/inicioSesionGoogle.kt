package com.juandeherrera.letskody.metodosAuxiliares.operaciones

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.navigation.NavController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.juandeherrera.letskody.localdb.AppDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val WEB_CLIENT_ID = "471155651747-togjsf3ome69vggf8nb80ftaiidm93gd.apps.googleusercontent.com"  // id obtenido de Firebase

// función auxiliar principal para la gestión del inicio de sesión con Google
fun iniciarSesionGoogle(context: Context, scope: CoroutineScope, db: AppDB, controladorNavegacion: NavController, error: (String) -> Unit) {

    val auth = FirebaseAuth.getInstance()  // instancia al sistema de autenticación de Firebase

    val dbfire = FirebaseFirestore.getInstance()  // instancia a la base de datos de Firebase asociada a la aplicación

    val gestorCredenciales = CredentialManager.create(context)  // se crea el gestor de credenciales de Android

    // se configura la opción de Google con el WEB CLIENT ID
    val opcionGoogle = GetGoogleIdOption.Builder()
        .setServerClientId(WEB_CLIENT_ID)      // id del cliente web de Firebase
        .setFilterByAuthorizedAccounts(false)  // se permite cuentas nuevas aparte de las autorizadas
        .setAutoSelectEnabled(false)           // se muestra siempre el selector de cuentas al usuario
        .build()

    // se construye la solicitud de credenciales con la opción de Google
    val solicitud = GetCredentialRequest.Builder().addCredentialOption(opcionGoogle).build()

    // se lanza una corrutina porque el gestor de credenciales es una operación asíncrona
    scope.launch {
        try {
            // se muestra el selector de cuentas de Google y se espera la respuesta del usuario
            val respuesta = gestorCredenciales.getCredential(request = solicitud, context = context)









        }
        catch (ex: GetCredentialException) {
            // si el usuario cancela la selección de cuenta o hay un error en el selector, se muestra en la terminal
            println("Selector de Google cancelado o error: ${ex.message}")
        }
    }
}
