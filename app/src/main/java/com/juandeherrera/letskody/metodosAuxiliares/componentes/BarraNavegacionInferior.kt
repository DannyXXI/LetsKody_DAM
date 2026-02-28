package com.juandeherrera.letskody.metodosAuxiliares.componentes

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.composables.icons.lucide.Book
import com.composables.icons.lucide.BriefcaseBusiness
import com.composables.icons.lucide.Lucide
import com.juandeherrera.letskody.navigation.AppScreens

// metodo auxiliar para cargar la barra de navegación inferior de la aplicacion
@Composable
fun BarraNavegacionInferior(badcomic: FontFamily, controladorNavegacion: NavController, selectInicio: Boolean, selectClases: Boolean, selectPerfil: Boolean) {
    // BARRA DE NAVEGACIÓN INFERIOR
    NavigationBar(
        containerColor = Color(0xFF2364C9) // color de fondo de la barra de navegación inferior
    ){
        // INICIO
        NavigationBarItem(
            selected = selectInicio, // comprueba si esta seleccionado el item de navegación
            onClick = {
                controladorNavegacion.navigate(AppScreens.perfil.route)  // al pulsarlo se navega a la seccion de inicio
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home, // icono
                    contentDescription = "inicio",    // descripcion del icono
                    modifier = Modifier.size(30.dp)   // tamaño del icono
                )
            },
            label = {
                Text(
                    text = "Inicio",  // texto
                    style = TextStyle(
                        fontFamily = badcomic,  // fuente tipografica del texto
                        fontSize = 14.sp,       // tamaño de la fuente del texto
                        fontWeight = if (selectInicio) FontWeight.Bold else FontWeight.Normal  // texto en negrita si esta seleccionado
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF2364C9),  // color del icono al ser seleccionado
                selectedTextColor = Color.White,        // color del texto al ser seleccionado
                indicatorColor = Color(0xFFA2BCE1),     // color de fondo del item seleccionado
                unselectedIconColor = Color.White,      // color del icono no seleccionado
                unselectedTextColor = Color.White       // color del texto no seleccionado
            )
        )

        // MATERIAS
        NavigationBarItem(
            selected = selectInicio, // comprueba si esta seleccionado el item de navegación
            onClick = {
                controladorNavegacion.navigate(AppScreens.perfil.route)  // al pulsarlo se navega a la seccion de inicio
            },
            icon = {
                Icon(
                    imageVector = Lucide.BriefcaseBusiness, // icono
                    contentDescription = "materias",    // descripcion del icono
                    modifier = Modifier.size(30.dp)   // tamaño del icono
                )
            },
            label = {
                Text(
                    text = "Materias",  // texto
                    style = TextStyle(
                        fontFamily = badcomic,  // fuente tipografica del texto
                        fontSize = 14.sp,       // tamaño de la fuente del texto
                        fontWeight = if (selectInicio) FontWeight.Bold else FontWeight.Normal  // texto en negrita si esta seleccionado
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF2364C9),  // color del icono al ser seleccionado
                selectedTextColor = Color.White,        // color del texto al ser seleccionado
                indicatorColor = Color(0xFFA2BCE1),     // color de fondo del item seleccionado
                unselectedIconColor = Color.White,      // color del icono no seleccionado
                unselectedTextColor = Color.White       // color del texto no seleccionado
            )
        )

        // PERFIL
        NavigationBarItem(
            selected = selectInicio, // comprueba si esta seleccionado el item de navegación
            onClick = {
                controladorNavegacion.navigate(AppScreens.perfil.route)  // al pulsarlo se navega a la seccion de inicio
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Person, // icono
                    contentDescription = "perfil",    // descripcion del icono
                    modifier = Modifier.size(30.dp)   // tamaño del icono
                )
            },
            label = {
                Text(
                    text = "Perfil",  // texto
                    style = TextStyle(
                        fontFamily = badcomic,  // fuente tipografica del texto
                        fontSize = 14.sp,       // tamaño de la fuente del texto
                        fontWeight = if (selectInicio) FontWeight.Bold else FontWeight.Normal  // texto en negrita si esta seleccionado
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF2364C9),  // color del icono al ser seleccionado
                selectedTextColor = Color.White,        // color del texto al ser seleccionado
                indicatorColor = Color(0xFFA2BCE1),     // color de fondo del item seleccionado
                unselectedIconColor = Color.White,      // color del icono no seleccionado
                unselectedTextColor = Color.White       // color del texto no seleccionado
            )
        )
    }
}