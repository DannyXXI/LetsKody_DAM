package com.juandeherrera.letskody.metodosAuxiliares.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// función auxiliar para cargar la barra superior
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(titulo: String, fuenteTipografica: FontFamily, estadoMenuDesplegable: Boolean, abrirMenuLateral: () -> Unit, abrirMenuDesplegable: () -> Unit, cerrarMenuDesplegable: () -> Unit, cerrarSesionUsuario: () -> Unit) {
    // barra superior
    CenterAlignedTopAppBar(
        modifier = Modifier.height(90.dp),  // altura de la barra superior
        title = {
            Text(
                text = titulo,                       // texto del título centrado
                style = TextStyle(
                    fontFamily = fuenteTipografica,  // fuente tipográfica del texto del título
                    fontSize = 22.sp                 // tamaño del texto del título
                )
            )
        },
        colors = topAppBarColors(
            containerColor = Color(0xFF2364C9),  // color del fondo de la barra superior
            titleContentColor = Color.White      // color del texto del título
        ),
        navigationIcon = {
            // botón de la izquierda (abrir el menu lateral)
            IconButton(
                onClick = { abrirMenuLateral() }  // al pulsarlo se ejecuta la función que abre el menu lateral
            ){
                Icon(
                    imageVector = Icons.Default.Menu,     // icono
                    contentDescription = "Menu lateral",  // descripción del icono
                    tint = Color.White,                   // color del icono
                    modifier = Modifier.size(20.dp)       // tamaño del icono
                )
            }
        },
        actions = {
            // botón de la derecha (abrir menu desplegable)
            IconButton(
                onClick = { abrirMenuDesplegable() } // al pulsarlo se muestra el menu desplegable
            ){
                Icon(
                    imageVector = Icons.Default.MoreVert,  // icono
                    contentDescription = "Menu lateral",   // descripción del icono
                    tint = Color.White,                    // color del icono
                    modifier = Modifier.size(20.dp)        // tamaño del icono
                )
            }

            // menu desplegable
            DropdownMenu(
                expanded = estadoMenuDesplegable,  // controla el estado del menu desplegable
                onDismissRequest = { cerrarMenuDesplegable() },  // función que cerrará el menu desplegable al pulsar afuera
                modifier = Modifier.background(Color(0xFF7D9CEE).copy(alpha = 0.3f))  // color de fondo del menu desplegable
            ){
                // elemento del menu para cerrar la sesión del usuario
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Cerrar sesión",    // texto del elemento
                            color = Color.Black,       // color del texto de elemento
                            style = TextStyle(
                                fontFamily = fuenteTipografica,  // fuente tipográfica del elemento
                                fontSize = 20.sp                 // tamaño de fuente del label
                            )
                        )
                    },
                    onClick = {
                        cerrarSesionUsuario()  // función que se encargará de cerrar la sesión del usuario
                    }
                )
            }
        }
    )
}



