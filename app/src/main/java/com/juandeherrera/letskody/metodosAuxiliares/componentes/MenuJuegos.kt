package com.juandeherrera.letskody.metodosAuxiliares.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.juandeherrera.letskody.clasesAuxiliares.juegos
import com.juandeherrera.letskody.metodosAuxiliares.interfaz.fondoDegradadoDiagonal

// función auxiliar para cargar el menu de juegos de cada materia
@Composable
fun MenuJuegosMateria(materia: String, controladorNavegacion: NavController, fuenteTipografica: FontFamily) {
    val juegosMateria = juegos.filter { it.materia == materia }  // lista de juegos filtrada que pertenezcan a la materia recibida en el parámetro

    // lista vertical desplazable que organiza los juegos y el botón de volver
    LazyColumn(
        modifier = Modifier.fillMaxSize()   // ocupa el espacio disponible
            .padding(horizontal = 16.dp),   // padding en los laterales para separarse del borde de la pantalla
        horizontalAlignment = Alignment.CenterHorizontally, // centrado horizontal
        verticalArrangement = Arrangement.spacedBy(16.dp),  // espaciado vertical entre cada tarjeta
        contentPadding = PaddingValues(vertical = 16.dp)    // padding superior e inferior de la lista
    ){
        // se comprueba si la lista de juegos no está vacía
        if (juegosMateria.isNotEmpty()) {

            // se itera sobre el número total de juegos disponibles para generar un item por cada uno
            items(count = juegosMateria.size) { index ->

                val juego = juegosMateria[index] // variable que contiene los datos del juego actual

                // tarjeta que representa visualmente cada juego
                ElevatedCard(
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp), // sombreado de elevación de la tarjeta
                    shape = RoundedCornerShape(size = 30.dp),  // bordes redondeados
                    modifier = Modifier.fillMaxWidth()         // se ocupa el ancho disponible
                        .height(100.dp)                        // altura fija de la tarjeta
                        .clip(shape = RoundedCornerShape(size = 30.dp))  // recorta el contenido seleccionado con el mismo borde redondeado
                        .clickable {
                            controladorNavegacion.navigate(route = juego.ruta)  // al pulsar la tarjeta se navega a la pantalla correspondiente
                        }
                ){
                    // box que ocupa toda la tarjeta y aplica el fondo de la tarjeta
                    Box(
                        modifier = Modifier.fillMaxSize()    // ocupa el espacio disponible
                            .background(brush = fondoDegradadoDiagonal(color1 = juego.color1, color2 = juego.color2, color3 = juego.color3)), // fondo con degradado de la tarjeta
                        contentAlignment = Alignment.Center  // se centra el contenido en el centro
                    ){
                        // TÍTULO DEL JUEGO
                        Text(
                            text = juego.nombre,   // texto
                            color = Color.White,   // color del texto
                            style = TextStyle(
                                fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                                fontSize = 22.sp,                // tamaño del texto
                                fontWeight = FontWeight.Bold,    // texto en negrita
                                textAlign = TextAlign.Center,    // texto alineado centralmente
                                shadow = Shadow(
                                    color = Color.Black.copy(alpha = 0.8f), // color del sombreado (con transparencia)
                                    offset = Offset(x = 2f, y = 2f),        // desplazamiento de la sombra (hacia abajo a la derecha)
                                    blurRadius = 4f                         // radio de desenfoque de la sombra para suavizarla
                                )
                            )
                        )
                    }
                }
            }
        }
        else {
            // si no hay juegos se muestra una tarjeta con un mensaje informativo
            item {

                // tarjeta elevada que muestra el mensaje de sin contenido
                ElevatedCard(
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp), // sombreado de elevación de la tarjeta
                    shape = RoundedCornerShape(size = 30.dp),  // bordes redondeados
                    modifier = Modifier.fillMaxWidth()         // se ocupa el ancho disponible
                        .height(100.dp)                        // altura fija de la tarjeta
                        .clip(shape = RoundedCornerShape(size = 30.dp))  // recorta el contenido seleccionado con el mismo borde redondeado
                ){
                    // box que ocupa toda la tarjeta y aplica el fondo de la tarjeta
                    Box(
                        modifier = Modifier.fillMaxSize()    // ocupa el espacio disponible
                            .background(brush = fondoDegradadoDiagonal(color1 = Color(0xFF9E9E9E), color2 = Color(0xFFBDBDBD), color3 = Color(0xFFE0E0E0))), // fondo con degradado de la tarjeta
                        contentAlignment = Alignment.Center  // se centra el contenido en el centro
                    ){
                        // MENSAJE INFORMATIVO
                        Text(
                            text = "No hay juegos disponibles",   // texto
                            color = Color.Black,   // color del texto
                            style = TextStyle(
                                fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                                fontSize = 22.sp,                // tamaño del texto
                                fontWeight = FontWeight.Bold,    // texto en negrita
                                textAlign = TextAlign.Center,    // texto alineado centralmente
                                shadow = Shadow(
                                    color = Color.Black.copy(alpha = 0.8f), // color del sombreado (con transparencia)
                                    offset = Offset(x = 2f, y = 2f),        // desplazamiento de la sombra (hacia abajo a la derecha)
                                    blurRadius = 4f                         // radio de desenfoque de la sombra para suavizarla
                                )
                            )
                        )
                    }
                }
            }
        }
    }
}