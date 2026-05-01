package com.juandeherrera.letskody.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.room.Room
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.juandeherrera.letskody.R
import com.juandeherrera.letskody.localdb.AppDB
import com.juandeherrera.letskody.localdb.Estructura
import com.juandeherrera.letskody.metodosAuxiliares.componentes.BarraNavegacionInferior
import com.juandeherrera.letskody.metodosAuxiliares.componentes.BarraSuperior
import com.juandeherrera.letskody.metodosAuxiliares.componentes.MensajeSnackbarHost
import com.juandeherrera.letskody.metodosAuxiliares.componentes.MenuLateralInicio
import com.juandeherrera.letskody.metodosAuxiliares.componentes.TarjetaConversacion
import com.juandeherrera.letskody.metodosAuxiliares.componentes.TarjetaEntrada
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.cerrarSesionUsuario
import com.juandeherrera.letskody.navigation.AppScreens
import com.juandeherrera.letskody.viewModels.asistenteIA.AsistenteIAViewModel
import kotlinx.coroutines.launch

@SuppressLint("DefaultLocale")
@RequiresApi(value = Build.VERSION_CODES.TIRAMISU) // solo se permite Android 13 o superior (API 33+)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PantallaAsistenteIA(controladorNavegacion: NavController) {
    val badcomic = FontFamily(Font(R.font.badcomic))  // fuente tipográfica por defecto

    val scope = rememberCoroutineScope() // variable que crea un ámbito de corrutinas que se mantienen en la recomposición de la interfaz

    val snackbarHostState = remember { SnackbarHostState() } // variable de estado que controla el estado (mostrar/ocultar) del Snackbar

    val tipoSnackbar = remember { mutableStateOf(value = "error") }  // variable de estado para indicar el tipo de snackbar a mostrar por defecto

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

    val abrirMenuLateral = rememberDrawerState(initialValue = DrawerValue.Closed)  // variable para el estado (abrir/cerrar) del menu lateral de navegación

    val asistenteIAViewModel: AsistenteIAViewModel = viewModel()    // variable que contiene el ViewModel del asistente IA

    val estadoChat by asistenteIAViewModel.estado.collectAsState()  // variable observable del ViewModel sobre el estado del chat

    // MENU LATERAL DE NAVEGACIÓN
    ModalNavigationDrawer(
        drawerState = abrirMenuLateral,  // controla el estado del menu lateral de navegación
        // el contenido que aparece dentro del menu lateral
        drawerContent = {
            MenuLateralInicio(
                estadoMenuLateral = abrirMenuLateral,
                titulo = "Asistente IA",
                selectInicio = false,
                selectAsistente = true,
                selectServicioTecnico = false,
                scope = scope,
                controladorNavegacion = controladorNavegacion,
                fuenteTipografica = badcomic
            )
        }
    ){
        Scaffold(
            // BARRA SUPERIOR
            topBar = {
                BarraSuperior(
                    titulo = "Asistente IA",
                    fuenteTipografica = badcomic,
                    estadoMenuDesplegable = abrirToolbar,
                    abrirMenuLateral = {
                        scope.launch { abrirMenuLateral.open() }
                    },
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
                    selectInicio = true,
                    selectMaterias = false,
                    selectPerfil = false
                )
            },
            // define el lugar donde se mostraran los Snackbar
            snackbarHost = {
                MensajeSnackbarHost(snackbarHostState = snackbarHostState, fuenteTipografica = badcomic, tipo = tipoSnackbar.value)
            }
        ){
            innerPadding ->

            Column(
                modifier = Modifier.fillMaxSize()           // se ocupa la pantalla completa
                    .background(Color(0xFFC2DAFD))          // color de fondo
                    .padding(paddingValues = innerPadding)  // padding por defecto
            ){
                // tarjeta donde se muestra los mensajes del chat
                TarjetaConversacion(
                    mensajes = estadoChat.mensajes,
                    cargando = estadoChat.cargando,
                    fuenteTipografica = badcomic,
                    modifier = Modifier.fillMaxWidth()                // se ocupa el ancho disponible
                        .weight(1f)                                   // ocupa el espacio disponible restante
                        .padding(horizontal = 8.dp, vertical = 6.dp)  // padding interno
                )

                // tarjeta donde se muestra el campo de texto y el botón de enviar
                TarjetaEntrada(
                    cargando = estadoChat.cargando,
                    fuenteTipografica = badcomic,
                    enviar = { texto -> asistenteIAViewModel.enviarMensaje(textoUsuario = texto)}
                )
            }
        }
    }
}