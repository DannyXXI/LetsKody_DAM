package com.juandeherrera.letskody.navigation

// clase sellada en la que a cada pantalla se le asigna la ruta para navegar a ella
sealed class AppScreens (val route: String) {

    object login: AppScreens("login")

    object crearUsuario: AppScreens("crearUsuario")

    object verificarEmailUsuario: AppScreens("verificarEmailUsuario")

    object perfil: AppScreens("perfil")
}