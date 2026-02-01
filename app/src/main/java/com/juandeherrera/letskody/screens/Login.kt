package com.juandeherrera.letskody.screens

import android.annotation.SuppressLint
import android.os.Build
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import com.juandeherrera.letskody.R
import com.juandeherrera.letskody.metodosAuxiliares.fondoDegradadoDiagonal

@SuppressLint("DefaultLocale")
@RequiresApi(Build.VERSION_CODES.TIRAMISU) // solo se permite Android 13 o superior (API 33+)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PantallaLogin(controladorNavegacion: NavController) {
    val badcomic = FontFamily(Font(R.font.badcomic))  // fuente tipografica por defecto

    val degradadoDiagonal = fondoDegradadoDiagonal(Color(0xFF0D47A1), Color(0xFF1976D2), Color(0xFF42A5F5))  // variable para obtener el degradado

    val scope = rememberCoroutineScope() // variable que crea un ambito de corrutinas que se mantienen en la recomposicion de la interfaz

    val snackbarHostState = remember { SnackbarHostState() } // variable de estado que controla el estado (mostrar/ocultar) del Snackbar

    val context = LocalContext.current // variable que obtiene el contexto actual

    // variables para los datos del formulario
    var email by remember { mutableStateOf("") }
    val password = rememberTextFieldState()
    var passVisible by remember { mutableStateOf(false) }

    Scaffold(
        // define el lugar donde se mostraran los Snackbar
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)  // componente que muestra el Snackbar en pantalla
        }
    ){
        innerPadding ->

        Column(
            modifier = Modifier.fillMaxSize()             // ocupa el espacio disponible
                .padding(innerPadding)     // usa el padding por defecto
                .background(degradadoDiagonal),   // fondo con degradado animado
            horizontalAlignment = Alignment.CenterHorizontally,   // centrado horizontal
            verticalArrangement = Arrangement.Center              // centrado vertical
        ){
            // tarjeta elevada donde se mostrara el formulario de login
            ElevatedCard(
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp), // sombreado de elevecacion de la tarjeta
                colors = CardDefaults.cardColors(containerColor = Color.White),  // color de fondo de la tarjeta
                modifier = Modifier.fillMaxWidth()        // ocupa el maximo ancho posible
                    .padding(start = 30.dp, end = 30.dp)  // padding en los laterales
            ){
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

                    Spacer(modifier = Modifier.height(10.dp))  // separacion vertical entre componentes

                    // TITULO
                    Text(
                        text = "Bienvenid@",               // texto
                        color = Color(0xFF017DB2),  // color del texto
                        style = TextStyle(
                            fontFamily = badcomic,   // fuente tipografica
                            fontSize = 45.sp         // tamaño de fuente
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))  // separacion vertical entre componentes

                    // se pide el email del usuario
                    OutlinedTextField(
                        value = email,  // valor del campo de texto
                        onValueChange = { if (it.length < 40){ email = it } },  // se limita la longitud a 40 caracteres
                        label = {
                            Text(
                                text = "Email del usuario",  // texto
                                color = Color.Black,         // color del texto
                                fontFamily = badcomic        // fuente tipografica
                            )
                        },
                        modifier = Modifier.width(310.dp),  // ancho del campo de texto
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color(0xFF017DB2),   // borde del campo cuando esta activo
                            unfocusedIndicatorColor = Color(0xFF017DB2), // borde del campo cuando no esta activo
                            focusedContainerColor = Color.White,   // color del fondo del campo cuando esta activo
                            unfocusedContainerColor = Color.White, // color del fondo del campo cuando no esta activo
                            cursorColor = Color(0xFF017DB2) // color del cursor en el campo de texto
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,            // tipo de teclado para el campo de texto
                            capitalization = KeyboardCapitalization.None, // no se capitaliza (no se trata las mayusculas) el texto del usuario
                            autoCorrectEnabled = true,                    // se habilita el autocorrector mientras escribe el usuario
                            imeAction = ImeAction.Next,                   // se habilita la acción de ir al siguiente campo de texto desde el teclado
                            showKeyboardOnFocus = true                    // se muestra el teclado cuando el campo recibe el foco
                        ),
                        textStyle = TextStyle(
                            color = Color.Black,      // color del texto introducido
                            fontFamily = badcomic     // fuente tipografica del texto introducida
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))  // separacion vertical entre componentes

                    // se pide la contraseña del usuario
                    OutlinedSecureTextField(
                        state = password,  // estado que contiene el texto introducido (la contraseña)
                        label = {
                            Text(
                                text = "Contraseña",   // texto
                                color = Color.Black,   // color del texto
                                fontFamily = badcomic  // fuente tipografica
                            )
                        },
                        modifier = Modifier.width(310.dp),  // ancho del campo de texto
                        // icono que va al final del campo de texto
                        trailingIcon = {
                            IconButton(
                                onClick = { passVisible = !passVisible }  // al pulsar el icono cambia el estado para mostrar/ocultar la contraseña
                            ){
                                Icon(
                                    // se cambia el icono si la contraseña es visible o no
                                    imageVector = if (passVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,

                                    // se cambia la descripcion para lectores de pantalla en funcion si la contraseña es visible o no
                                    contentDescription = if (passVisible) "Ocultar contraseña" else "Mostrar contraseña",

                                    tint = Color.Black // color del icono
                                )
                            }
                        },
                        // controla como se oculta el texto (lo hace visibible completamente o solo muestra el ultimo caracter)
                        textObfuscationMode = if (passVisible) TextObfuscationMode.Visible else TextObfuscationMode.RevealLastTyped,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color(0xFF017DB2),   // borde del campo cuando esta activo
                            unfocusedIndicatorColor = Color(0xFF017DB2), // borde del campo cuando no esta activo
                            focusedContainerColor = Color.White,   // color del fondo del campo cuando esta activo
                            unfocusedContainerColor = Color.White, // color del fondo del campo cuando no esta activo
                            cursorColor = Color(0xFF017DB2) // color del cursor en el campo de texto
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,         // tipo de teclado para el campo de texto
                            capitalization = KeyboardCapitalization.None, // no se capitaliza (no se trata las mayusculas) el texto del usuario
                            autoCorrectEnabled = false,                   // se inhabilita el autocorrector mientras escribe el usuario
                            imeAction = ImeAction.Send,                   // se habilita la acción de iniciar sesion desde el teclado (enviar formulario)
                            showKeyboardOnFocus = true                    // se muestra el teclado cuando el campo recibe el foco
                        ),
                        onKeyboardAction = {
                            // accion que se realiza al pulsar el boton de enviar formulario (se llama a la función de iniciar sesión)
                            //iniciarSesion(controladorNavegacion, email, password.text as String, context)
                        },
                        textStyle = TextStyle(
                            color = Color.Black,         // color del texto introducido
                            fontFamily = badcomic        // fuente tipografica del texto introducida
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))  // separacion vertical entre componentes

                    Row {
                        Text(
                            text = "¿Olvidaste la contraseña? ",  // texto
                            color = Color.Black,                          // color del texto
                            style = TextStyle(
                                fontFamily = badcomic,  // fuente tipografica del texto
                                fontSize = 16.sp                    // tamaño de fuente del texto
                            )
                        )

                        // texto clicable que lleva a un mensaje de dialogo para solicitar recuperar la contraseña
                        Text(
                            text = "Pulsa aquí",  // texto
                            color = Color(0xFF017DB2),  // color del texto
                            style = TextStyle(
                                fontFamily = badcomic,  // fuente tipografica del texto
                                fontSize = 16.sp,                   // tamaño de fuente del texto
                                fontWeight = FontWeight.Bold        // texto en negrita
                            ),
                            modifier = Modifier.clickable {

                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))  // separacion vertical entre componentes

                    // BOTON DE INICIO DE SESION
                    Button(
                        onClick = {

                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF017DB2),  // color de fondo del boton
                            contentColor = Color.White                  // color del texto del boton
                        )
                    ){
                        Text(
                            text = "Iniciar sesión",    // texto del boton
                            style = TextStyle(
                                fontFamily = badcomic,  // fuente tipografica
                                fontSize = 18.sp        // tamaño de fuente
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))  // separacion vertical entre componentes

                    Row {
                        Text(
                            text = "¿No tienes cuenta? ",  // texto
                            color = Color.Black,           // color del texto
                            style = TextStyle(
                                fontFamily = badcomic,   // fuente tipografica
                                fontSize = 16.sp         // tamaño de fuente
                            )
                        )

                        // texto clicable que lleva al formulario de registro
                        Text(
                            text = "Regístrate",  // texto
                            color = Color(0xFF017DB2),  // color del texto
                            style = TextStyle(
                                fontFamily = badcomic,           // fuente tipografica
                                fontSize = 16.sp,                // tamaño de fuente
                                fontWeight = FontWeight.Bold     // texto en negrita
                            ),
                            modifier = Modifier.clickable {
                                //controladorNavegacion.navigate(AppScreens.crearUsuario.route) // vas al formulario de crear el usuario
                            }
                        )
                    }







                }
            }









        }


    }



}