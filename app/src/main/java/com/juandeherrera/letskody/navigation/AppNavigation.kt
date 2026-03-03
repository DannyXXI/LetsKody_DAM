package com.juandeherrera.letskody.navigation

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.juandeherrera.letskody.clasesAuxiliares.DetectorRed
import com.juandeherrera.letskody.screens.PantallaCrearUsuario
import com.juandeherrera.letskody.screens.PantallaLogin

@RequiresApi(value = Build.VERSION_CODES.TIRAMISU) // solo se permite Android 13 o superior (API 33+)
@Composable
fun AppNavigation() {
    val controladorNavegacion = rememberNavController()  // controlador del estado navegación entre las pantallas para desplazarse entre ellas

    val context = LocalContext.current // se obtiene el contexto actual

    val detectorRed = remember { DetectorRed(context = context) } // se crea una instancia recordable del detector de red

    val hayInternet by detectorRed.hayInternet.collectAsState()  // estado observable que indica si existe conexión a Internet

    val usuarioActual = FirebaseAuth.getInstance().currentUser  // se obtiene el usuario iniciado en Firebase (si no existe, será nulo)

    // si hay Internet, se muestra la navegación normal de la aplicación
    if (hayInternet) {

        // contenedor que gestiona la navegación y muestra las pantallas según la ruta actual
        // se le pasa el controlador del estado de navegación y la pantalla inicial al abrir la app
        // se muestre el login o la pantalla de perfil en función si existe una sesión iniciada en Firebase
        NavHost(navController = controladorNavegacion, startDestination = if (usuarioActual != null) { AppScreens.Perfil.route } else { AppScreens.Login.route }) {

            // se definen las rutas para las pantallas y se le indica al navegador la función que se ejecutará
            composable(route = AppScreens.Login.route) { PantallaLogin(controladorNavegacion = controladorNavegacion) }

            composable(route = AppScreens.CrearUsuario.route) {
                BackHandler(enabled = true) {} // impide al usuario ir a la pantalla anterior usando el botón físico del dispositivo
                PantallaCrearUsuario(controladorNavegacion = controladorNavegacion)
            }

        }
    }
    else {
        // si no hay Internet, se bloqueará la aplicación en esta pantalla aislada
    }
}