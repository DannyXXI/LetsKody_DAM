package com.juandeherrera.letskody.metodosAuxiliares.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.juandeherrera.letskody.clasesAuxiliares.materias
import com.juandeherrera.letskody.metodosAuxiliares.interfaz.fondoDegradadoDiagonal

// función auxiliar para cargar el carrusel de materias disponibles
@Composable
fun CarruselMaterias(controladorNavegacion: NavController, fuenteTipografica: FontFamily) {
    // lista horizontal desplazable que renderiza solo los elementos visibles en pantalla
    LazyRow(
        modifier = Modifier.fillMaxWidth(),                   // se ocupa el ancho disponible
        horizontalArrangement = Arrangement.spacedBy(16.dp),  // espaciado horizontal entre cada tarjeta
        contentPadding = PaddingValues(horizontal = 16.dp)    // padding en los laterales para que las tarjetas no se peguen al borde
    ){
        // se itera sobre el número total de materias para generar un item por cada una
        items(count = materias.size) { index ->

            val materia = materias[index]  // variable que contiene los datos de la materia actual

            // tarjeta que representa la material visualmente
            ElevatedCard(
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp), // sombreado de elevación de la tarjeta
                shape = RoundedCornerShape(size = 30.dp),  // bordes redondeados
                modifier = Modifier.width(260.dp)   // ancho fijo de la tarjeta
                    .height(280.dp)                 // altura fija de la tarjeta
                    .clip(shape = RoundedCornerShape(size = 30.dp)) // recorta el contenido seleccionado con el mismo borde redondeado
                    .clickable {
                        controladorNavegacion.navigate(route = materia.ruta)  // al pulsar la tarjeta se navega a la pantalla correspondiente
                    }
            ){
                // box que ocupa toda la tarjeta y aplica el fondo de la tarjeta
                Box(
                    modifier = Modifier.fillMaxSize()  // ocupa el espacio disponible
                        .background(brush = fondoDegradadoDiagonal(color1 = materia.color1, color2 = materia.color2, color3 = materia.color3)), // fondo con degradado de la tarjeta
                    contentAlignment = Alignment.Center  // se centra el contenido en el centro
                ){
                    // columna con la imagen y el título de la materia
                    Column(
                        modifier = Modifier.fillMaxSize()  // ocupa el espacio disponible
                            .padding(all = 16.dp),         // padding interno
                        horizontalAlignment = Alignment.CenterHorizontally,  // centrado horizontal
                        verticalArrangement = Arrangement.SpaceEvenly        // espaciado vertical uniforme entre elementos
                    ){
                        // IMAGEN
                        Image(
                            painter = painterResource(id = materia.idRecursoImagen),  // ruta al recurso (imagen)
                            contentDescription = materia.nombre,                      // texto descriptivo de la imagen
                            modifier = Modifier.size(160.dp),                         // tamaño de la imagen
                            contentScale = ContentScale.Fit                           // forma de escalar la imagen
                        )

                        // TITULO
                        Text(
                            text = materia.nombre,   // texto
                            color = Color.White,     // color del texto
                            style = TextStyle(
                                fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                                fontSize = 24.sp,                // tamaño del texto
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