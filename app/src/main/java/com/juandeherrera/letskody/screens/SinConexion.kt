package com.juandeherrera.letskody.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.juandeherrera.letskody.R
import com.juandeherrera.letskody.metodosAuxiliares.interfaz.fondoDegradadoDiagonal

@SuppressLint("DefaultLocale")
@RequiresApi(value = Build.VERSION_CODES.TIRAMISU) // solo se permite Android 13 o superior (API 33+)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PantallaSinConexion() {
    val badcomic = FontFamily(Font(R.font.badcomic))  // fuente tipográfica por defecto

    val degradadoDiagonal = fondoDegradadoDiagonal(color1 = Color(0xFF0D47A1), color2 = Color(0xFF1976D2), color3 = Color(0xFF42A5F5))  // variable para obtener el degradado

    Scaffold{
        innerPadding ->

        Column(
            modifier = Modifier.fillMaxSize()               // ocupa el espacio disponible
                .background(brush = degradadoDiagonal)      // fondo con degradado animado
                .padding(paddingValues = innerPadding),     // usa el padding por defecto
            horizontalAlignment = Alignment.CenterHorizontally,   // centrado horizontal
            verticalArrangement = Arrangement.Center              // centrado vertical
        ){
            // tarjeta elevada donde se mostrara el mensaje de falta de conexión
            ElevatedCard(
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),  // sombreado de elevación de la tarjeta
                colors = CardDefaults.cardColors(containerColor = Color.White),   // color de fondo de la tarjeta
                modifier = Modifier.fillMaxWidth()         // ocupa el maximo ancho posible
                    .padding(start = 30.dp, end = 30.dp)   // padding en los laterales
            ){
                Column(
                    modifier = Modifier.fillMaxWidth()  // ocupa el ancho maximo posible
                        .padding(16.dp),           // padding interior
                    horizontalAlignment = Alignment.CenterHorizontally,   // centrado horizontal
                    verticalArrangement = Arrangement.Center              // centrado vertical
                ){
                    // TITULO
                    Text(
                        text = "No hay Internet",   // texto
                        color = Color(0xFF017DB2),  // color del texto
                        style = TextStyle(
                            fontFamily = badcomic,  // fuente tipográfica
                            fontSize = 38.sp        // tamaño de fuente
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))  // separación vertical entre componentes

                    // MENSAJE DE AVISO
                    Text(
                        text = "¡Oh no! Hemos perdido la conexión de Internet y Kody la necesita estar actualizado para poder seguir jugando contigo.",   // texto
                        color = Color.Black,               // color del texto
                        style = TextStyle(
                            fontFamily = badcomic,         // fuente tipográfica
                            fontSize = 17.sp,              // tamaño de fuente
                            textAlign = TextAlign.Justify  // texto alineado de manera justificada
                        )
                    )

                    // IMAGEN DE FALTA DE CONEXIÓN INTERNET
                    Image(
                        painter = painterResource(id = R.drawable.kodysininternet),  // ruta al recurso (imagen)
                        contentDescription = "Lets Kody",                            // texto descriptivo de la imagen
                        contentScale = ContentScale.Fit,                             // forma de escalar la imagen
                        modifier = Modifier.size(210.dp)                             // tamaño de la imagen
                    )

                    // fila del mensaje simulado que intenta recuperar la conexión
                    Row(
                        verticalAlignment = Alignment.CenterVertically,     // centrado vertical
                        horizontalArrangement = Arrangement.spacedBy(8.dp)  // espacio horizontal entre elementos
                    ){
                        // texto
                        Text(
                            text = "Intentando recuperar la conexión",  // texto
                            color = Color.Black,                        // color del texto
                            style = TextStyle(
                                fontFamily = badcomic,                  // fuente tipográfica
                                fontSize = 17.sp,                       // tamaño de fuente
                            )
                        )

                        // barra de progreso infinita de forma circular
                        CircularProgressIndicator(
                            color = Color(0xFF017DB2),       // color
                            strokeWidth = 3.dp,              // grosor
                            modifier = Modifier.size(17.dp)  // tamaño de la barra de progreso
                        )
                    }
                }
            }
        }
    }
}