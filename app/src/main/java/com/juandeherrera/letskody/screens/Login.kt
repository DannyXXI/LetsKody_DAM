package com.juandeherrera.letskody.screens

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.juandeherrera.letskody.R
import com.juandeherrera.letskody.metodosAuxiliares.componentes.MensajeSnackbarHost
import com.juandeherrera.letskody.metodosAuxiliares.componentes.ModalModificarPassword
import com.juandeherrera.letskody.metodosAuxiliares.componentes.notificationSnackbar
import com.juandeherrera.letskody.metodosAuxiliares.interfaz.fondoDegradadoDiagonal
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.loguearUsuario
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.recuperarPasswordUsuario
import com.juandeherrera.letskody.navigation.AppScreens

@SuppressLint("DefaultLocale")
@RequiresApi(value = Build.VERSION_CODES.TIRAMISU) // solo se permite Android 13 o superior (API 33+)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PantallaLogin(controladorNavegacion: NavController) {
    val badcomic = FontFamily(Font(R.font.badcomic))  // fuente tipográfica por defecto

    val degradadoDiagonal = fondoDegradadoDiagonal(color1 = Color(0xFF0D47A1), color2 = Color(0xFF1976D2), color3 = Color(0xFF42A5F5))  // variable para obtener el degradado

    val scope = rememberCoroutineScope() // variable que crea un ámbito de corrutinas que se mantienen en la recomposición de la interfaz

    val snackbarHostState = remember { SnackbarHostState() } // variable de estado que controla el estado (mostrar/ocultar) del Snackbar

    val tipoSnackbar = remember { mutableStateOf(value = "error") }  // variable de estado para indicar el tipo de snackbar a mostrar por defecto

    val permisosNotificacion = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS) // controlador de permisos de notificaciones

    val context = LocalContext.current // variable que obtiene el contexto actual

    // variables para los datos del formulario
    var email by remember { mutableStateOf(value = "") }           // correo electrónico
    val password = rememberTextFieldState()                        // contraseña
    var passVisible by remember { mutableStateOf(value = false) }  // variable para mostrar la contraseña

    val emailPattern = Regex(pattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}") // patron que debe cumplir el email

    var emailRecuperacion by remember { mutableStateOf(value = "") } // variable de estado para el email del usuario para la recuperación de su contraseña

    val abrirModalPassword = remember { mutableStateOf(value = false) } // variable para el estado (abrir/cerrar) del modal de recuperación de contraseña

    Scaffold(
        // define el lugar donde se mostraran los Snackbar
        snackbarHost = {
            MensajeSnackbarHost(snackbarHostState = snackbarHostState, fuenteTipografica = badcomic, tipo = tipoSnackbar.value)
        }
    ){
        innerPadding ->

        // bloque de código que se ejecuta una sola vez al iniciar la aplicación por primera vez
        LaunchedEffect(key1 = true) {
            // cuando cargue la pantalla se pide permiso de notificaciones si no se dio antes (solo la primera vez)
            if (!permisosNotificacion.status.isGranted) {
                permisosNotificacion.launchPermissionRequest()  // popup para que el usuario conceda o no los permisos de notificaciones a la aplicación
            }
        }

        // si el estado del modal de recuperación de contraseña es abierto
        if (abrirModalPassword.value) {
            // se muestra el modal
            ModalModificarPassword(
                context = context,
                fuenteTipografica = badcomic,
                email = emailRecuperacion,
                detectorEmail = { emailRecuperacion = it },
                cerrar = { abrirModalPassword.value = false },
                enviar = {
                    // se procesa el envío del enlace por Firebase
                    recuperarPasswordUsuario(
                        emailRecuperacion = emailRecuperacion,
                        exito = {
                            emailRecuperacion = ""  // se limpia la variable del email de recuperación

                            abrirModalPassword.value = false  // se cierra el modal

                            tipoSnackbar.value = "success" // se cambia el tipo de snackbar

                            notificationSnackbar(scope = scope, snackbarHostState = snackbarHostState, mensaje = "Email de modificación enviado.")  // mensaje al usuario
                        },
                        error = { mensaje ->
                            Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
                        }
                    )
                }
            )
        }

        Column(
            modifier = Modifier.fillMaxSize()               // ocupa el máximo espacio disponible
                .background(brush = degradadoDiagonal)      // fondo con degradado animado
                .padding(paddingValues = innerPadding),     // se usa el padding por defecto
            horizontalAlignment = Alignment.CenterHorizontally,   // centrado horizontal
            verticalArrangement = Arrangement.Center              // centrado vertical
        ){
            // tarjeta elevada donde se mostrara el formulario de login
            ElevatedCard(
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp), // sombreado de la tarjeta
                colors = CardDefaults.cardColors(containerColor = Color.White),  // color de fondo de la tarjeta
                modifier = Modifier.fillMaxWidth()        // ocupa el maximo ancho posible
                    .padding(start = 30.dp, end = 30.dp)  // padding en los laterales
            ){
                // columna que contiene el formulario
                Column(
                    modifier = Modifier.fillMaxWidth() // ocupa el ancho maximo posible
                        .padding(16.dp),          // padding interior
                    horizontalAlignment = Alignment.CenterHorizontally,   // centrado horizontal
                    verticalArrangement = Arrangement.Center              // centrado vertical
                ){
                    // IMAGEN DEL TÍTULO DE LA APLICACIÓN
                    Image(
                        painter = painterResource(id = R.drawable.titulo),   // ruta al recurso (imagen)
                        contentDescription = "Lets Kody",                    // texto descriptivo de la imagen (TalkBack)
                        contentScale = ContentScale.Fit                      // forma de escalar la imagen
                    )

                    Spacer(modifier = Modifier.height(10.dp))  // separación vertical entre componentes

                    // TITULO
                    Text(
                        text = "Bienvenid@",         // texto
                        color = Color(0xFF017DB2),   // color del texto
                        style = TextStyle(
                            fontFamily = badcomic,   // fuente tipográfica del texto
                            fontSize = 45.sp         // tamaño del texto
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))  // separación vertical entre componentes

                    // se pide el email del usuario
                    OutlinedTextField(
                        value = email,  // valor del campo de texto
                        onValueChange = { if (it.length < 40){ email = it } },  // se limita la longitud a 40 caracteres
                        label = {
                            Text(
                                text = "Email del usuario",  // texto
                                color = Color.Black,         // color del texto
                                fontFamily = badcomic        // fuente tipográfica del texto
                            )
                        },
                        modifier = Modifier.width(310.dp),  // ancho del campo de texto
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color(0xFF017DB2),   // borde del campo cuando está activo
                            unfocusedIndicatorColor = Color(0xFF017DB2), // borde del campo cuando no está activo
                            focusedContainerColor = Color.White,         // color del fondo del campo cuando está activo
                            unfocusedContainerColor = Color.White,       // color del fondo del campo cuando no está activo
                            cursorColor = Color(0xFF017DB2)              // color del cursor en el campo de texto
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,            // tipo de teclado para el campo de texto
                            capitalization = KeyboardCapitalization.None, // no se capitaliza (no se trata las mayúsculas) el texto del usuario
                            autoCorrectEnabled = true,                    // se habilita el autocorrector mientras escribe el usuario
                            imeAction = ImeAction.Next,                   // se habilita la acción de ir al siguiente campo de texto desde el teclado
                            showKeyboardOnFocus = true                    // se muestra el teclado cuando el campo recibe el foco
                        ),
                        textStyle = TextStyle(
                            color = Color.Black,      // color del texto introducido
                            fontFamily = badcomic     // fuente tipográfica del texto introducida
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))  // separación vertical entre componentes

                    // se pide la contraseña del usuario
                    OutlinedSecureTextField(
                        state = password,  // estado que contiene el texto introducido (la contraseña)
                        label = {
                            Text(
                                text = "Contraseña",   // texto
                                color = Color.Black,   // color del texto
                                fontFamily = badcomic  // fuente tipográfica del texto
                            )
                        },
                        modifier = Modifier.width(310.dp),  // ancho del campo de texto
                        // icono situado al final del campo de texto
                        trailingIcon = {
                            IconButton(
                                onClick = { passVisible = !passVisible }  // al pulsar el icono cambia el estado para mostrar/ocultar la contraseña
                            ){
                                Icon(
                                    // se cambia el icono si la contraseña es visible o no
                                    imageVector = if (passVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,

                                    // se cambia la descripción para lectores de pantalla en función si la contraseña es visible o no
                                    contentDescription = if (passVisible) "Ocultar contraseña" else "Mostrar contraseña",

                                    tint = Color.Black // color del icono
                                )
                            }
                        },
                        // controla como se oculta el texto (lo hace visible completamente o solo muestra el último carácter)
                        textObfuscationMode = if (passVisible) TextObfuscationMode.Visible else TextObfuscationMode.RevealLastTyped,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color(0xFF017DB2),   // borde del campo cuando está activo
                            unfocusedIndicatorColor = Color(0xFF017DB2), // borde del campo cuando no está activo
                            focusedContainerColor = Color.White,         // color del fondo del campo cuando está activo
                            unfocusedContainerColor = Color.White,       // color del fondo del campo cuando no está activo
                            cursorColor = Color(0xFF017DB2)              // color del cursor en el campo de texto
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,         // tipo de teclado para el campo de texto
                            capitalization = KeyboardCapitalization.None, // no se capitaliza (no se trata las mayúsculas) el texto del usuario
                            autoCorrectEnabled = false,                   // se inhabilita el autocorrector mientras escribe el usuario
                            imeAction = ImeAction.Send,                   // se habilita la acción de iniciar sesión desde el teclado (enviar formulario)
                            showKeyboardOnFocus = true                    // se muestra el teclado cuando el campo recibe el foco
                        ),
                        onKeyboardAction = {
                            // inicia sesión el usuario
                            loguearUsuario(
                                controladorNavegacion = controladorNavegacion,
                                context = context,
                                scope = scope,
                                snackbarHostState = snackbarHostState,
                                email = email,
                                password = password.text.toString()
                            )
                        },
                        textStyle = TextStyle(
                            color = Color.Black,        // color del texto introducido
                            fontFamily = badcomic       // fuente tipográfica del texto introducida
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))  // separación vertical entre componentes

                    // fila para la opción de recuperación de contraseña
                    Row {
                        // texto normal
                        Text(
                            text = "¿Olvidaste la contraseña? ",  // texto
                            color = Color.Black,                  // color del texto
                            style = TextStyle(
                                fontFamily = badcomic,  // fuente tipográfica del texto
                                fontSize = 16.sp        // tamaño del texto
                            )
                        )

                        // texto seleccionable que muestra el modal para la recuperación de la contraseña
                        Text(
                            text = "Pulsa aquí",        // texto
                            color = Color(0xFF017DB2),  // color del texto
                            style = TextStyle(
                                fontFamily = badcomic,         // fuente tipográfica del texto
                                fontSize = 16.sp,              // tamaño del texto
                                fontWeight = FontWeight.Bold   // texto en negrita
                            ),
                            modifier = Modifier.clickable { abrirModalPassword.value = true } // al pulsar se muestra el modal de recuperación de contraseña
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))  // separación vertical entre componentes

                    // BOTÓN DE INICIO DE SESIÓN
                    Button(
                        onClick = {
                            // validaciones de los campos del formulario
                            when{
                                email.isBlank() -> {
                                    notificationSnackbar(scope = scope, snackbarHostState = snackbarHostState, mensaje = "El email no puede estar vacío.")
                                }
                                !email.matches(regex = emailPattern) -> {
                                    notificationSnackbar(scope = scope, snackbarHostState = snackbarHostState, mensaje = "El email no tiene un formato válido.")
                                }
                                password.text.length < 8 -> {
                                    notificationSnackbar(scope = scope, snackbarHostState = snackbarHostState, mensaje = "La contraseña debe tener 8 caracteres.")
                                }
                                else -> {
                                    // inicia sesión el usuario
                                    loguearUsuario(
                                        controladorNavegacion = controladorNavegacion,
                                        context = context,
                                        scope = scope,
                                        snackbarHostState = snackbarHostState,
                                        email = email,
                                        password = password.text.toString()
                                    )
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF017DB2),  // color de fondo del botón
                            contentColor = Color.White           // color del texto del botón
                        )
                    ){
                        Text(
                            text = "Iniciar sesión",    // texto del botón
                            style = TextStyle(
                                fontFamily = badcomic,  // fuente tipográfica del texto
                                fontSize = 18.sp        // tamaño del texto
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))  // separación vertical entre componentes

                    // fila para el registro de un nuevo usuario
                    Row {
                        // texto normal
                        Text(
                            text = "¿No tienes cuenta? ",  // texto
                            color = Color.Black,           // color del texto
                            style = TextStyle(
                                fontFamily = badcomic,     // fuente tipográfica del texto
                                fontSize = 16.sp           // tamaño del texto
                            )
                        )

                        // texto seleccionable que te navega al formulario de registro
                        Text(
                            text = "Regístrate",        // texto
                            color = Color(0xFF017DB2),  // color del texto
                            style = TextStyle(
                                fontFamily = badcomic,           // fuente tipográfica del texto
                                fontSize = 16.sp,                // tamaño del texto
                                fontWeight = FontWeight.Bold     // texto en negrita
                            ),
                            modifier = Modifier.clickable {
                                controladorNavegacion.navigate(AppScreens.CrearUsuario.route) // al pulsar se navega al formulario de crear usuario
                            }
                        )
                    }
                }
            }
        }
    }
}