package com.juandeherrera.letskody.screens.juegos.palabrix1

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.room.Room
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.juandeherrera.letskody.R
import com.juandeherrera.letskody.localdb.AppDB
import com.juandeherrera.letskody.localdb.Estructura
import com.juandeherrera.letskody.metodosAuxiliares.componentes.BarraNavegacionInferior
import com.juandeherrera.letskody.metodosAuxiliares.componentes.BarraSuperiorSinMenuLateral
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.cerrarSesionUsuario
import com.juandeherrera.letskody.navigation.AppScreens


@SuppressLint("DefaultLocale")
@RequiresApi(value = Build.VERSION_CODES.TIRAMISU) // solo se permite Android 13 o superior (API 33+)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PantallaMenuPalabrix1(controladorNavegacion: NavController) {
    val badcomic = FontFamily(Font(R.font.badcomic))  // fuente tipográfica por defecto

    val context = LocalContext.current // variable que obtiene el contexto actual

    // instancia a la base de datos local (en el mismo hilo)
    val db = Room.databaseBuilder(context, klass = AppDB::class.java, name = Estructura.DB.NAME).allowMainThreadQueries().build()

    val usuario by db.usuarioDao().getUser().collectAsState(initial = null)  // variable que contiene los datos del usuario

    when (usuario) {
        null -> {
            // si no existe el usuario o esta cargando el usuario
            Box(
                modifier = Modifier.fillMaxSize(),  // ocupa la pantalla completa
                contentAlignment = Alignment.Center // contenido centrado en el medio
            ) {
                CircularProgressIndicator() // spinner girando de manera infinita
            }
            return
        }
        else -> { /* aquí ya existe el usuario */ }
    }

    var abrirToolbar by remember { mutableStateOf(value = false) } // variable para el estado (abrir/cerrar) del menu desplegable del toolbar

    Scaffold(
        // BARRA SUPERIOR
        topBar = {
            BarraSuperiorSinMenuLateral(
                titulo = "Palabrix I",
                fuenteTipografica = badcomic,
                controladorNavegacion = controladorNavegacion,
                estadoMenuDesplegable = abrirToolbar,
                abrirMenuDesplegable = { abrirToolbar = true },
                cerrarMenuDesplegable = { abrirToolbar = false },
                cerrarSesionUsuario = {
                    cerrarSesionUsuario(db = db, usuario = usuario!!)  // se cierra la sesión de Firebase y se borran los datos locales
                    controladorNavegacion.navigate(route = AppScreens.Login.route) { popUpTo(id = 0) {inclusive = true} }
                }
            )
        },
        // BARRA INFERIOR
        bottomBar = {
            BarraNavegacionInferior(
                fuenteTipografica = badcomic,
                controladorNavegacion = controladorNavegacion,
                selectInicio = false,
                selectMaterias = true,
                selectPerfil = false
            )
        }
    ){
            innerPadding ->

        Column(
            modifier = Modifier.fillMaxSize()       // se ocupa la pantalla completa
                .background(Color(0xFFC2DAFD))                   // color de fondo
                .padding(paddingValues = innerPadding), // padding por defecto
            horizontalAlignment = Alignment.CenterHorizontally,  // centrado horizontal
            verticalArrangement = Arrangement.Center             // centrado vertical
        ){
            // tarjeta con la información del menu del juego
            ElevatedCard(
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),  // sombreado de la tarjeta
                colors = CardDefaults.cardColors(containerColor = Color.White),   // color de fondo de la tarjeta
                shape = RoundedCornerShape(size = 20.dp), // bordes redondeados
                modifier = Modifier.fillMaxWidth()        // se ocupa el maximo ancho disponible
                    .padding(all = 20.dp)                 // padding externo
            ){
                // columna con el contenido de la tarjeta del servicio técnico
                Column(
                    modifier = Modifier.fillMaxWidth()        // se ocupa el maximo ancho disponible
                        .padding(all = 22.dp),                // padding externo
                    horizontalAlignment = Alignment.CenterHorizontally,  // centrado horizontal
                    verticalArrangement = Arrangement.spacedBy(16.dp)    // espacio vertical entre elementos
                ){
                    // TITULO
                    Text(
                        text = "¡Palabras rebeldes!", // texto
                        color = Color(0xFF12A4BE),        // color del texto
                        style = TextStyle(
                            fontFamily = badcomic,        // fuente tipográfica del texto
                            fontSize = 20.sp,             // tamaño del texto
                            fontWeight = FontWeight.Bold, // texto en negrita
                            textAlign = TextAlign.Center  // texto alineado centralmente
                        )
                    )

                    Text(
                        text = "El doctor Letramix ha logrado que el diccionario cobre vida ... ¡y ahora las palabras se han olvidado de que tipo son!", // texto
                        color = Color.Black,                // color del texto
                        style = TextStyle(
                            fontFamily = badcomic,          // fuente tipográfica del texto
                            fontSize = 14.sp,               // tamaño del texto
                            textAlign = TextAlign.Justify,  // texto alineado centralmente
                            lineHeight = 20.sp              // espaciado vertical entre líneas
                        )
                    )

                    Text(
                        text = "Tu misión será descubrir el tipo de cada palabra antes que el diccionario se vuelva loco. ¿Cuántas podrás acertar en el menor tiempo posible?", // texto
                        color = Color.Black,                // color del texto
                        style = TextStyle(
                            fontFamily = badcomic,          // fuente tipográfica del texto
                            fontSize = 14.sp,               // tamaño del texto
                            textAlign = TextAlign.Justify,  // texto alineado centralmente
                            lineHeight = 20.sp              // espaciado vertical entre líneas
                        )
                    )

                    Button(
                        onClick = { controladorNavegacion.navigate(route = AppScreens.Numinario1.route) }, // al pulsar te lleva al juego
                        modifier = Modifier.fillMaxWidth() // se ocupa el maximo ancho disponible
                            .height(50.dp),                // altura del botón
                        shape = RoundedCornerShape(size = 12.dp),  // bordes redondeados
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF12A4BE),  // color de fondo del botón
                            contentColor = Color.White           // color del texto del botón
                        )
                    ){
                        Text(
                            text = "Jugar",
                            style = TextStyle(
                                fontFamily = badcomic,        // fuente tipográfica del texto
                                fontSize = 16.sp,             // tamaño del texto
                                fontWeight = FontWeight.Bold  // texto en negrita
                            )
                        )
                    }

                }
            }
        }
    }
}