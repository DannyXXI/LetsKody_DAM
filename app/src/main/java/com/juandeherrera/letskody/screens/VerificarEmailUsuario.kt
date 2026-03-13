package com.juandeherrera.letskody.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.room.Room
import com.composables.icons.lucide.ArrowBigLeft
import com.composables.icons.lucide.Lucide
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.firebase.auth.FirebaseAuth
import com.juandeherrera.letskody.R
import com.juandeherrera.letskody.localdb.AppDB
import com.juandeherrera.letskody.localdb.Estructura
import com.juandeherrera.letskody.metodosAuxiliares.componentes.MensajeSnackbarHost
import com.juandeherrera.letskody.metodosAuxiliares.componentes.notificationSnackbar
import com.juandeherrera.letskody.metodosAuxiliares.interfaz.fondoDegradadoDiagonal
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.cancelarRegistroUsuarioFirebase
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.registrarUsuarioFirebase
import com.juandeherrera.letskody.navigation.AppScreens
import com.juandeherrera.letskody.notification.NotificationHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

@SuppressLint("DefaultLocale")
@RequiresApi(value = Build.VERSION_CODES.TIRAMISU) // solo se permite Android 13 o superior (API 33+)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PantallaVerificarEmailUsuario(controladorNavegacion: NavController) {

    val badcomic = FontFamily(Font(R.font.badcomic))  // fuente tipográfica por defecto

    val degradadoDiagonal = fondoDegradadoDiagonal(color1 = Color(0xFF0D47A1), color2 = Color(0xFF1976D2), color3 = Color(0xFF42A5F5))  // variable para obtener el degradado

    val scope = rememberCoroutineScope() // variable que crea un ámbito de corrutinas que se mantienen en la recomposición de la interfaz

    val snackbarHostState = remember { SnackbarHostState() } // variable de estado que controla el estado (mostrar/ocultar) del Snackbar

    val tipoSnackbar = remember { mutableStateOf(value = "error") }  // variable de estado para indicar el tipo de snackbar a mostrar por defecto

    val context = LocalContext.current // se obtiene el contexto actual

    val notificationHandler = NotificationHandler(context) // variable que se encarga de mostrar las notificaciones

    // instancia a la base de datos local (en el mismo hilo)
    val db = remember { Room.databaseBuilder(context, klass = AppDB::class.java, name = Estructura.DB.NAME).allowMainThreadQueries().build() }

    val auth = FirebaseAuth.getInstance() // instancia al sistema de autenticación de Firebase

    val usuarioTemporal by db.usuarioDao().getUser().collectAsState(initial = null)  // variable de estado que obtiene los datos del usuario temporal

    // se espera a que se carguen los datos del usuario
    when(usuarioTemporal){
        null -> {
            // si no existe el usuario temporal o se están cargando los datos del usuario temporal
            Box(
                modifier = Modifier.fillMaxSize(),  // se ocupa la pantalla completa
                contentAlignment = Alignment.Center // contenido centrado en el medio
            ){
                CircularProgressIndicator()  // barra de progreso circular infinito
            }

            return  // detiene la ejecución del resto del código hasta que no se carguen los datos del usuario temporal
        }
        else -> { /* aquí ya existe el usuario temporal */ }
    }

    // bloque de código que se ejecuta una sola vez cuando se carga toda la pantalla
    LaunchedEffect(key1 = Unit) {
        var usuarioVerificado = false  // variable para comprobar que el usuario ha sido verificado

        // bucle infinito controlado para comprobar periódicamente si el usuario ha verificado su email
        while (!usuarioVerificado) {
            val user = auth.currentUser  // se obtiene el usuario actual autenticado

            if (user == null) {
                // si no existe un usuario autenticado, se vuelve a la pantalla de login (se limpia el historial de navegación)
                controladorNavegacion.navigate(route = AppScreens.Login.route) { popUpTo(id = 0) }

                break // se rompe el bucle infinito
            }

            try {
                user.reload().await()  // se obliga a Firebase a consultar en el servidor y actualizar el estado del usuario
            }
            catch (e: Exception) {
                // si falla el reload (sin conexión a Internet), se reintenta en el siguiente ciclo (2 segundos)
                delay(timeMillis = 2000)
                continue
            }

            // si el usuario verifico su email, se registra sus datos en Firebase
            if (user.isEmailVerified) {

                usuarioVerificado = true // se modifica la variable indicando que el usuario verifico su email

                registrarUsuarioFirebase(
                    uid = user.uid,
                    usuarioTemporal = usuarioTemporal!!,
                    db = db,
                    notificationHandler = notificationHandler,
                    exito = {
                        // si el registro se hizo correctamente, se navega a la pantalla de login (se limpia el historial de navegación)
                        controladorNavegacion.navigate(AppScreens.Login.route) { popUpTo(AppScreens.VerificarEmailUsuario.route) {inclusive = true} }
                    },
                    error = { mensaje ->
                        usuarioVerificado = false // si falla el registro se permite volverlo a intentar

                        // si ocurre algún error durante el registro del usuario se muestra un mensaje al usuario
                        notificationSnackbar(scope = scope, snackbarHostState = snackbarHostState, mensaje = mensaje)
                    }
                )
            }

            if (!usuarioVerificado) delay(timeMillis = 2000) // se espera 2 segundos antes de volver a comprobar si se verifico el email
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
                .padding(paddingValues = innerPadding),         // se usa el padding por defecto
            horizontalAlignment = Alignment.CenterHorizontally,   // centrado horizontal
            verticalArrangement = Arrangement.Center              // centrado vertical
        ){
            // tarjeta donde se mostrará el aviso de verificación y el botón
            ElevatedCard(
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),  // sombreado de elevación de la tarjeta
                colors = CardDefaults.cardColors(containerColor = Color.White),   // color de fondo de la tarjeta
                modifier = Modifier.fillMaxWidth()      // se ocupa el maximo ancho disponible
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
                        onClick = {
                            // al pulsarlo se cancela el registro del usuario temporal en Firebase
                            cancelarRegistroUsuarioFirebase(controladorNavegacion = controladorNavegacion, db = db, auth = auth, email = usuarioTemporal!!.emailUsuario)
                        }
                    ){
                        Icon(
                            imageVector = Lucide.ArrowBigLeft,      // icono
                            contentDescription = "Volver al login", // descripción del icono
                            modifier = Modifier.size(40.dp),        // tamaño del icono
                            tint = Color.Black                      // color del icono
                        )
                    }
                }

                // columna para el contenido del mensaje
                Column(
                    modifier = Modifier.fillMaxWidth()      // se ocupa el maximo ancho disponible
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp), // padding en los laterales e inferior
                    horizontalAlignment = Alignment.CenterHorizontally,   // centrado horizontal
                    verticalArrangement = Arrangement.Center              // centrado vertical
                ){
                    // TITULO
                    Text(
                        text = "Verifica tu email",  // texto
                        color = Color(0xFF017DB2),   // color del texto
                        style = TextStyle(
                            fontFamily = badcomic,   // fuente tipográfica del texto
                            fontSize = 34.sp         // tamaño de fuente del texto
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))  // separación vertical entre componentes

                    // MENSAJE DE AVISO
                    Text(
                        text = "¡Ya falta poco! Solo debemos verificar que tu email te pertenezca y no seas ningún duende travieso.",    // texto
                        color = Color.Black,                 // color del texto
                        style = TextStyle(
                            fontFamily = badcomic,           // fuente tipográfica del texto
                            fontSize = 16.sp,                // tamaño de fuente del texto
                            textAlign = TextAlign.Justify    // texto alineado de manera justificada
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))  // separación vertical entre componentes

                    // MENSAJE DE AVISO
                    Text(
                        text = "Pulsa en el botón para recibir el enlace de verificación; si no lo encuentras revisa tu carpeta de Spam.",    // texto
                        color = Color.Black,                 // color del texto
                        style = TextStyle(
                            fontFamily = badcomic,           // fuente tipográfica del texto
                            fontSize = 16.sp,                // tamaño de fuente del texto
                            textAlign = TextAlign.Justify    // texto alineado de manera justificada
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))  // separación vertical entre componentes

                    // BOTÓN DE ENVÍO DE CORREO ELECTRÓNICO DE VERIFICACIÓN
                    Button(
                        onClick = {
                            auth.currentUser?.sendEmailVerification()  // se envía email de verificación al email del usuario
                            tipoSnackbar.value = "success" // se cambia el tipo de snackbar
                            notificationSnackbar(scope = scope, snackbarHostState = snackbarHostState, mensaje = "Email de verificación enviado.")  // mensaje al usuario
                            // tras mostrarse el snackbar de exito se vuelve a convertir en uno de tipo error
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF017DB2),    // color de fondo del botón
                            contentColor = Color.White             // color del texto del botón
                        )
                    ){
                        Text(
                            text = "Enviar email de verificación",   // texto del botón
                            style = TextStyle(
                                fontFamily = badcomic,        // fuente tipográfica del texto
                                fontSize = 16.sp              // tamaño de fuente del texto
                            )
                        )
                    }
                }
            }
        }
    }
}