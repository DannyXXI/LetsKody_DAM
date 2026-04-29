package com.juandeherrera.letskody.metodosAuxiliares.componentes.juegos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// función auxiliar para cargar la fila con la información del resultado del juego
@Composable
fun FilaResultado(etiqueta: String, valor: String, fuenteTipografica: FontFamily, color: Color, negrita: FontWeight) {
    Row(
        modifier = Modifier.fillMaxWidth()  // ocupa el máximo ancho posible
            .background(color = Color(0xFFA9CBFF), shape = RoundedCornerShape(size = 10.dp))  // color de fondo con bordes redondeados
            .padding(horizontal = 12.dp, vertical = 8.dp),  // padding interno
        horizontalArrangement = Arrangement.SpaceBetween,   // espaciado horizontal entre elementos a la misma distancia
        verticalAlignment = Alignment.CenterVertically      // centrado vertical
    ){
        // ETIQUETA
        Text(
            text = etiqueta,    // texto
            color = color,       // color del texto
            style = TextStyle(
                fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                fontSize = 14.sp,                // tamaño del texto
                fontWeight = negrita     // texto en negrita
            )
        )

        // VALOR
        Text(
            text = valor,    // texto
            color = color,       // color del texto
            style = TextStyle(
                fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                fontSize = 14.sp,                // tamaño del texto
                fontWeight = negrita     // texto en negrita
            )
        )
    }
}

// función auxiliar para cargar la fila con la información del tiempo y penalización
@Composable
fun FilaTiempoYPenalizacion(tiempo: String, penalizacion: String, fuenteTipografica: FontFamily) {
    Row(
        modifier = Modifier.fillMaxWidth()  // ocupa el máximo ancho posible
            .background(color = Color(0xFFA9CBFF), shape = RoundedCornerShape(size = 10.dp))  // color de fondo con bordes redondeados
            .padding(horizontal = 12.dp, vertical = 8.dp),  // padding interno
        horizontalArrangement = Arrangement.SpaceBetween,   // espaciado horizontal entre elementos a la misma distancia
        verticalAlignment = Alignment.CenterVertically      // centrado vertical
    ){
        // columna con las etiquetas correspondientes
        Column {
            Text(
                text = "Tiempo de juego",    // texto
                color = Color.Black,         // color del texto
                style = TextStyle(
                    fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                    fontSize = 14.sp,                // tamaño del texto
                )
            )

            Spacer(modifier = Modifier.height(4.dp))  // separación vertical entre componentes

            Text(
                text = "Penalización",    // texto
                color = Color.Red,        // color del texto
                style = TextStyle(
                    fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                    fontSize = 14.sp,                // tamaño del texto
                )
            )
        }

        // columna con los valores correspondientes
        Column(
            horizontalAlignment = Alignment.End  // alineación horizontal a la derecha
        ){
            Text(
                text = tiempo,    // texto
                color = Color.Black,         // color del texto
                style = TextStyle(
                    fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                    fontSize = 14.sp,                // tamaño del texto
                )
            )

            Spacer(modifier = Modifier.height(4.dp))  // separación vertical entre componentes

            Text(
                text = "+ $penalizacion s",    // texto
                color = Color.Red,        // color del texto
                style = TextStyle(
                    fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                    fontSize = 14.sp,                // tamaño del texto
                )
            )
        }
    }
}