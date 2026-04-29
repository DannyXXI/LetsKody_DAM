package com.juandeherrera.letskody.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
import androidx.navigation.NavController
import androidx.room.Room
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.juandeherrera.letskody.R
import com.juandeherrera.letskody.localdb.AppDB
import com.juandeherrera.letskody.localdb.Estructura
import com.juandeherrera.letskody.metodosAuxiliares.componentes.BarraNavegacionInferior
import com.juandeherrera.letskody.metodosAuxiliares.componentes.BarraSuperior
import com.juandeherrera.letskody.metodosAuxiliares.componentes.ImagenKodyFlotando
import com.juandeherrera.letskody.metodosAuxiliares.componentes.MensajeSnackbarHost
import com.juandeherrera.letskody.metodosAuxiliares.componentes.MenuLateralInicio
import com.juandeherrera.letskody.metodosAuxiliares.componentes.notificationSnackbar
import com.juandeherrera.letskody.metodosAuxiliares.interfaz.FondoDinamico
import com.juandeherrera.letskody.metodosAuxiliares.interfaz.MensajeBienvenida
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.cerrarSesionUsuario
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.obtenerMomentoDelDia
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.refrescarBaseDatos
import com.juandeherrera.letskody.navigation.AppScreens
import kotlinx.coroutines.launch

@SuppressLint("DefaultLocale")
@RequiresApi(value = Build.VERSION_CODES.TIRAMISU) // solo se permite Android 13 o superior (API 33+)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PantallaInicio(controladorNavegacion: NavController) {
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

    var refrescarPantalla by remember { mutableStateOf(value = false) } // variable de estado para refrescar

    val estadoRefrescoPantalla = rememberPullToRefreshState()  // variable para el estado del refresco de pantalla

    val momentoDelDia = remember { obtenerMomentoDelDia() }  // variable que obtiene el momento del día que es según la hora del dispositivo

    // animación de flotación de Kody (sube 16dp y baja en 2 segundos de manera indefinida)
    val transicionMascota = rememberInfiniteTransition(label = "mascota")
    val flotacionMascota by transicionMascota.animateFloat(
        initialValue = 0f, targetValue = -16f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "flotacion"
    )

    // MENU LATERAL DE NAVEGACIÓN
    ModalNavigationDrawer(
        drawerState = abrirMenuLateral,  // controla el estado del menu lateral de navegación
        // el contenido que aparece dentro del menu lateral
        drawerContent = {
            MenuLateralInicio(
                estadoMenuLateral = abrirMenuLateral,
                titulo = "Inicio",
                selectInicio = true,
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
                    titulo = "Inicio",
                    fuenteTipografica = badcomic,
                    estadoMenuDesplegable = abrirToolbar,
                    abrirMenuLateral = {
                        scope.launch { abrirMenuLateral.open() }
                    },
                    abrirMenuDesplegable = { abrirToolbar = true },
                    cerrarMenuDesplegable = { abrirToolbar = false },
                    cerrarSesionUsuario = {
                        cerrarSesionUsuario(db = db, usuario = usuario!!)  // se cierra la sesión de Firebase y se borran los datos locales
                        controladorNavegacion.navigate(route = AppScreens.Login.route) { popUpTo(id = 0) {inclusive = true} }  // se vuelve a la pantalla de login (se limpia el historial de navegación)
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

            // contenedor que permite hacer el gesto de arrastrar hacia abajo para refrescar
            PullToRefreshBox(
                state = estadoRefrescoPantalla,    // estado de refresco de la pantalla
                isRefreshing = refrescarPantalla,  // indica si se muestra el indicador de carga
                // función que se ejecuta cuando el usuario refresca
                onRefresh = {
                    refrescarPantalla = true  // se activa el indicador de carga

                    // se lanza la corrutina para ejecutar la sincronización
                    scope.launch {
                        // se sincronizan los datos de Firebase con los locales
                        refrescarBaseDatos(
                            uidUsuario = usuario!!.uidUsuario,
                            db = db,
                            error = { mensaje ->
                                notificationSnackbar(scope = scope, snackbarHostState = snackbarHostState, mensaje = mensaje)
                            }
                        )

                        refrescarPantalla = false  // se oculta el indicador tras finalizar la sincronización
                    }
                },
                // indicador de refrescar pantalla
                indicator = {
                    Indicator(
                        modifier = Modifier.align(Alignment.TopCenter), // centrado arriba
                        isRefreshing = refrescarPantalla,
                        containerColor = Color(0xFF9CC6FF), // color de fondo del circulo
                        color = Color(0xFF2364C9),          // color del icono girando
                        state = estadoRefrescoPantalla      // estado de refresco de la pantalla
                    )
                },
                modifier = Modifier.fillMaxSize()          // se ocupa la pantalla completa
                    .padding(paddingValues = innerPadding) // padding por defecto
            ){

                // contenedor principal
                Box(
                    modifier = Modifier.fillMaxSize()  // se ocupa el espacio disponible

                ){
                    // capa del fondo animado
                    FondoDinamico(momento = momentoDelDia)

                    // capa con la imagen de Kody flotando y el saludo al usuario
                    Column(
                        modifier = Modifier.fillMaxSize()   // se ocupa el espacio disponible
                            .padding(horizontal = 24.dp)    // padding en los laterales horizontales
                        .verticalScroll(rememberScrollState()),       // scroll vertical
                        horizontalAlignment = Alignment.CenterHorizontally,  // centrado horizontal
                        verticalArrangement = Arrangement.Center             // centrado vertical
                    ){
                        // imagen
                        ImagenKodyFlotando(flotacionVertical = flotacionMascota)

                        // saludo de bienvenida
                        MensajeBienvenida(momento = momentoDelDia, nombreUsuario = usuario!!.nombreUsuario, fuenteTipografica = badcomic)
                    }
                }
            }
        }
    }
}