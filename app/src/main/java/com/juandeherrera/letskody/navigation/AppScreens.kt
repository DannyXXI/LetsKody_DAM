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

    object MenuEuroBanderas: AppScreens(route = "MenuEuroBanderas")

    object EuroBanderas: AppScreens(route = "EuroBanderas")

    object MenuNuminario1: AppScreens(route = "MenuNuminario1")

    object Numinario1: AppScreens(route = "Numinario1")

    object MenuPalabrix1: AppScreens(route = "MenuPalabrix1")

    object Palabrix1: AppScreens(route = "Palabrix1")

    object Ranking: AppScreens(route = "Ranking")

    object MenuMiscelanea: AppScreens(route = "MenuMiscelanea")

    object EstiraRebota: AppScreens(route = "EstiraRebota")

    object Perfil: AppScreens(route = "Perfil")

    object EditarPerfil: AppScreens(route = "EditarPerfil")
}