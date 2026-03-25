package com.juandeherrera.letskody.screens

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
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
import com.composables.icons.lucide.ArrowBigLeft
import com.composables.icons.lucide.Lucide
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.juandeherrera.letskody.R
import com.juandeherrera.letskody.clasesAuxiliares.paises
import com.juandeherrera.letskody.metodosAuxiliares.componentes.MensajeSnackbarHost
import com.juandeherrera.letskody.metodosAuxiliares.componentes.notificationSnackbar
import com.juandeherrera.letskody.metodosAuxiliares.interfaz.fondoDegradadoDiagonal
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.calcularEdad
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.convertirURIenBase64
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.crearUsuarioTemporal
import com.juandeherrera.letskody.navigation.AppScreens
import java.time.Instant
import java.time.ZoneId

@SuppressLint("DefaultLocale")
@RequiresApi(value = Build.VERSION_CODES.TIRAMISU) // solo se permite Android 13 o superior (API 33+)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PantallaCrearUsuario(controladorNavegacion: NavController) {
    val badcomic = FontFamily(Font(R.font.badcomic))  // fuente tipográfica por defecto

    val degradadoDiagonal = fondoDegradadoDiagonal(color1 = Color(0xFF0D47A1), color2 = Color(0xFF1976D2), color3 = Color(0xFF42A5F5))  // variable para obtener el degradado

    val scope = rememberCoroutineScope() // variable que crea un ámbito de corrutinas que se mantienen en la recomposición de la interfaz

    val snackbarHostState = remember { SnackbarHostState() } // variable de estado que controla el estado (mostrar/ocultar) del Snackbar

    val tipoSnackbar = remember { mutableStateOf(value = "error") }  // variable de estado para indicar el tipo de snackbar a mostrar por defecto

    // variables para los datos del formulario
    var nombre by remember { mutableStateOf(value = "") }                    // nombre
    var apellidos by remember { mutableStateOf(value = "") }                 // apellidos
    var paisSeleccionado by remember { mutableStateOf(value = paises[0]) }   // país seleccionado en el prefijo telefónico
    var expandedPrefijo by remember { mutableStateOf(value = false) }        // variable para abrir el menu desplegable del prefijo telefónico
    var telefono by remember { mutableStateOf(value = "") }                  // teléfono
    var email by remember { mutableStateOf(value = "") }                     // correo electrónico
    val password = rememberTextFieldState()                                  // contraseña
    var passVisible by remember { mutableStateOf(value = false) }            // variable para mostrar la contraseña
    var fechaNacimiento by remember { mutableStateOf(value = "") }           // fecha de nacimiento
    var mostrarCalendario by remember { mutableStateOf(value = false) }      // variable para mostrar el popup del calendario para elegir fecha
    val opcionesSexo = listOf("Hombre", "Mujer", "Otro")                     // lista de sexo disponibles a escoger
    var sexoSeleccionado by remember { mutableStateOf(value = "") }          // sexo seleccionado
    var uriImagenGaleria by remember { mutableStateOf<Uri?>(value = null) }  // URI de la imagen seleccionada desde la galería
    var imagen by remember { mutableStateOf(value = "") }                    // imagen codificada en Base64

    // launcher para la galería
    val launcherGaleriaUri = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        uriImagenGaleria = uri   // cuando el usuario seleccione una imagen de la galería, se guardará su URI
    }

    val emailPattern = Regex(pattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}") // patron que debe cumplir el email

    val context = LocalContext.current // se obtiene el contexto actual

    // bloque de código que se ejecuta cuando el usuario selecciona una imagen de la galería
    LaunchedEffect(key1 = uriImagenGaleria) {

        // si se ha cargado una imagen, se procede a convertirla a base64
        if (uriImagenGaleria != null) {

            // se obtiene el string en base64 para almacenarlo en la base de datos
            imagen = convertirURIenBase64(uriImagen = uriImagenGaleria!!, context = context, error = { mensaje ->
                notificationSnackbar(scope = scope, snackbarHostState = snackbarHostState, mensaje = mensaje) // si hay algún error se muestra un mensaje por Snackbar
            })
        }
    }

    Scaffold(
        // define el lugar donde se mostraran los Snackbar
        snackbarHost = {
            MensajeSnackbarHost(snackbarHostState = snackbarHostState, fuenteTipografica = badcomic, tipo = tipoSnackbar.value)
        }
    ){
        innerPadding ->

        Column(
            modifier = Modifier.fillMaxSize()                   // se ocupa el espacio disponible
                .background(brush = degradadoDiagonal)          // fondo con degradado animado
                .verticalScroll(state = rememberScrollState())  // scroll vertical
                .padding(paddingValues = innerPadding),          // se usa el padding por defecto
            horizontalAlignment = Alignment.CenterHorizontally,   // centrado horizontal
            verticalArrangement = Arrangement.Center              // centrado vertical
        ){

            // tarjeta donde se mostrará el formulario de registro
            ElevatedCard(
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),  // sombreado de elevación de la tarjeta
                colors = CardDefaults.cardColors(containerColor = Color.White),   // color de fondo de la tarjeta
                modifier = Modifier.fillMaxSize()    // se ocupa la pantalla completa
                    .padding(all = 30.dp)            // padding externo
            ){
                // fila para el botón de volver atrás
                Row(
                    modifier = Modifier.fillMaxWidth()      // se ocupa el maximo ancho disponible
                        .padding(start = 8.dp, top = 8.dp), // padding superior y en la izquierda
                    verticalAlignment = Alignment.CenterVertically, // centrado vertical
                    horizontalArrangement = Arrangement.Start       // alineación horizontal a la izquierda
                ){
                    // botón para volver atrás
                    IconButton(
                        onClick = { controladorNavegacion.navigate(route = AppScreens.Login.route) }  // al pulsarlo se vuelve a la pantalla de login
                    ){
                        Icon(
                            imageVector = Lucide.ArrowBigLeft,  // icono
                            contentDescription = "Volver al login", // descripción del icono
                            modifier = Modifier.size(40.dp),        // tamaño del icono
                            tint = Color.Black                      // color del icono
                        )
                    }
                }

                // columna para el formulario
                Column(
                    modifier = Modifier.fillMaxWidth()      // se ocupa el maximo ancho disponible
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp), // padding en los laterales e inferior
                    horizontalAlignment = Alignment.CenterHorizontally,   // centrado horizontal
                    verticalArrangement = Arrangement.Center              // centrado vertical
                ){
                    // TITULO
                    Text(
                        text = "Regístrate",         // texto
                        color = Color(0xFF017DB2),   // color del texto
                        style = TextStyle(
                            fontFamily = badcomic,   // fuente tipográfica del texto
                            fontSize = 40.sp         // tamaño de fuente del texto
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))  // separación vertical entre componentes

                    // se pide el nombre del usuario
                    OutlinedTextField(
                        value = nombre,  // valor del campo de texto
                        onValueChange = { if (it.length < 41){ nombre = it } },  // se limita la longitud a 40 caracteres
                        label = {
                            Text(
                                text = "Nombre del usuario",  // texto
                                color = Color.Black,          // color del texto
                                fontFamily = badcomic         // fuente tipográfica
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
                            keyboardType = KeyboardType.Text,             // tipo de teclado para el campo de texto
                            capitalization = KeyboardCapitalization.None, // no se capitaliza (no se trata las mayúsculas) el texto del usuario
                            autoCorrectEnabled = true,                    // se habilita el autocorrector mientras escribe el usuario
                            imeAction = ImeAction.Next,                   // se habilita la acción de ir al siguiente campo de texto desde el teclado
                            showKeyboardOnFocus = true                    // se muestra el teclado cuando el campo recibe el foco
                        ),
                        textStyle = TextStyle(
                            color = Color.Black,      // color del texto introducido
                            fontFamily = badcomic     // fuente tipográfica del texto introducida
                        ),
                        singleLine = true // el campo de texto solo puede tener una sola línea de texto (con TAB se pasa al siguiente campo)
                    )

                    Spacer(modifier = Modifier.height(20.dp))  // separación vertical entre componentes

                    // se piden los apellidos del usuario
                    OutlinedTextField(
                        value = apellidos,  // valor del campo de texto
                        onValueChange = { if (it.length < 61){ apellidos = it } },  // se limita la longitud a 60 caracteres
                        label = {
                            Text(
                                text = "Apellidos del usuario",  // texto
                                color = Color.Black,             // color del texto
                                fontFamily = badcomic            // fuente tipográfica
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
                            keyboardType = KeyboardType.Text,             // tipo de teclado para el campo de texto
                            capitalization = KeyboardCapitalization.None, // no se capitaliza (no se trata las mayúsculas) el texto del usuario
                            autoCorrectEnabled = true,                    // se habilita el autocorrector mientras escribe el usuario
                            imeAction = ImeAction.Next,                   // se habilita la acción de ir al siguiente campo de texto desde el teclado
                            showKeyboardOnFocus = true                    // se muestra el teclado cuando el campo recibe el foco
                        ),
                        textStyle = TextStyle(
                            color = Color.Black,      // color del texto introducido
                            fontFamily = badcomic     // fuente tipográfica del texto introducida
                        ),
                        singleLine = true // el campo de texto solo puede tener una sola línea de texto (con TAB se pasa al siguiente campo)
                    )

                    Spacer(modifier = Modifier.height(20.dp))  // separación vertical entre componentes

                    // fila para el prefijo telefónico y el número de teléfono
                    Row(
                        modifier = Modifier.width(310.dp),               // mismo ancho que los campos del formulario
                        verticalAlignment = Alignment.CenterVertically,  // centrado vertical
                        horizontalArrangement = Arrangement.Center       // centrado horizontal
                    ){

                        // se usa un box como contenedor para el menu desplegable del prefijo telefónico
                        // evita que el menu desplegable intente heredar el comportamiento del Row o del Card
                        Box(
                            modifier = Modifier.width(150.dp) // ancho del contenedor
                        ){

                            // campo de texto que actúa como ancla del menu desplegable del prefijo telefónico
                            OutlinedTextField(
                                value = "${paisSeleccionado.bandera} ${paisSeleccionado.prefijo}",  // valor del campo de texto (bandera y prefijo)
                                onValueChange = {},  // no se puede editar manualmente
                                readOnly = true,     // solo lectura (se modifica desde el menu desplegable)
                                label = {
                                    Text(
                                        text = "Prefijo",        // texto
                                        color = Color.Black,     // color del texto
                                        fontFamily = badcomic    // fuente tipográfica
                                    )
                                },
                                trailingIcon = {
                                    // icono animado que se muestra a la derecha del campo de texto para saber si el menu está desplegado
                                    if (!expandedPrefijo) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,   // icono
                                            contentDescription = "menu no mostrado",     // descripción
                                            tint = Color.Black                           // color del icono
                                        )
                                    }
                                    else {
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropUp,  // icono
                                            contentDescription = "menu mostrado",     // descripción
                                            tint = Color.Black                        // color del icono
                                        )
                                    }
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Color(0xFF017DB2),   // borde del campo cuando está activo
                                    unfocusedIndicatorColor = Color(0xFF017DB2), // borde del campo cuando no está activo
                                    focusedContainerColor = Color.White,         // color del fondo del campo cuando está activo
                                    unfocusedContainerColor = Color.White,       // color del fondo del campo cuando no está activo
                                    cursorColor = Color(0xFF017DB2)              // color del cursor en el campo de texto
                                ),
                                textStyle = TextStyle(
                                    color = Color.Black,      // color del texto introducido
                                    fontFamily = badcomic     // fuente tipográfica del texto introducida
                                ),
                                singleLine = true, // el campo de texto solo puede tener una sola línea de texto (con TAB se pasa al siguiente campo)
                                modifier = Modifier.fillMaxWidth() // el campo de texto abarca el ancho del contenedor
                            )

                            // capa de detección que al ser pulsar en el campo de texto se abra o cierre el menu desplegable
                            Box(
                                modifier = Modifier.matchParentSize()     // se ajusta el tamaño de la capa al del campo de texto (padre)
                                    .clickable{ expandedPrefijo = true }  // al pulsar la capa, se activa el menu desplegable
                            )

                            // menu desplegable estándar con la lista de paises y sus prefijos
                            // al contrario del Exposed, se comporta como un popup y es más resistente a fallos dentro de Cards y Columns con scroll
                            DropdownMenu(
                                expanded = expandedPrefijo,                      // controla el estado de apertura del menu desplegable
                                onDismissRequest = { expandedPrefijo = false },  // se cierra al pulsar afuera del menu
                                modifier = Modifier.background(Color(0xFF7D9CEE).copy(alpha = 0.3f))  // color de fondo del menu desplegable
                                    .width(150.dp)     // ancho del menu desplegable
                            ){
                                // se recorre la lista de paises
                                paises.forEach { pais ->

                                    // cada país es asignado como un elemento del menu
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "${pais.bandera} ${pais.nombre} (${pais.prefijo})",  // texto del elemento
                                                color = Color.Black,                                        // color del texto de elemento
                                                style = TextStyle(
                                                    fontFamily = badcomic,                                  // fuente tipográfica del elemento
                                                )
                                            )
                                        },
                                        onClick = {
                                            paisSeleccionado = pais  // guarda el pais seleccionado
                                            expandedPrefijo = false  // cierra el menu desplegable
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp)) // separación horizontal entre componentes

                        // se pide el número de teléfono al usuario
                        OutlinedTextField(
                            value = telefono,  // valor del campo de texto
                            onValueChange = { if (it.length < 10){ telefono = it.filter(Char::isDigit) } },  // se limita la longitud a 9 caracteres numéricos
                            label = {
                                Text(
                                    text = "Teléfono",       // texto
                                    color = Color.Black,     // color del texto
                                    fontFamily = badcomic    // fuente tipográfica
                                )
                            },
                            modifier = Modifier.weight(1f),  // ocupa el ancho disponible
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color(0xFF017DB2),   // borde del campo cuando está activo
                                unfocusedIndicatorColor = Color(0xFF017DB2), // borde del campo cuando no está activo
                                focusedContainerColor = Color.White,         // color del fondo del campo cuando está activo
                                unfocusedContainerColor = Color.White,       // color del fondo del campo cuando no está activo
                                cursorColor = Color(0xFF017DB2)              // color del cursor en el campo de texto
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone,            // tipo de teclado para el campo de texto
                                capitalization = KeyboardCapitalization.None, // no se capitaliza (no se trata las mayúsculas) el texto del usuario
                                autoCorrectEnabled = true,                    // se habilita el autocorrector mientras escribe el usuario
                                imeAction = ImeAction.Next,                   // se habilita la acción de ir al siguiente campo de texto desde el teclado
                                showKeyboardOnFocus = true                    // se muestra el teclado cuando el campo recibe el foco
                            ),
                            textStyle = TextStyle(
                                color = Color.Black,      // color del texto introducido
                                fontFamily = badcomic     // fuente tipográfica del texto introducida
                            ),
                            singleLine = true // el campo de texto solo puede tener una sola línea de texto (con TAB se pasa al siguiente campo)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))  // separación vertical entre componentes

                    // se pide el email del usuario
                    OutlinedTextField(
                        value = email,  // valor del campo de texto
                        onValueChange = { if (it.length < 41){ email = it } },  // se limita la longitud a 40 caracteres
                        label = {
                            Text(
                                text = "Email del usuario",  // texto
                                color = Color.Black,          // color del texto
                                fontFamily = badcomic         // fuente tipográfica
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
                        ),
                        singleLine = true // el campo de texto solo puede tener una sola línea de texto (con TAB se pasa al siguiente campo)
                    )

                    Spacer(modifier = Modifier.height(20.dp))  // separación vertical entre componentes

                    // se pide la contraseña del usuario
                    OutlinedSecureTextField(
                        state = password,  // estado que contiene el texto introducido (la contraseña)
                        label = {
                            Text(
                                text = "Contraseña",   // texto
                                color = Color.Black,   // color del texto
                                fontFamily = badcomic  // fuente tipográfica
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
                            imeAction = ImeAction.Next,                   // se habilita la acción de ir al siguiente campo de texto desde el teclado
                            showKeyboardOnFocus = true                    // se muestra el teclado cuando el campo recibe el foco
                        ),
                        textStyle = TextStyle(
                            color = Color.Black,         // color del texto introducido
                            fontFamily = badcomic        // fuente tipográfica del texto introducida
                        )
                    )

                    Spacer(modifier = Modifier.height(28.dp))  // separación vertical entre componentes

                    // tarjeta que contendrá las opciones del sexo del usuario
                    OutlinedCard(
                        shape = RoundedCornerShape(size = 5.dp),                          // bordes redondeados
                        colors = CardDefaults.cardColors(containerColor = Color.White),   // color de fondo de la tarjeta
                        border = BorderStroke(width = 1.dp, color = Color(0xFF017DB2)),   // borde de la tarjeta
                        modifier = Modifier.width(310.dp)   // ancho que coincide con el de los campos de texto
                    ){

                        // columna que contiene el titulo y las opciones del sexo
                        Column(
                            Modifier.padding(all = 10.dp) // padding externo
                        ){
                            // titulo
                            Text(
                                text = "Sexo",         // texto
                                color = Color.Black,   // color del texto
                                style = TextStyle(
                                    fontFamily = badcomic,        // fuente tipográfica del texto
                                    fontSize = 16.sp,             // tamaño de fuente del texto
                                    fontWeight = FontWeight.Bold  // texto en negrita
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))  // separación vertical entre componentes

                            // fila que contiene todas las opciones
                            Row(
                                modifier = Modifier.fillMaxWidth() // se ocupa el ancho posible
                                    .selectableGroup(),            // se indica que es un grupo seleccionable
                                horizontalArrangement = Arrangement.SpaceBetween,  // espaciado horizontal entre elementos
                                verticalAlignment = Alignment.CenterVertically     // centrado vertical
                            ){

                                // se recorre cada indice de la lista de opciones de sexo
                                opcionesSexo.forEach { text ->
                                    Row(
                                        modifier = Modifier.height(20.dp) // abarca la altura
                                            .selectable(
                                                selected = (text == sexoSeleccionado),  // si la opcion seleccionada corresponde con el texto
                                                onClick = { sexoSeleccionado = text },  // al pulsar se selecciona la opcion
                                                role = Role.RadioButton                 // se le asigna el rol de botón seleccionable
                                            ),
                                        horizontalArrangement = Arrangement.Center,     // centrado horizontal
                                        verticalAlignment = Alignment.CenterVertically  // centrado vertical
                                    ){
                                        // botón seleccionable
                                        RadioButton(
                                            selected = (text == sexoSeleccionado), // al seleccionarlo se guarda el valor escogido
                                            onClick = null,  // recomendado por accesibilidad para los lectores de pantalla
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = Color(0xFF017DB2),    // color cuando está seleccionado
                                                unselectedColor = Color.Black         // color cuando no está seleccionado
                                            )
                                        )

                                        // texto asociado al botón seleccionable
                                        Text(
                                            text = text,         // texto
                                            modifier = Modifier.padding(start = 10.dp),  // padding en el lateral izquierdo
                                            color = Color.Black,   // color del texto
                                            style = TextStyle(
                                                fontFamily = badcomic,   // fuente tipográfica del texto
                                                fontSize = 16.sp         // tamaño de fuente del texto
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))  // separación vertical entre componentes

                    // se pide la fecha de nacimiento del usuario
                    OutlinedTextField(
                        value = fechaNacimiento, // valor del campo de texto
                        onValueChange = {},  // no se puede editar manualmente
                        readOnly = true,     // solo lectura (se modifica desde el selector de fecha)
                        label = {
                            Text(
                                text = "Fecha de nacimiento",  // texto
                                color = Color.Black,           // color del texto
                                fontFamily = badcomic          // fuente tipográfica
                            )
                        },
                        modifier = Modifier.width(310.dp),  // ancho del campo de texto
                        trailingIcon = {
                            // icono que se muestra a la derecha del campo de texto
                            IconButton(
                                onClick = { mostrarCalendario = true } // se muestra el selector de fecha al pulsar el icono
                            ){
                                Icon(
                                    imageVector = Icons.Default.DateRange,                  // icono
                                    contentDescription = "Fecha de nacimiento del usuario", // descripción
                                    tint = Color.Black                                      // color del icono
                                )
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color(0xFF017DB2),   // borde del campo cuando está activo
                            unfocusedIndicatorColor = Color(0xFF017DB2), // borde del campo cuando no está activo
                            focusedContainerColor = Color.White,         // color del fondo del campo cuando está activo
                            unfocusedContainerColor = Color.White,       // color del fondo del campo cuando no está activo
                            cursorColor = Color(0xFF017DB2)              // color del cursor en el campo de texto
                        ),
                        textStyle = TextStyle(
                            color = Color.Black,         // color del texto introducido
                            fontFamily = badcomic        // fuente tipográfica del texto introducida
                        )
                    )

                    // si se ha pulsado el botón, se muestra el selector de fecha en un dialogo
                    if (mostrarCalendario) {
                        DatePickerDialog(
                            onDismissRequest = { mostrarCalendario = false }, // se cierra si se pulsa fuera
                            confirmButton = {
                                // botón de confirmar en el selector de fecha
                                TextButton(
                                    onClick = { mostrarCalendario = false }  // se cierra al procesar la fecha
                                ) {
                                    Text(
                                        text = "Aceptar",             // texto del botón
                                        color = Color(0xFF017DB2),    // color del texto
                                        style = TextStyle(
                                            fontFamily = badcomic,    // fuente tipográfica del texto
                                        )
                                    )
                                }
                            }
                        ) {
                            val estadoSelectorFecha = rememberDatePickerState()  // estado interno del selector fecha

                            DatePicker(
                                state = estadoSelectorFecha,  // estado del selector de fecha
                                colors = DatePickerDefaults.colors(
                                    selectedDayContainerColor = Color(0xFF017DB2),  // fondo del día seleccionado
                                    selectedDayContentColor = Color.White,          // texto del día seleccionado
                                    todayContentColor = Color(0xFF017DB2),          // texto del día actual
                                    todayDateBorderColor = Color(0xFF017DB2)        // borde del día actual
                                )
                            )

                            // al pulsar el botón de confirmar, se guarda la fecha
                            LaunchedEffect(key1 = estadoSelectorFecha.selectedDateMillis) {
                                val millis = estadoSelectorFecha.selectedDateMillis  // fecha en milisegundos

                                if (millis != null) {
                                    // se convierte los milisegundos a fecha local
                                    val fecha = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()

                                    val dia = fecha.dayOfMonth.toString().padStart(length = 2, padChar = '0') // dia con dos dígitos
                                    val mes = fecha.monthValue.toString().padStart(length = 2, padChar = '0') // mes con dos dígitos
                                    val ano = fecha.year.toString()  // año completo

                                    fechaNacimiento = "$dia/$mes/$ano"  // se guarda la fecha en formato DD/MM/AAAA
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))  // separación vertical entre componentes

                    // fila para el botón de cargar la imagen de perfil del usuario
                    Row(
                        modifier = Modifier.width(310.dp),                // mismo ancho que los campos del formulario
                        verticalAlignment = Alignment.CenterVertically,   // centrado vertical
                        horizontalArrangement = Arrangement.SpaceBetween  // espacio entre los elementos horizontalmente
                    ){
                        // botón para cargar la foto de perfil desde la galería
                        Button(
                            onClick = {
                                launcherGaleriaUri.launch(input = "image/*") // se abre la galería para seleccionar una imagen
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF017DB2),    // color de fondo del botón
                                contentColor = Color.White             // color del texto del botón
                            ),
                            modifier = Modifier.width(185.dp)  // ancho del botón
                        ){
                            Text(
                                text = "Cargar foto de perfil",   // texto del botón
                                style = TextStyle(
                                    fontFamily = badcomic,        // fuente tipográfica del texto
                                    fontSize = 14.sp              // tamaño de fuente del texto
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))  // separación horizontal entre componentes

                        // texto para indicar si se cargó la foto de la galería
                        Text(
                            text = if (uriImagenGaleria == null) { "No existe foto" } else { "Foto cargada" },  // texto según haya foto guardada
                            color = Color.Black,      // color del texto
                            style = TextStyle(
                                fontFamily = badcomic,  // fuente tipográfica del texto
                                fontSize = 14.sp        // tamaño de fuente
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))  // separación vertical entre componentes

                    // BOTÓN DE REGISTRAR USUARIO
                    Button(
                        onClick = {
                            // validaciones de los campos del formulario
                            when{
                                nombre.isBlank() -> {
                                    notificationSnackbar(scope = scope, snackbarHostState = snackbarHostState, mensaje = "El nombre no puede estar vacío.")
                                }
                                apellidos.isBlank() -> {
                                    notificationSnackbar(scope = scope, snackbarHostState = snackbarHostState, mensaje = "Los apellidos no pueden estar vacíos.")
                                }
                                telefono.isBlank() -> {
                                    notificationSnackbar(scope = scope, snackbarHostState = snackbarHostState, mensaje = "El teléfono no puede estar vacío.")
                                }
                                telefono.length < 9 -> {
                                    notificationSnackbar(scope = scope, snackbarHostState = snackbarHostState, mensaje = "El teléfono debe tener 9 dígitos.")
                                }
                                email.isBlank() -> {
                                    notificationSnackbar(scope = scope, snackbarHostState = snackbarHostState, mensaje = "El email no puede estar vacío.")
                                }
                                !email.matches(regex = emailPattern) -> {
                                    notificationSnackbar(scope = scope, snackbarHostState = snackbarHostState, mensaje = "El email no tiene un formato válido.")
                                }
                                password.text.length < 8 -> {
                                    notificationSnackbar(scope = scope, snackbarHostState = snackbarHostState, mensaje = "La contraseña debe tener 8 caracteres.")
                                }
                                sexoSeleccionado.isEmpty() -> {
                                    notificationSnackbar(scope = scope, snackbarHostState = snackbarHostState, mensaje = "Se debe elegir un sexo.")
                                }
                                fechaNacimiento.isBlank() -> {
                                    notificationSnackbar(scope = scope, snackbarHostState = snackbarHostState, mensaje = "La fecha de nacimiento no puede estar vacía.")
                                }
                                calcularEdad(fechaNacimiento) <= 6 -> {
                                    notificationSnackbar(scope = scope, snackbarHostState = snackbarHostState, mensaje = "No se supera la edad mínima (6 años).")
                                }
                                imagen.isBlank() -> {
                                    notificationSnackbar(scope = scope, snackbarHostState = snackbarHostState, mensaje = "Se debe elegir una foto de perfil.")
                                }
                                else -> {
                                    // se crea el usuario temporal
                                    crearUsuarioTemporal(
                                        controladorNavegacion = controladorNavegacion,
                                        scope = scope,
                                        snackbarHostState = snackbarHostState,
                                        context = context,
                                        nombre = nombre,
                                        apellidos = apellidos,
                                        telefono = paisSeleccionado.prefijo + telefono,
                                        email = email,
                                        password = password.text.toString(),
                                        sexo = sexoSeleccionado,
                                        fechaNacimiento = fechaNacimiento,
                                        foto = imagen
                                    )
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF017DB2),    // color de fondo del botón
                            contentColor = Color.White             // color del texto del botón
                        )
                    ){
                        Text(
                            text = "Registrar usuario",   // texto del botón
                            style = TextStyle(
                                fontFamily = badcomic,      // fuente tipográfica del texto
                                fontSize = 16.sp            // tamaño de fuente del texto
                            )
                        )
                    }
                }
            }
        }
    }
}