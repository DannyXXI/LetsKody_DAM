package com.juandeherrera.letskody.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import com.juandeherrera.letskody.metodosAuxiliares.componentes.BarraSuperior
import com.juandeherrera.letskody.metodosAuxiliares.componentes.ImagenUsuario
import com.juandeherrera.letskody.metodosAuxiliares.componentes.MensajeSnackbarHost
import com.juandeherrera.letskody.metodosAuxiliares.componentes.MenuLateralPerfil
import com.juandeherrera.letskody.metodosAuxiliares.componentes.ModalEliminarCuenta
import com.juandeherrera.letskody.metodosAuxiliares.componentes.notificationSnackbar
import com.juandeherrera.letskody.metodosAuxiliares.interfaz.colorFondo
import com.juandeherrera.letskody.metodosAuxiliares.interfaz.colorTexto
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.calcularEdad
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.cerrarSesionUsuario
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.eliminarCuentaUsuario
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.refrescarBaseDatos
import com.juandeherrera.letskody.navigation.AppScreens
import kotlinx.coroutines.launch

@SuppressLint("DefaultLocale")
@RequiresApi(value = Build.VERSION_CODES.TIRAMISU) // solo se permite Android 13 o superior (API 33+)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PantallaPerfil(controladorNavegacion: NavController) {
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

    val abrirModalEliminarCuenta = remember { mutableStateOf(value = false) } // variable para el estado (abrir/cerrar) del modal de eliminar cuenta

    val passwordVerificacion = rememberTextFieldState()  // contraseña de verificación para eliminar la cuenta del usuario

    var passVisibleVerificacion by remember { mutableStateOf(value = false) }  // variable de estado (mostrar/ocultar) la contraseña de verificación

    // MENU LATERAL DE NAVEGACIÓN
    ModalNavigationDrawer(
        drawerState = abrirMenuLateral,  // controla el estado del menu lateral de navegación
        // el contenido que aparece dentro del menu lateral
        drawerContent = {
            MenuLateralPerfil(
                estadoMenuLateral = abrirMenuLateral,
                titulo = "Mi perfil",
                selectPerfil = true,
                selectEditarPerfil = false,
                scope = scope,
                controladorNavegacion = controladorNavegacion,
                fuenteTipografica = badcomic,
                mostrarModalEliminarCuenta = abrirModalEliminarCuenta
            )
        }
    ){
        Scaffold(
            // BARRA SUPERIOR
            topBar = {
                BarraSuperior(
                    titulo = "Mi perfil",
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
                    selectInicio = false,
                    selectMaterias = false,
                    selectPerfil = true
                )
            },
            // define el lugar donde se mostraran los Snackbar
            snackbarHost = {
                MensajeSnackbarHost(snackbarHostState = snackbarHostState, fuenteTipografica = badcomic, tipo = tipoSnackbar.value)
            }
        ){
            innerPadding ->

            // si el estado del modal de eliminar cuenta es abierto
            if (abrirModalEliminarCuenta.value) {
                // se muestra el modal
                ModalEliminarCuenta(
                    context = context,
                    fuenteTipografica = badcomic,
                    password = passwordVerificacion,
                    passVisible = passVisibleVerificacion,
                    mostrarPassword = { passVisibleVerificacion = !passVisibleVerificacion },
                    cerrar = {
                        abrirModalEliminarCuenta.value = false
                        passwordVerificacion.clearText()
                        passVisibleVerificacion = false
                    },
                    enviar = {
                        eliminarCuentaUsuario(
                            usuario = usuario!!,
                            password = passwordVerificacion.text.toString(),
                            db = db,
                            controladorNavegacion = controladorNavegacion,
                            context = context,
                            scope = scope,
                            error = { mensaje ->
                                notificationSnackbar(scope = scope, snackbarHostState = snackbarHostState, mensaje = mensaje)
                            }
                        )
                    }
                )
            }

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
                Column(
                    modifier = Modifier.fillMaxSize()       // se ocupa la pantalla completa
                        .verticalScroll(state = rememberScrollState())  // scroll vertical
                        .background(Color(0xFFC2DAFD)),     // color de fondo
                    horizontalAlignment = Alignment.CenterHorizontally,  // centrado horizontal
                    verticalArrangement = Arrangement.Center             // centrado vertical
                ){
                    // TARJETA CON LA INFORMACIÓN DEL USUARIO
                    ElevatedCard(
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),  // sombreado de la tarjeta
                        colors = CardDefaults.cardColors(containerColor = Color.White),   // color de fondo de la tarjeta
                        modifier = Modifier.fillMaxWidth()    // ocupa el maximo ancho disponible
                            .padding(horizontal = 24.dp)      // padding en los laterales
                    ){
                        // columna con el contenido de la tarjeta de información
                        Column(
                            modifier = Modifier.fillMaxWidth()    // ocupa el maximo ancho disponible
                                .padding(all = 24.dp),            // padding externo
                            horizontalAlignment = Alignment.CenterHorizontally,  // centrado horizontal
                            verticalArrangement = Arrangement.spacedBy(16.dp)    // espacio vertical entre elementos
                        ){

                            ImagenUsuario(usuario = usuario!!)  // se carga la imagen del usuario

                            // nombre completo del usuario
                            Text(
                                text = "${usuario!!.nombreUsuario} ${usuario!!.apellidosUsuario}",  // texto
                                color = colorTexto(genero = usuario!!.sexoUsuario),   // color del texto
                                style = TextStyle(
                                    fontFamily = badcomic,   // fuente tipográfica del texto
                                    fontSize = 26.sp,        // tamaño del texto
                                    fontWeight = FontWeight.Bold,  // texto en negrita
                                    textAlign = TextAlign.Center   // se centra el texto
                                )
                            )

                            // contenedor del email del usuario
                            ElevatedCard(
                                colors = CardDefaults.cardColors(containerColor = colorFondo(genero = usuario!!.sexoUsuario)),   // color de fondo de la tarjeta
                                modifier = Modifier.fillMaxWidth(),    // ocupa el maximo ancho disponible
                            ){
                                // fila para el contenido del contenedor
                                Row(
                                    modifier = Modifier.fillMaxWidth()    // ocupa el maximo ancho disponible
                                        .padding(all = 8.dp),             // padding interno
                                    verticalAlignment = Alignment.CenterVertically  // centrado vertical
                                ){
                                    // label
                                    Text(
                                        text = "Email:",  // texto
                                        color = colorTexto(genero = usuario!!.sexoUsuario),   // color del texto
                                        style = TextStyle(
                                            fontFamily = badcomic,   // fuente tipográfica del texto
                                            fontSize = 16.sp,        // tamaño del texto
                                            fontWeight = FontWeight.Bold,  // texto en negrita
                                        )
                                    )

                                    // email del usuario
                                    Text(
                                        text = usuario!!.emailUsuario,  // texto
                                        color = Color.Black,            // color del texto
                                        style = TextStyle(
                                            fontFamily = badcomic,       // fuente tipográfica del texto
                                            fontSize = 16.sp,            // tamaño del texto
                                            textAlign = TextAlign.Center // texto alineado en el centro
                                        ),
                                        modifier = Modifier.weight(1f)  // se ocupa el resto del espacio disponible
                                    )
                                }
                            }

                            // contenedor del teléfono del usuario
                            ElevatedCard(
                                colors = CardDefaults.cardColors(containerColor = colorFondo(genero = usuario!!.sexoUsuario)),   // color de fondo de la tarjeta
                                modifier = Modifier.fillMaxWidth(),    // ocupa el maximo ancho disponible
                            ){
                                // fila para el contenido del contenedor
                                Row(
                                    modifier = Modifier.fillMaxWidth()    // ocupa el maximo ancho disponible
                                        .padding(all = 8.dp),             // padding interno
                                    verticalAlignment = Alignment.CenterVertically  // centrado vertical
                                ){
                                    // label
                                    Text(
                                        text = "Teléfono:",  // texto
                                        color = colorTexto(genero = usuario!!.sexoUsuario),   // color del texto
                                        style = TextStyle(
                                            fontFamily = badcomic,   // fuente tipográfica del texto
                                            fontSize = 16.sp,        // tamaño del texto
                                            fontWeight = FontWeight.Bold,  // texto en negrita
                                        )
                                    )

                                    // email del usuario
                                    Text(
                                        text = usuario!!.telefonoUsuario,  // texto
                                        color = Color.Black,            // color del texto
                                        style = TextStyle(
                                            fontFamily = badcomic,       // fuente tipográfica del texto
                                            fontSize = 16.sp,            // tamaño del texto
                                            textAlign = TextAlign.Center // texto alineado en el centro
                                        ),
                                        modifier = Modifier.weight(1f)  // se ocupa el resto del espacio disponible
                                    )
                                }
                            }

                            // contenedor de la edad del usuario
                            ElevatedCard(
                                colors = CardDefaults.cardColors(containerColor = colorFondo(genero = usuario!!.sexoUsuario)),   // color de fondo de la tarjeta
                                modifier = Modifier.fillMaxWidth(),    // ocupa el maximo ancho disponible
                            ){
                                // fila para el contenido del contenedor
                                Row(
                                    modifier = Modifier.fillMaxWidth()    // ocupa el maximo ancho disponible
                                        .padding(all = 8.dp),             // padding interno
                                    verticalAlignment = Alignment.CenterVertically  // centrado vertical
                                ){
                                    // label
                                    Text(
                                        text = "Edad:",  // texto
                                        color = colorTexto(genero = usuario!!.sexoUsuario),   // color del texto
                                        style = TextStyle(
                                            fontFamily = badcomic,   // fuente tipográfica del texto
                                            fontSize = 16.sp,        // tamaño del texto
                                            fontWeight = FontWeight.Bold,  // texto en negrita
                                        )
                                    )

                                    // edad del usuario
                                    Text(
                                        text = "${calcularEdad(fechaNacimiento = usuario!!.fnacUsuario)} años",  // texto
                                        color = Color.Black,            // color del texto
                                        style = TextStyle(
                                            fontFamily = badcomic,       // fuente tipográfica del texto
                                            fontSize = 16.sp,            // tamaño del texto
                                            textAlign = TextAlign.Center // texto alineado en el centro
                                        ),
                                        modifier = Modifier.weight(1f)  // se ocupa el resto del espacio disponible
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}