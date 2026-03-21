package com.juandeherrera.letskody.metodosAuxiliares.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// función auxiliar para cargar un elemento del menu lateral de navegación
@Composable
fun ElementoMenuLateral(fuenteTipografica: FontFamily, icono: ImageVector, texto: String, seleccionado: Boolean, peligro: Boolean = false, accion: () -> Unit) {

    // variable para el color del texto según los parámetros
    val colorTexto = when {
        peligro -> Color.Red               // color rojo si es un elemento peligroso (eliminar cuenta)
        seleccionado -> Color(0xFF0D47A1)  // color cuando el elemento esté seleccionado
        else -> Color.Black                // color negro si el elemento no está seleccionado
    }

    // variable para el color del fondo del elemento en función de sí está seleccionado
    val colorFondo = if (seleccionado) { Color(0xFF90CBFF) } else { Color.Transparent }

    // fila que corresponde al elemento del menu lateral de navegación
    Row(
        modifier = Modifier.fillMaxWidth()  // se ocupa el maximo ancho disponible
            .background(color = colorFondo, shape = if (seleccionado) RoundedCornerShape(size = 14.dp) else RectangleShape) // color de fondo (con bordes redondeados si esta seleccionado)
            .padding(all = 14.dp)           // padding interno
            .clickable { accion() },        // al pulsarlo se ejecuta la acción correspondiente
        verticalAlignment = Alignment.CenterVertically  // centrado vertical
    ){
        // icono
        Icon(
            imageVector = icono,              // icono (imagen vectorial)
            contentDescription = texto,       // descripción del icono
            tint = colorTexto,                // color del icono
            modifier = Modifier.size(18.dp)   // tamaño del icono
        )

        Spacer(modifier = Modifier.width(16.dp)) // separación horizontal entre componentes

        // texto
        Text(
            text = texto,        // texto
            color = colorTexto,  // color del texto
            style = TextStyle(
                fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                fontSize = 18.sp                 // tamaño del texto
            )
        )
    }
}

// función auxiliar para cargar el menu lateral del inicio
@Composable
fun MenuLateralInicio(estadoMenuLateral: DrawerState, titulo: String, selectInicio: Boolean, selectServicioTecnico: Boolean, scope: CoroutineScope, controladorNavegacion: NavController, fuenteTipografica: FontFamily) {
    // contenedor visual del menu lateral
    ModalDrawerSheet(
        modifier = Modifier.width(280.dp),        // ancho del menú lateral
        drawerShape = RectangleShape,             // bordes rectangulares (sin bordes redondeados)
        drawerContainerColor = Color(0xFF5FB2FF)  // color de fondo del menu lateral
    ){
        // columna con el contenido del menu lateral
        Column(
            modifier = Modifier.padding(all = 16.dp),         // padding interno
            verticalArrangement = Arrangement.spacedBy(18.dp) // separación vertical entre elementos
        ){
            Spacer(modifier = Modifier.height(26.dp))  // espaciado superior (para que no quede tan pegado arriba)

            // titulo del menu lateral
            Text(
                text = titulo,            // texto
                color = Color(0xFF003E83),  // color del texto
                style = TextStyle(
                    fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                    fontSize = 26.sp,                // tamaño del texto
                    fontWeight = FontWeight.Bold     // texto en negrita
                )
            )

            HorizontalDivider(thickness = 1.dp, color = Color(0xFF003E83)) // linea separadora

            // elemento 1: ir a la pantalla de inicio
            ElementoMenuLateral(
                fuenteTipografica = fuenteTipografica,
                icono = Icons.Default.Home,
                texto = "Inicio",
                seleccionado = selectInicio,
                accion = {
                    if (selectInicio) {
                        scope.launch { estadoMenuLateral.close() }
                    }
                    else {
                        println("Otra acción")
                    }
                }
            )

            // elemento 2: ir a la pantalla de servicio técnico
            ElementoMenuLateral(
                fuenteTipografica = fuenteTipografica,
                icono = Icons.Default.Build,
                texto = "Servicio técnico",
                seleccionado = selectServicioTecnico,
                accion = {
                    if (selectServicioTecnico) {
                        scope.launch { estadoMenuLateral.close() }
                    }
                    else {
                        println("Otra acción")
                    }
                }
            )
        }
    }
}
