package com.juandeherrera.letskody.screens.juegos.numinario1

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.room.Room
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.juandeherrera.letskody.R
import com.juandeherrera.letskody.localdb.AppDB
import com.juandeherrera.letskody.localdb.Estructura
import com.juandeherrera.letskody.metodosAuxiliares.componentes.BarraNavegacionInferior
import com.juandeherrera.letskody.metodosAuxiliares.componentes.BarraSuperiorSinMenuLateral
import com.juandeherrera.letskody.metodosAuxiliares.componentes.ModalPuntuacionJuegosContrarreloj
import com.juandeherrera.letskody.metodosAuxiliares.componentes.juegos.PantallaJugando
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.cerrarSesionUsuario
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.juegos.GestorPuntuacionNuminario1
import com.juandeherrera.letskody.navigation.AppScreens
import com.juandeherrera.letskody.viewModels.numinario1.EstadoNuminario1
import com.juandeherrera.letskody.viewModels.numinario1.Numinario1ViewModel
import com.juandeherrera.letskody.viewModels.numinario1.Numinario1ViewModelFactory

@SuppressLint("DefaultLocale")
@RequiresApi(value = Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PantallaNuminario1(controladorNavegacion: NavController) {
    val badcomic = FontFamily(Font(R.font.badcomic))  // fuente tipográfica por defecto

    val context = LocalContext.current // variable que obtiene el contexto actual

    // instancia a la base de datos local (en el mismo hilo)
    val db = remember {Room.databaseBuilder(context, klass = AppDB::class.java, name = Estructura.DB.NAME).allowMainThreadQueries().build() }

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

    // variable que contiene el ViewModel del juego (se recupera o se crea si no existe)
    val viewModel: Numinario1ViewModel = viewModel(factory = Numinario1ViewModelFactory(context = context)) // variable que contiene el ViewModel del juego

    // variables observables de ViewModel que hacen recomponer la vista cuando sus valores cambian
    val estado by viewModel.estado.collectAsState()                  // estado de juego
    val resultado by viewModel.resultado.collectAsState()            // resultado final del jugador

    var textoRespuesta by rememberSaveable { mutableStateOf(value = "") }  // variable para la respuesta del usuario (se conserva su valor a pesar de las recomposiciones)

    LaunchedEffect(key1 = Unit) { viewModel.iniciarJuego() }  // arranca el juego al entrar en la pantalla

    // al generar una nueva operación se limpia el campo de texto de la respuesta del usuario
    LaunchedEffect(key1 = (estado as? EstadoNuminario1.Jugando)?.numero1, key2 = (estado as? EstadoNuminario1.Jugando)?.numero2) {
        textoRespuesta = ""
    }

    Scaffold(
        // BARRA SUPERIOR
        topBar = {
            BarraSuperiorSinMenuLateral(
                titulo = "Numinario I",
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

        // contenedor principal
        Box(
            modifier = Modifier.fillMaxSize()          // ocupa el espacio disponible
                .background(Color(0xFFC2DAFD))         // color de fondo
                .padding(paddingValues = innerPadding) // padding por defecto
        ){
            // se reacciona al estado del juego
            when (val s = estado) {
                // sí se está cargando el juego que muestre el círculo de carga
                is EstadoNuminario1.Cargando -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),  // ocupa el espacio disponible
                        contentAlignment = Alignment.Center // contenido centrado
                    ){
                        CircularProgressIndicator()  // spinner girando de manera infinita
                    }
                }

                // si el usuario está jugando
                is EstadoNuminario1.Jugando -> {
                    PantallaJugando(
                        estado = s,
                        respuesta = textoRespuesta,
                        fuenteTipografica = badcomic,
                        cambiar = { textoRespuesta = it },
                        comprobar = { viewModel.comprobarRespuesta(respuesta = textoRespuesta) }
                    )
                }

                // si el juego ha finalizado
                is EstadoNuminario1.Terminado -> {
                    Box(modifier = Modifier.fillMaxSize()) // ocupa el espacio disponible
                }
            }

            // se muestra el modal de resultado final cuando se ha terminado el tiempo
            if (estado is EstadoNuminario1.Terminado && resultado != null) {
                ModalPuntuacionJuegosContrarreloj(
                    resultado = resultado!!,
                    fuenteTipografica = badcomic,
                    repetir = {
                        // se limpia el campo de texto y se inicia de nuevo el juego
                        textoRespuesta = ""
                        viewModel.iniciarJuego()
                    },
                    guardarYsalir   = {
                        // se guarda la puntuación y el tiempo en la base de datos local y en Firebase
                        GestorPuntuacionNuminario1.guardarPuntuacion(
                            db = db,
                            uidUsuario = usuario!!.uidUsuario,
                            puntos = resultado!!.puntos,
                            fallos = resultado!!.fallos
                        )

                        // se navega al menu del juego (se limpia el historial)
                        controladorNavegacion.navigate(AppScreens.MenuNuminario1.route) { popUpTo(id = 0) { inclusive = true } }
                    }
                )
            }
        }
    }
}



