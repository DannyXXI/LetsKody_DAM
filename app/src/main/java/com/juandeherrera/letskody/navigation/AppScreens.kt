package com.juandeherrera.letskody.navigation

// clase sellada en la que a cada pantalla se le asigna la ruta para navegar a ella
sealed class AppScreens (val route: String) {

    object Login: AppScreens(route = "Login")

    object CrearUsuario: AppScreens(route = "CrearUsuario")

    object VerificarEmailUsuario: AppScreens(route = "VerificarEmailUsuario")

    object Inicio: AppScreens(route = "Inicio")

    object ServicioTecnico: AppScreens(route = "ServicioTecnico")

    object Materias: AppScreens(route = "Materias")

    object MenuJuegosMaterias: AppScreens(route = "MenuJuegosMaterias")

    object Perfil: AppScreens(route = "Perfil")

    object EditarPerfil: AppScreens(route = "EditarPerfil")
}