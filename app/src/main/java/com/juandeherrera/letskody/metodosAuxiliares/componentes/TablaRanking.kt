package com.juandeherrera.letskody.metodosAuxiliares.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.juandeherrera.letskody.clasesAuxiliares.FilaTablaRanking

// función auxiliar para cargar la cabecera de la tabla
@Composable
fun FilaCabeceraTabla(cabeceras: List<String>, fuenteTipografica: FontFamily) {
    Row(
        modifier = Modifier.fillMaxWidth()  // se ocupa el máximo ancho disponible
            .background(Color(0xFF1A3A5C))  // color de fondo
            .padding(all = 8.dp),           // padding interno
        horizontalArrangement = Arrangement.Center,     // centrado horizontal
        verticalAlignment = Alignment.CenterVertically  // centrado vertical
    ){
        // columna de la posición
        Text(
            text = cabeceras[0],                   // texto
            color = Color.White,                   // color del texto
            style = TextStyle(
                fontFamily = fuenteTipografica,    // fuente tipográfica del texto
                fontSize = 14.sp,                  // tamaño del texto
                fontWeight = FontWeight.Bold,      // texto en negrita
                textAlign = TextAlign.Center       // texto alineado centralmente
            ),
            modifier = Modifier.width(80.dp)       // ancho fijo
        )

        // columna de los puntos
        Text(
            text = cabeceras[1],                   // texto
            color = Color.White,                   // color del texto
            style = TextStyle(
                fontFamily = fuenteTipografica,    // fuente tipográfica del texto
                fontSize = 14.sp,                  // tamaño del texto
                fontWeight = FontWeight.Bold,      // texto en negrita
                textAlign = TextAlign.Center       // texto alineado centralmente
            ),
            modifier = Modifier.weight(1f)         // ocupe el mismo espacio
        )

        // columna del tiempo o fallos
        Text(
            text = cabeceras[2],                   // texto
            color = Color.White,                   // color del texto
            style = TextStyle(
                fontFamily = fuenteTipografica,    // fuente tipográfica del texto
                fontSize = 14.sp,                  // tamaño del texto
                fontWeight = FontWeight.Bold,      // texto en negrita
                textAlign = TextAlign.Center       // texto alineado centralmente
            ),
            modifier = Modifier.weight(1f)         // ocupe el mismo espacio
        )
    }
}

// función auxiliar para cargar los datos de la tabla
@Composable
fun FilaDatosTabla(fila: FilaTablaRanking, fuenteTipografica: FontFamily) {

    // variable para obtener el color de fondo según la posicion del usuario
    val colorFondo = when (fila.posicion) {
        1    -> Color(0xFFFFD700)
        2    -> Color(0xFFC0C0C0)
        3    -> Color(0xFFCD7F32)
        else -> Color(0xFFD4F1FD)
    }

    Row(
        modifier = Modifier.fillMaxWidth()  // se ocupa el máximo ancho disponible
            .background(colorFondo)         // color de fondo
            .border(width = 2.dp, color = Color(0xFF1A3A5C)) // borde con color
            .padding(all = 9.dp),           // padding interno
        horizontalArrangement = Arrangement.Center,     // centrado horizontal
        verticalAlignment = Alignment.CenterVertically  // centrado vertical
    ){
        // columna de la posición
        Text(
            text = "${fila.posicion.toString()}º",       // texto
            color = Color.Black,                   // color del texto
            style = TextStyle(
                fontFamily = fuenteTipografica,    // fuente tipográfica del texto
                fontSize = 14.sp,                  // tamaño del texto
                fontWeight = FontWeight.Bold,      // texto en negrita
                textAlign = TextAlign.Center       // texto alineado centralmente
            ),
            modifier = Modifier.width(80.dp)       // ancho fijo
        )

        // columna de los puntos
        Text(
            text = fila.columna1,                  // texto
            color = Color.Black,                   // color del texto
            style = TextStyle(
                fontFamily = fuenteTipografica,    // fuente tipográfica del texto
                fontSize = 14.sp,                  // tamaño del texto
                fontWeight = FontWeight.SemiBold,  // texto en negrita
                textAlign = TextAlign.Center       // texto alineado centralmente
            ),
            modifier = Modifier.weight(1f)         // ocupe el mismo espacio
        )

        // columna del tiempo o fallos
        Text(
            text = fila.columna2,                  // texto
            color = Color.Black,                   // color del texto
            style = TextStyle(
                fontFamily = fuenteTipografica,    // fuente tipográfica del texto
                fontSize = 14.sp,                  // tamaño del texto
                fontWeight = FontWeight.SemiBold,  // texto en negrita
                textAlign = TextAlign.Center       // texto alineado centralmente
            ),
            modifier = Modifier.weight(1f)         // ocupe el mismo espacio
        )
    }
}

// función auxiliar para mostrar una tarjeta con su respectiva tabla de ranking
@Composable
fun TablaRanking(titulo: String, cabeceras: List<String>, fila: FilaTablaRanking?, mensajeSinDatos: String, fuenteTipografica: FontFamily) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),  // sombreado de elevación
        colors = CardDefaults.cardColors(containerColor = Color.White),   // color de fondo
        shape = RoundedCornerShape(size = 20.dp),  // bordes redondeados
        modifier = Modifier.fillMaxWidth()         // se ocupa el máximo ancho disponible
    ){
        Column(
            modifier = Modifier.fillMaxWidth()   // se ocupa el máximo ancho disponible
                .padding(all = 16.dp)            // padding externo
        ){
            // TÍTULO DEL JUEGO
            Text(
                text = titulo,                         // texto
                color = Color(0xFF1A3A5C),             // color del texto
                style = TextStyle(
                    fontFamily = fuenteTipografica,    // fuente tipográfica del texto
                    fontSize = 20.sp,                  // tamaño del texto
                    fontWeight = FontWeight.Bold,      // texto en negrita
                    textAlign = TextAlign.Center       // texto alineado centralmente
                ),
                modifier = Modifier.fillMaxWidth()   // se ocupa el máximo ancho disponible
                    .padding(bottom = 12.dp)         // padding inferior
            )

            if (fila == null) {
                // si el usuario no ha jugado al juego, se mostrará un mensaje identificativo
                Text(
                    text = mensajeSinDatos,                // texto
                    color = Color(0xFF1A3A5C),             // color del texto
                    style = TextStyle(
                        fontFamily = fuenteTipografica,    // fuente tipográfica del texto
                        fontSize = 14.sp,                  // tamaño del texto
                        textAlign = TextAlign.Center       // texto alineado centralmente
                    ),
                    modifier = Modifier.fillMaxWidth()     // se ocupa el máximo ancho disponible
                        .padding(vertical = 12.dp)         // padding en los laterales verticales
                )
            }
            else {
                // si el usuario ha jugado al juego, se mostrará la tabla del ranking
                FilaCabeceraTabla(cabeceras = cabeceras, fuenteTipografica = fuenteTipografica)
                FilaDatosTabla(fila = fila, fuenteTipografica = fuenteTipografica)
            }
        }
    }
}


