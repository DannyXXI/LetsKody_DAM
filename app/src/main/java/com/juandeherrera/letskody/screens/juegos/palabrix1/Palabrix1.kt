package com.juandeherrera.letskody.screens.juegos.palabrix1

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.room.Room
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.juandeherrera.letskody.R
import com.juandeherrera.letskody.localdb.AppDB
import com.juandeherrera.letskody.localdb.Estructura
import com.juandeherrera.letskody.metodosAuxiliares.componentes.BarraNavegacionInferior
import com.juandeherrera.letskody.metodosAuxiliares.componentes.BarraSuperiorSinMenuLateral
import com.juandeherrera.letskody.metodosAuxiliares.componentes.ModalInactividadJuego
import com.juandeherrera.letskody.metodosAuxiliares.componentes.ModalPuntuacionJuegosCronometro
import com.juandeherrera.letskody.metodosAuxiliares.componentes.juegos.PantallaJugandoPalabrix1
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.cerrarSesionUsuario
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.juegos.GestorPuntuacionPalabrix1
import com.juandeherrera.letskody.navigation.AppScreens
import com.juandeherrera.letskody.viewModels.palabrix1.EstadoPalabrix1
import com.juandeherrera.letskody.viewModels.palabrix1.Palabrix1ViewModel
import com.juandeherrera.letskody.viewModels.palabrix1.Palabrix1ViewModelFactory

@SuppressLint("DefaultLocale")
@RequiresApi(value = Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PantallaPalabrix1(controladorNavegacion: NavController) {
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
    val viewModel: Palabrix1ViewModel = viewModel(factory = Palabrix1ViewModelFactory(db = db, context = context)) // variable que contiene el ViewModel del juego

    // variables observables de ViewModel que hacen recomponer la vista cuando sus valores cambian
    val estado by viewModel.estado.collectAsState()                                    // estado de juego
    val mostrarModalInactividad by viewModel.mostrarModalInactividad.collectAsState()  // comprueba para mostrar el modal de inactividad
    val cuentaAtras by viewModel.cuentaAtrasInactividad.collectAsState()               // cuenta atrás del modal de inactividad
    val resultado by viewModel.resultado.collectAsState()                              // resultado final del jugador

    LaunchedEffect(key1 = Unit) { viewModel.iniciarJuego() }  // arranca el juego al entrar en la pantalla

    // efecto de vigilancia de la pantalla (si la cuenta atrás del modal llega a cero, sales del juego)
    LaunchedEffect(key1 = cuentaAtras, key2 = mostrarModalInactividad) {
        if (mostrarModalInactividad && cuentaAtras <= 0) {
            controladorNavegacion.navigate(route = AppScreens.MenuEuroBanderas.route) { popUpTo(id = 0) { inclusive = true } } // se navega al menu del juego (se limpia el historial)
        }
    }

    DisposableEffect(key1 = Unit) {
        onDispose {
            viewModel.limpiar()  // detecta cuando el usuario abandona la pantalla y detiene los recursos del juego
        }
    }

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

        // contenedor principal con detección de actividad
        Box(
            modifier = Modifier.fillMaxSize()          // ocupa el espacio disponible
                .background(Color(0xFFC2DAFD))         // color de fondo
                .padding(paddingValues = innerPadding) // padding por defecto
                // capturador de eventos de pulsación sobre la pantalla
                .pointerInput(key1 = Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            awaitPointerEvent()              // esperar cualquier toque
                            viewModel.registrarActividad()   // notificar al ViewModel
                        }
                    }
                }
        ){
            // se reacciona al estado del juego
            when (val s = estado) {

                // sí se está cargando el juego que muestre el círculo de carga
                is EstadoPalabrix1.Cargando -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),  // ocupa el espacio disponible
                        contentAlignment = Alignment.Center // contenido centrado
                    ){
                        CircularProgressIndicator()  // spinner girando de manera infinita
                    }
                }

                // sí no hay suficientes palabras en la base de datos, se mostrará mensaje de error
                is EstadoPalabrix1.ErrorSinPalabras -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),  // ocupa el espacio disponible
                        contentAlignment = Alignment.Center // contenido centrado
                    ){
                        // MENSAJE
                        Text(
                            text = "No hay suficientes palabras en la base de datos.",   // texto
                            color = Color.Red,          // color del texto
                            style = TextStyle(
                                fontFamily = badcomic,  // fuente tipográfica del texto
                                fontSize = 16.sp        // tamaño del texto
                            )
                        )
                    }
                }

                // si el usuario está jugando
                is EstadoPalabrix1.Jugando -> {
                    PantallaJugandoPalabrix1(
                        estado = s,
                        fuenteTipografica = badcomic,
                        respuesta = { opcion -> viewModel.responder(opcion = opcion) }
                    )
                }

                // si el juego ha finalizado
                is EstadoPalabrix1.Terminado -> {
                    Box(modifier = Modifier.fillMaxSize()) // ocupa el espacio disponible
                }
            }

            // se muestra el modal de inactividad si el usuario lleva x tiempo sin tocar la pantalla
            if (mostrarModalInactividad) {
                ModalInactividadJuego(
                    cuentaAtras = cuentaAtras,
                    fuenteTipografica = badcomic,
                    continuar = {
                        viewModel.cerrarModalInactividad()   // cierra el diálogo y reinicia el cronómetro
                    }
                )
            }

            // se muestra el modal de resultado final cuando el usuario ha terminado las preguntas
            if (estado is EstadoPalabrix1.Terminado && resultado != null) {
                ModalPuntuacionJuegosCronometro(
                    resultado = resultado!!,
                    fuenteTipografica = badcomic,
                    repetir = { viewModel.iniciarJuego() },
                    guardarYsalir   = {
                        // se guarda la puntuación y el tiempo en la base de datos local y en Firebase
                        GestorPuntuacionPalabrix1.guardarPuntuacion(
                            db = db,
                            uidUsuario = usuario!!.uidUsuario,
                            puntos = resultado!!.puntos,
                            tiempoTotal = resultado!!.tiempoTotal
                        )

                        // se navega al menu del juego (se limpia la pantalla del historial)
                        controladorNavegacion.navigate(AppScreens.MenuPalabrix1.route) {
                            popUpTo(AppScreens.MenuPalabrix1.route) { inclusive = true }
                            launchSingleTop = true   // evita crear una segunda instancia
                        }
                    }
                )
            }

        }
    }
}


