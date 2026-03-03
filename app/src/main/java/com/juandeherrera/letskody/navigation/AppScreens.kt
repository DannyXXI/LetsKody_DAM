package com.juandeherrera.letskody.navigation

// clase sellada en la que a cada pantalla se le asigna la ruta para navegar a ella
sealed class AppScreens (val route: String) {

    object Login: AppScreens(route = "Login")

    object CrearUsuario: AppScreens(route = "CrearUsuario")

    object verificarEmailUsuario: AppScreens("verificarEmailUsuario")

    object Perfil: AppScreens(route = "Perfil")
}