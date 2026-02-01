package com.juandeherrera.letskody.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.juandeherrera.letskody.screens.PantallaLogin

@RequiresApi(Build.VERSION_CODES.TIRAMISU) // solo se permite Android 13 o superior (API 33+)
@Composable
fun AppNavigation() {
    val controladorNavegacion = rememberNavController()  // controlador del estado navegación entre las pantallas para desplazarse entre ellas

    val context = LocalContext.current // se obtiene el contexto actual (necesario para la bd local)

    // contenedor que gestiona la navegacion y muestra las pantallas segun la ruta actual
    // se le pasa el controlador del estado de navegacion y la pantalla inicial al abrir la app
    // se muestre el login o la pantalla de perfil en funcion si exista una sesion iniciada
    NavHost(navController = controladorNavegacion, startDestination = AppScreens.login.route) {

        // se define la ruta para la pantalla y se le indica al navegador la función que ejecutará
        composable(route = AppScreens.login.route) { PantallaLogin(controladorNavegacion) }



    }
}