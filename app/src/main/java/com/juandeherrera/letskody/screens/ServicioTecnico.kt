package com.juandeherrera.letskody.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
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
import com.juandeherrera.letskody.metodosAuxiliares.componentes.MensajeSnackbarHost
import com.juandeherrera.letskody.metodosAuxiliares.componentes.MenuLateralInicio
import com.juandeherrera.letskody.metodosAuxiliares.componentes.notificationSnackbar
import com.juandeherrera.letskody.metodosAuxiliares.interfaz.mensajeTiempoRestante
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.cerrarSesionUsuario
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.registrarTicketServicioTecnico
import com.juandeherrera.letskody.navigation.AppScreens
import com.juandeherrera.letskody.notification.NotificationHandler
import kotlinx.coroutines.launch

@SuppressLint("DefaultLocale")
@RequiresApi(value = Build.VERSION_CODES.TIRAMISU) // solo se permite Android 13 o superior (API 33+)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PantallaServicioTecnico(controladorNavegacion: NavController) {
    val badcomic = FontFamily(Font(R.font.badcomic))  // fuente tipográfica por defecto

    val scope = rememberCoroutineScope() // variable que crea un ámbito de corrutinas que se mantienen en la recomposición de la interfaz

    val snackbarHostState = remember { SnackbarHostState() } // variable de estado que controla el estado (mostrar/ocultar) del Snackbar

    val tipoSnackbar = remember { mutableStateOf(value = "error") }  // variable de estado para indicar el tipo de snackbar a mostrar por defecto

    val context = LocalContext.current // variable que obtiene el contexto actual

    val notificationHandler = NotificationHandler(context) // variable que se encarga de mostrar las notificaciones

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

    // variable para los datos del formulario
    var asunto by remember { mutableStateOf(value = "") }       // asunto del problema
    var descripcion by remember { mutableStateOf(value = "") }  // descripción del problema

    val tiempoEspera = 5 * 60 * 1000L  // tiempo mínimo de espera entre envíos (en milisegundos)

    // MENU LATERAL DE NAVEGACIÓN
    ModalNavigationDrawer(
        drawerState = abrirMenuLateral,  // controla el estado del menu lateral de navegación
        // el contenido que aparece dentro del menu lateral
        drawerContent = {
            MenuLateralInicio(
                estadoMenuLateral = abrirMenuLateral,
                titulo = "Servicio técnico",
                selectInicio = false,
                selectServicioTecnico = true,
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
                    titulo = "Servicio técnico",
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
                modifier = Modifier.fillMaxSize()       // se ocupa la pantalla completa
                    .verticalScroll(state = rememberScrollState())   // scroll vertical
                    .background(Color(0xFFC2DAFD))                   // color de fondo
                    .padding(paddingValues = innerPadding), // padding por defecto
                horizontalAlignment = Alignment.CenterHorizontally,  // centrado horizontal
                verticalArrangement = Arrangement.Center             // centrado vertical
            ){
                // tarjeta con la información del servicio técnico
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

                        // IMAGEN
                        Image(
                            painter = painterResource(id = R.drawable.kody_serviciotecnico),  // ruta al recurso (imagen)
                            contentDescription = "Kody de soporte técnico",                   // texto descriptivo de la imagen (TalkBack)
                            contentScale = ContentScale.Fit,                                  // forma de escalar la imagen
                            modifier = Modifier.width(160.dp).height(130.dp)                  // dimensiones de la imagen
                        )

                        // TITULO
                        Text(
                            text = "¿Tienes algún problema?", // texto
                            color = Color(0xFF2363C8),        // color del texto
                            style = TextStyle(
                                fontFamily = badcomic,        // fuente tipográfica del texto
                                fontSize = 24.sp,             // tamaño del texto
                                fontWeight = FontWeight.Bold, // texto en negrita
                                textAlign = TextAlign.Center  // texto alineado centralmente
                            )
                        )

                        // TITULO
                        Text(
                            text = "Cuéntale a Kody que ha ocurrido e intentará ayudarte lo antes posible. Rellena el formulario con los datos del problema.", // texto
                            color = Color.Black,                // color del texto
                            style = TextStyle(
                                fontFamily = badcomic,          // fuente tipográfica del texto
                                fontSize = 14.sp,               // tamaño del texto
                                textAlign = TextAlign.Justify,  // texto alineado centralmente
                                lineHeight = 20.sp              // espaciado vertical entre líneas
                            )
                        )

                        // se pide el asunto del ticket
                        OutlinedTextField(
                            value = asunto,  // valor del campo de texto
                            onValueChange = { if (it.length < 41){ asunto = it } }, // se limita la longitud a 40 caracteres
                            label = {
                                Text(
                                    text = "Asunto",        // texto
                                    color = Color.Black,    // color del texto
                                    fontFamily = badcomic   // fuente tipográfica del texto
                                )
                            },
                            singleLine = true,   // el campo de texto solo puede tener una sola línea de texto (con TAB se pasa al siguiente campo)
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color(0xFF017DB2),   // borde del campo cuando está activo
                                unfocusedIndicatorColor = Color(0xFF017DB2), // borde del campo cuando no está activo
                                focusedContainerColor = Color.White,         // color del fondo del campo cuando está activo
                                unfocusedContainerColor = Color.White,       // color del fondo del campo cuando no está activo
                                cursorColor = Color(0xFF017DB2)              // color del cursor en el campo de texto
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,             // tipo de teclado para el campo de texto
                                capitalization = KeyboardCapitalization.None, // no se capitaliza (no se trata las mayúsculas) el texto del usuario
                                autoCorrectEnabled = true,                    // se habilita el autocorrector mientras escribe el usuario
                                imeAction = ImeAction.Next,                   // se habilita la acción de ir al siguiente campo de texto desde el teclado
                                showKeyboardOnFocus = true                    // se muestra el teclado cuando el campo recibe el foco
                            ),
                            modifier = Modifier.fillMaxWidth()  // se ocupa el maximo ancho disponible
                        )

                        // se pide la descripción del ticket
                        OutlinedTextField(
                            value = descripcion,  // valor del campo de texto
                            onValueChange = { if (it.length < 301){ descripcion = it } }, // se limita la longitud a 300 caracteres
                            label = {
                                Text(
                                    text = "Descripción del problema",   // texto
                                    color = Color.Black,                 // color del texto
                                    fontFamily = badcomic                // fuente tipográfica del texto
                                )
                            },
                            minLines = 4,  // número mínimo de líneas que tiene el campo de texto
                            maxLines = 4,  // número máximo de líneas que tiene el campo de texto
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color(0xFF017DB2),   // borde del campo cuando está activo
                                unfocusedIndicatorColor = Color(0xFF017DB2), // borde del campo cuando no está activo
                                focusedContainerColor = Color.White,         // color del fondo del campo cuando está activo
                                unfocusedContainerColor = Color.White,       // color del fondo del campo cuando no está activo
                                cursorColor = Color(0xFF017DB2)              // color del cursor en el campo de texto
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,             // tipo de teclado para el campo de texto
                                capitalization = KeyboardCapitalization.None, // no se capitaliza (no se trata las mayúsculas) el texto del usuario
                                autoCorrectEnabled = true,                    // se habilita el autocorrector mientras escribe el usuario
                                imeAction = ImeAction.Previous,               // se habilita la acción de ir al anterior campo de texto desde el teclado
                                showKeyboardOnFocus = true                    // se muestra el teclado cuando el campo recibe el foco
                            ),
                            modifier = Modifier.fillMaxWidth()  // se ocupa el maximo ancho disponible
                        )

                        // BOTÓN DE ENVIAR TICKET
                        Button(
                            onClick = {
                                val ahora = System.currentTimeMillis()        // tiempo actual en milisegundos

                                val ultimoEnvio = usuario!!.ultimoEnvioTicket  // tiempo en el que se hizo el último envío (milisegundos)

                                val tiempoRestante = tiempoEspera - (ahora - ultimoEnvio)  // tiempo restante para el último envío (milisegundos)

                                when{
                                    // validaciones de los campos del formulario
                                    asunto.isBlank() || descripcion.isBlank() -> {
                                        notificationSnackbar(scope = scope, snackbarHostState = snackbarHostState, mensaje = "Los campos de texto no pueden estar vacíos.")
                                    }
                                    // se comprueba si hay tiempo de espera
                                    ultimoEnvio != 0L && tiempoRestante > 0 -> {
                                        notificationSnackbar(scope = scope, snackbarHostState = snackbarHostState, mensaje = mensajeTiempoRestante(msRestantes = tiempoRestante))
                                    }
                                    else -> {
                                        // se registra el mensaje de incidencia del usuario en Firebase
                                        registrarTicketServicioTecnico(
                                            uidUsuario = usuario!!.uidUsuario,
                                            db = db,
                                            asunto = asunto,
                                            descripcion = descripcion,
                                            exito = {
                                                // se limpia los campos de texto del formulario
                                                asunto = ""
                                                descripcion = ""

                                                notificationHandler.notificacionCreacionIncidencia()  // notificación de registro de incidencia
                                            },
                                            error = { mensaje ->
                                                notificationSnackbar(scope = scope, snackbarHostState = snackbarHostState, mensaje = mensaje)
                                            }
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth() // se ocupa el maximo ancho disponible
                                .height(50.dp),                // altura del botón
                            shape = RoundedCornerShape(size = 12.dp),  // bordes redondeados
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF017DB2),  // color de fondo del botón
                                contentColor = Color.White           // color del texto del botón
                            )
                        ){
                            Text(
                                text = "Enviar",
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
}