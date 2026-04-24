package com.juandeherrera.letskody.navigation

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.juandeherrera.letskody.clasesAuxiliares.DetectorRed
import com.juandeherrera.letskody.screens.PantallaCrearUsuario
import com.juandeherrera.letskody.screens.PantallaEditarPerfil
import com.juandeherrera.letskody.screens.PantallaInicio
import com.juandeherrera.letskody.screens.PantallaLogin
import com.juandeherrera.letskody.screens.PantallaMaterias
import com.juandeherrera.letskody.screens.PantallaMenuJuegosMaterias
import com.juandeherrera.letskody.screens.PantallaPerfil
import com.juandeherrera.letskody.screens.PantallaRanking
import com.juandeherrera.letskody.screens.PantallaServicioTecnico
import com.juandeherrera.letskody.screens.PantallaSinConexion
import com.juandeherrera.letskody.screens.PantallaVerificarEmailUsuario
import com.juandeherrera.letskody.screens.juegos.euroBanderas.PantallaEuroBanderas
import com.juandeherrera.letskody.screens.juegos.euroBanderas.PantallaMenuEuroBanderas
import com.juandeherrera.letskody.screens.juegos.numinario1.PantallaMenuNuminario1
import com.juandeherrera.letskody.screens.juegos.numinario1.PantallaNuminario1
import com.juandeherrera.letskody.screens.juegos.palabrix1.PantallaMenuPalabrix1

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
        NavHost(navController = controladorNavegacion, startDestination = if (usuarioActual != null) { AppScreens.Inicio.route } else { AppScreens.Login.route }) {

            // se definen las rutas para las pantallas y se le indica al navegador la función que se ejecutará
            composable(route = AppScreens.Login.route) { PantallaLogin(controladorNavegacion = controladorNavegacion) }

            composable(route = AppScreens.CrearUsuario.route) {
                BackHandler(enabled = true) {} // impide al usuario ir a la pantalla anterior usando el botón físico del dispositivo
                PantallaCrearUsuario(controladorNavegacion = controladorNavegacion)
            }

            composable(route = AppScreens.VerificarEmailUsuario.route) {
                BackHandler(enabled = true) {} // impide al usuario ir a la pantalla anterior usando el botón físico del dispositivo
                PantallaVerificarEmailUsuario(controladorNavegacion = controladorNavegacion)
            }

            composable(route = AppScreens.Inicio.route) {
                BackHandler(enabled = true) {} // impide al usuario ir a la pantalla anterior usando el botón físico del dispositivo
                PantallaInicio(controladorNavegacion = controladorNavegacion)
            }

            composable(route = AppScreens.ServicioTecnico.route) {
                BackHandler(enabled = true) {} // impide al usuario ir a la pantalla anterior usando el botón físico del dispositivo
                PantallaServicioTecnico(controladorNavegacion = controladorNavegacion)
            }

            composable(route = AppScreens.Materias.route) {
                BackHandler(enabled = true) {} // impide al usuario ir a la pantalla anterior usando el botón físico del dispositivo
                PantallaMaterias(controladorNavegacion = controladorNavegacion)
            }

            composable(
                route = AppScreens.MenuJuegosMaterias.route + "/{materia}",
                arguments = listOf(
                    navArgument(name = "materia"){ type = NavType.StringType } // se indica el tipo del argumento de la ruta
                )
            ){
                backStackEntry ->

                BackHandler(enabled = true) {} // impide al usuario ir a la pantalla anterior usando el botón físico del dispositivo

                val materia = backStackEntry.arguments?.getString("materia")  // variable con el valor del argumento

                // si el argumento no es nulo, se realiza la navegación
                if (materia != null) {
                    PantallaMenuJuegosMaterias(controladorNavegacion = controladorNavegacion, materia = materia)
                }
            }

            composable(route = AppScreens.MenuEuroBanderas.route) {
                BackHandler(enabled = true) {} // impide al usuario ir a la pantalla anterior usando el botón físico del dispositivo
                PantallaMenuEuroBanderas(controladorNavegacion = controladorNavegacion)
            }

            composable(route = AppScreens.EuroBanderas.route) {
                BackHandler(enabled = true) {} // impide al usuario ir a la pantalla anterior usando el botón físico del dispositivo
                PantallaEuroBanderas(controladorNavegacion = controladorNavegacion)
            }

            composable(route = AppScreens.MenuNuminario1.route) {
                BackHandler(enabled = true) {} // impide al usuario ir a la pantalla anterior usando el botón físico del dispositivo
                PantallaMenuNuminario1(controladorNavegacion = controladorNavegacion)
            }

            composable(route = AppScreens.Numinario1.route) {
                BackHandler(enabled = true) {} // impide al usuario ir a la pantalla anterior usando el botón físico del dispositivo
                PantallaNuminario1(controladorNavegacion = controladorNavegacion)
            }

            composable(route = AppScreens.MenuPalabrix1.route) {
                BackHandler(enabled = true) {} // impide al usuario ir a la pantalla anterior usando el botón físico del dispositivo
                PantallaMenuPalabrix1(controladorNavegacion = controladorNavegacion)
            }

            composable(route = AppScreens.Ranking.route) {
                BackHandler(enabled = true) {} // impide al usuario ir a la pantalla anterior usando el botón físico del dispositivo
                PantallaRanking(controladorNavegacion = controladorNavegacion)
            }

            composable(route = AppScreens.Perfil.route) {
                BackHandler(enabled = true) {} // impide al usuario ir a la pantalla anterior usando el botón físico del dispositivo
                PantallaPerfil(controladorNavegacion = controladorNavegacion)
            }

            composable(route = AppScreens.EditarPerfil.route) {
                BackHandler(enabled = true) {} // impide al usuario ir a la pantalla anterior usando el botón físico del dispositivo
                PantallaEditarPerfil(controladorNavegacion = controladorNavegacion)
            }

        }
    }
    else {
        PantallaSinConexion()// si no hay Internet, se bloqueará la aplicación en esta pantalla aislada
    }
}