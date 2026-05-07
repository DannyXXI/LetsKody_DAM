package com.juandeherrera.letskody.metodosAuxiliares.componentes

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Rotate90DegreesCw
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.juandeherrera.letskody.clasesAuxiliares.ResultadoJuegoContrarreloj
import com.juandeherrera.letskody.clasesAuxiliares.ResultadoJuegoCronometro
import com.juandeherrera.letskody.metodosAuxiliares.componentes.juegos.FilaResultado
import com.juandeherrera.letskody.metodosAuxiliares.componentes.juegos.FilaTiempoYPenalizacion
import com.juandeherrera.letskody.metodosAuxiliares.interfaz.fondoDegradadoDiagonal
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.formatearSegundos
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.recortarBitmap
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import androidx.core.graphics.toColorInt

// función auxiliar para cargar el modal para la modificación de contraseña del usuario
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalModificarPassword(context: Context, fuenteTipografica: FontFamily, email: String, detectorEmail: (String) -> Unit, cerrar: () -> Unit, enviar: () -> Unit) {
    // MODAL (cuadro de diálogo)
    BasicAlertDialog(
        onDismissRequest = { cerrar() }  // se cierra el modal cuando se pulsa afuera de él
    ){
        // tarjeta que conformará el modal
        ElevatedCard(
            shape = RoundedCornerShape(size = 10.dp),                         // bordes redondeados
            colors = CardDefaults.cardColors(containerColor = Color.White),   // color de fondo de la tarjeta
            modifier = Modifier.fillMaxWidth()                                // ocupa el máximo ancho posible
                .padding(all = 22.dp)                                         // padding externo
        ){
            // columna que contiene el contenido de la tarjeta
            Column(
                modifier = Modifier.padding(all = 20.dp),  // padding externo
                horizontalAlignment = Alignment.CenterHorizontally  // centrado horizontalmente
            ){
                // TITULO
                Text(
                    text = "Recuperar contraseña",   // texto
                    color = Color(0xFF017DB2),       // color del texto
                    style = TextStyle(
                        fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                        fontSize = 20.sp,                // tamaño del texto
                        fontWeight = FontWeight.Bold     // texto en negrita
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))  // separación vertical entre componentes

                // MENSAJE
                Text(
                    text = "¡Oh no! Olvidaste tu contraseña.\n\nNo te preocupes, Kody se encargará de todo. Solo necesita tu email para enviarte un enlace para restablecer tu contraseña.",
                    color = Color.Black,    // color del texto
                    style = TextStyle(
                        fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                        fontSize = 15.sp,                // tamaño del texto
                        textAlign = TextAlign.Justify    // texto alineado de manera justificada
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))  // separación vertical entre componentes

                // campo de texto para escribir el email
                OutlinedTextField(
                    value = email,  // valor del campo de texto
                    onValueChange = { if (it.length < 41){ detectorEmail(it) } },  // se limita la longitud a 40 caracteres
                    label = {
                        Text(
                            text = "Email del usuario",      // texto
                            color = Color.Black,             // color del texto
                            fontFamily = fuenteTipografica   // fuente tipográfica
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
                        showKeyboardOnFocus = true                    // se muestra el teclado cuando el campo recibe el foco
                    ),
                    textStyle = TextStyle(
                        color = Color.Black,              // color del texto introducido
                        fontFamily = fuenteTipografica    // fuente tipográfica del texto introducida
                    ),
                    singleLine = true // el campo de texto solo puede tener una sola línea de texto (con TAB se pasa al siguiente campo)
                )

                Spacer(modifier = Modifier.height(20.dp))  // separación vertical entre componentes

                // fila que contiene los botones
                Row(
                    modifier = Modifier.fillMaxWidth(),             // se ocupa el maximo ancho posible
                    horizontalArrangement = Arrangement.End,        // alineación horizontal a la derecha
                    verticalAlignment = Alignment.CenterVertically  // centrado vertical
                ){
                    // BOTÓN DE CANCELAR (texto seleccionable)
                    Text(
                        text = "Cancelar",             // texto
                        color = Color(0xFF017DB2),     // color del texto
                        style = TextStyle(
                            fontFamily = fuenteTipografica,      // fuente tipográfica del texto
                            fontSize = 16.sp                     // tamaño del texto
                        ),
                        modifier = Modifier.padding(all = 8.dp)  // padding externo
                            .clickable{ cerrar() }               // al pulsar se cierra el modal
                    )

                    Spacer(modifier = Modifier.width(16.dp))  // separación horizontal entre componentes

                    // BOTÓN DE ENVIAR
                    Button(
                        onClick = {
                            val emailPattern = Regex(pattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}") // patron que debe cumplir el email

                            // validaciones básicas del campo de texto del email
                            when{
                                email.isBlank() -> {
                                    Toast.makeText(context, "El email no puede estar vacío.", Toast.LENGTH_LONG).show()
                                }
                                !email.matches(regex = emailPattern) -> {
                                    Toast.makeText(context, "El email no tiene un formato válido.", Toast.LENGTH_LONG).show()
                                }
                                else -> {
                                    enviar()  // se procesa el envío del enlace de recuperación de contraseña
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF017DB2),    // color de fondo del botón
                            contentColor = Color.White             // color del texto del botón
                        )
                    ){
                        Text(
                            text = "Enviar",                     // texto del botón
                            style = TextStyle(
                                fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                                fontSize = 16.sp                 // tamaño de fuente del texto
                            )
                        )
                    }
                }
            }
        }
    }
}

// función auxiliar para cargar el modal para la eliminación de la cuenta del usuario
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalEliminarCuenta(context: Context, fuenteTipografica: FontFamily, password: TextFieldState, passVisible: Boolean, esUsuarioGoogle: Boolean, mostrarPassword: () -> Unit, cerrar: () -> Unit, enviar: () -> Unit) {
    // MODAL (cuadro de diálogo)
    BasicAlertDialog(
        onDismissRequest = { cerrar() }  // se cierra el modal cuando se pulsa afuera de él
    ){
        // tarjeta que conformará el modal
        ElevatedCard(
            shape = RoundedCornerShape(size = 10.dp),                         // bordes redondeados
            colors = CardDefaults.cardColors(containerColor = Color.White),   // color de fondo de la tarjeta
            modifier = Modifier.fillMaxWidth()                                // ocupa el máximo ancho posible
                .padding(all = 20.dp)                                         // padding externo
        ){
            // columna que contiene el contenido de la tarjeta
            Column(
                modifier = Modifier.padding(all = 20.dp),           // padding externo
                horizontalAlignment = Alignment.CenterHorizontally  // centrado horizontalmente
            ){
                // TITULO
                Text(
                    text = "Eliminar cuenta",   // texto
                    color = Color(0xFF017DB2),       // color del texto
                    style = TextStyle(
                        fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                        fontSize = 22.sp,                // tamaño del texto
                        fontWeight = FontWeight.Bold     // texto en negrita
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))  // separación vertical entre componentes

                // si el usuario sesión con Google no se le pedirá la contraseña (no existe en Firebase Authentication)
                if (esUsuarioGoogle) {
                    // MENSAJE
                    Text(
                        text = "¿Estás seguro de querer eliminar todos tus datos?\n\nKody necesitará que pulses el botón para confirmar la acción.",
                        color = Color.Black,    // color del texto
                        style = TextStyle(
                            fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                            fontSize = 15.sp,                // tamaño del texto
                            textAlign = TextAlign.Justify    // texto alineado de manera justificada
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))  // separación vertical entre componentes
                }
                else {
                    // MENSAJE
                    Text(
                        text = "¿Estás seguro de querer eliminar todos tus datos?\n\nKody necesita tu contraseña para realizar esta acción.",
                        color = Color.Black,    // color del texto
                        style = TextStyle(
                            fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                            fontSize = 15.sp,                // tamaño del texto
                            textAlign = TextAlign.Justify    // texto alineado de manera justificada
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))  // separación vertical entre componentes

                    // se pide la contraseña del usuario
                    OutlinedSecureTextField(
                        state = password,  // estado que contiene el texto introducido (la contraseña)
                        label = {
                            Text(
                                text = "Contraseña",   // texto
                                color = Color.Black,   // color del texto
                                fontFamily = fuenteTipografica  // fuente tipográfica del texto
                            )
                        },
                        modifier = Modifier.width(310.dp),  // ancho del campo de texto
                        // icono situado al final del campo de texto
                        trailingIcon = {
                            IconButton(
                                onClick = { mostrarPassword() }  // al pulsar el icono cambia el estado para mostrar/ocultar la contraseña
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
                            showKeyboardOnFocus = true                    // se muestra el teclado cuando el campo recibe el foco
                        ),
                        textStyle = TextStyle(
                            color = Color.Black,              // color del texto introducido
                            fontFamily = fuenteTipografica    // fuente tipográfica del texto introducida
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))  // separación vertical entre componentes
                }

                // fila que contiene los botones
                Row(
                    horizontalArrangement = Arrangement.End,        // alineación horizontal a la derecha
                    verticalAlignment = Alignment.CenterVertically, // centrado vertical
                    modifier = Modifier.fillMaxWidth()              // ocupa el espacio disponible
                ){
                    // BOTÓN DE CANCELAR (texto seleccionable)
                    Text(
                        text = "Cancelar",             // texto
                        color = Color(0xFF017DB2),     // color del texto
                        style = TextStyle(
                            fontFamily = fuenteTipografica,      // fuente tipográfica del texto
                            fontSize = 14.sp                     // tamaño del texto
                        ),
                        modifier = Modifier.padding(all = 8.dp)  // padding externo
                            .clickable{ cerrar() }               // al pulsar se cierra el modal
                    )

                    Spacer(modifier = Modifier.width(16.dp))  // separación horizontal entre componentes

                    // BOTÓN DE ENVIAR
                    Button(
                        onClick = {
                            // validaciones básicas del campo de texto del email
                            when{
                                password.text.length < 8  && !esUsuarioGoogle-> {
                                    Toast.makeText(context, "La contraseña debe tener 8 caracteres.", Toast.LENGTH_LONG).show()
                                }
                                else -> {
                                    enviar()  // se procesa la eliminación de la cuenta del usuario
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,    // color de fondo del botón
                            contentColor = Color.White             // color del texto del botón
                        )
                    ){
                        Text(
                            text = "Eliminar cuenta",                     // texto del botón
                            style = TextStyle(
                                fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                                fontSize = 14.sp                 // tamaño de fuente del texto
                            )
                        )
                    }
                }
            }
        }
    }
}

// función auxiliar para cargar el modal de inactividad en los juegos
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalInactividadJuego(cuentaAtras: Int, fuenteTipografica: FontFamily, continuar: () -> Unit) {
    // MODAL (cuadro de diálogo)
    BasicAlertDialog(
        onDismissRequest = { }  // no se puede cerrar el modal cuando se pulsa afuera de él
    ){
        // tarjeta que conformará el modal
        ElevatedCard(
            shape = RoundedCornerShape(size = 10.dp),                         // bordes redondeados
            colors = CardDefaults.cardColors(containerColor = Color.White),   // color de fondo de la tarjeta
            modifier = Modifier.fillMaxWidth()                                // ocupa el máximo ancho posible
                .padding(all = 14.dp)                                         // padding externo
        ){
            // columna que contiene el contenido de la tarjeta
            Column(
                modifier = Modifier.padding(all = 14.dp),           // padding externo
                horizontalAlignment = Alignment.CenterHorizontally  // centrado horizontalmente
            ){
                // TITULO
                Text(
                    text = "¿Sigues ahí?",   // texto
                    color = Color(0xFF017DB2),       // color del texto
                    style = TextStyle(
                        fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                        fontSize = 22.sp,                // tamaño del texto
                        fontWeight = FontWeight.Bold     // texto en negrita
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))  // separación vertical entre componentes

                // MENSAJE
                Text(
                    text = "Kody no ha detectado ninguna actividad en la pantalla del juego.",
                    color = Color.Black,    // color del texto
                    style = TextStyle(
                        fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                        fontSize = 14.sp,                // tamaño del texto
                        textAlign = TextAlign.Justify    // texto alineado de manera justificada
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))  // separación vertical entre componentes

                // MENSAJE
                Text(
                    text = "Kody va a cerrar este juego en:",
                    color = Color.Black,    // color del texto
                    style = TextStyle(
                        fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                        fontSize = 14.sp,                // tamaño del texto
                        textAlign = TextAlign.Left    // texto alineado de manera justificada
                    ),
                    modifier = Modifier.fillMaxWidth()  // se ocupa el ancho disponible
                )

                Spacer(modifier = Modifier.height(16.dp))  // separación vertical entre componentes

                // CÍRCULO CON LA CUENTA ATRÁS (EN SEGUNDOS)
                Box(
                    modifier = Modifier.size(60.dp)  // tamaño del circulo
                        .background(color = Color(0xFF1565C0), shape = CircleShape),  // fondo de forma circular
                    contentAlignment = Alignment.Center  // se centra el contenido del círculo
                ){
                    // tiempo de la cuenta atrás
                    Text(
                        text = "$cuentaAtras",   // texto
                        color = Color.White,    // color del texto
                        style = TextStyle(
                            fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                            fontSize = 26.sp,                // tamaño del texto
                            fontWeight = FontWeight.Bold     // texto en negrita
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))  // separación vertical entre componentes

                // BOTÓN DE SEGUIR JUGANDO
                Button(
                    onClick = continuar,   // al pulsar el botón se cierra el modal y se continua con el juego
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF348AEC),    // color de fondo del botón
                        contentColor = Color.White             // color del texto del botón
                    ),
                    shape = RoundedCornerShape(size = 10.dp)  // bordes redondeados
                ){
                    Text(
                        text = "¡Seguir jugando!",   // texto
                        color = Color.White,    // color del texto
                        style = TextStyle(
                            fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                            fontWeight = FontWeight.Bold     // texto en negrita
                        )
                    )
                }
            }
        }
    }
}

// función auxiliar para cargar el modal con la puntuación del usuario en los juegos de cronómetro
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalPuntuacionJuegosCronometro(resultado: ResultadoJuegoCronometro, fuenteTipografica: FontFamily, repetir: () -> Unit, guardarYsalir: () -> Unit) {
    // MODAL (cuadro de diálogo)
    BasicAlertDialog(
        onDismissRequest = { }  // no se puede cerrar el modal cuando se pulsa afuera de él
    ){
        // tarjeta que conformará el modal
        ElevatedCard(
            shape = RoundedCornerShape(size = 12.dp),                         // bordes redondeados
            colors = CardDefaults.cardColors(containerColor = Color.White),   // color de fondo de la tarjeta
            modifier = Modifier.fillMaxWidth()                                // ocupa el máximo ancho posible
                .padding(all = 14.dp)                                         // padding externo
        ){
            // columna que contiene el contenido de la tarjeta
            Column(
                modifier = Modifier.padding(all = 14.dp),           // padding externo
                horizontalAlignment = Alignment.CenterHorizontally  // centrado horizontalmente
            ){
                // TITULO
                Text(
                    text = "¡Partida terminada!",    // texto
                    color = Color(0xFF142E49),       // color del texto
                    style = TextStyle(
                        fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                        fontSize = 22.sp,                // tamaño del texto
                        fontWeight = FontWeight.Bold     // texto en negrita
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))  // separación vertical entre componentes

                // fila de puntuación
                FilaResultado(
                    etiqueta = "Puntuación",
                    valor = "${resultado.puntos} ⭐",
                    fuenteTipografica = fuenteTipografica,
                    color = Color.Black,
                    negrita = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(8.dp))  // separación vertical entre componentes

                // fila de tiempo
                FilaTiempoYPenalizacion(
                    tiempo = formatearSegundos(segundos = resultado.tiempoBase),
                    penalizacion = "${resultado.penalizacion}",
                    fuenteTipografica = fuenteTipografica
                )

                Spacer(modifier = Modifier.height(8.dp))  // separación vertical entre componentes

                // fila de tiempo total
                FilaResultado(
                    etiqueta = "Tiempo total",
                    valor = formatearSegundos(segundos = resultado.tiempoTotal),
                    fuenteTipografica = fuenteTipografica,
                    color = Color.Black,
                    negrita = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))  // separación vertical entre componentes

                // BOTÓN DE REPETIR PARTIDA
                Button(
                    onClick = repetir,   // al pulsar el botón se repite la partida sin guardar la puntuación
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF348AEC),    // color de fondo del botón
                        contentColor = Color.White             // color del texto del botón
                    ),
                    shape = RoundedCornerShape(size = 10.dp),  // bordes redondeados
                    modifier = Modifier.fillMaxWidth()         // ocupa el máximo ancho posible
                ){
                    Text(
                        text = "Repetir sin guardar",   // texto
                        style = TextStyle(
                            fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                            fontWeight = FontWeight.Bold     // texto en negrita
                        )
                    )
                }

                // BOTÓN DE GUARDAR PARTIDA Y SALIR
                OutlinedButton(
                    onClick = guardarYsalir,   // al pulsar el botón se guarda la partida y se sale del juego
                    border = BorderStroke(width = 1.dp, color = Color(0xFF348AEC)),  // grosor y color del borde
                    shape = RoundedCornerShape(size = 10.dp),  // bordes redondeados
                    modifier = Modifier.fillMaxWidth()         // ocupa el máximo ancho posible
                ){
                    Text(
                        text = "Guardar y salir",   // texto
                        color = Color(0xFF348AEC),        // color del texto
                        style = TextStyle(
                            fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                            fontWeight = FontWeight.Bold     // texto en negrita
                        )
                    )
                }
            }
        }
    }
}

// función auxiliar para cargar el modal con la puntuación del usuario en los juegos de contrarreloj
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalPuntuacionJuegosContrarreloj(resultado: ResultadoJuegoContrarreloj, fuenteTipografica: FontFamily, repetir: () -> Unit, guardarYsalir: () -> Unit) {
    // MODAL (cuadro de diálogo)
    BasicAlertDialog(
        onDismissRequest = { }  // no se puede cerrar el modal cuando se pulsa afuera de él
    ){
        // tarjeta que conformará el modal
        ElevatedCard(
            shape = RoundedCornerShape(size = 12.dp),                         // bordes redondeados
            colors = CardDefaults.cardColors(containerColor = Color.White),   // color de fondo de la tarjeta
            modifier = Modifier.fillMaxWidth()                                // ocupa el máximo ancho posible
                .padding(all = 14.dp)                                         // padding externo
        ){
            // columna que contiene el contenido de la tarjeta
            Column(
                modifier = Modifier.padding(all = 14.dp),           // padding externo
                horizontalAlignment = Alignment.CenterHorizontally  // centrado horizontalmente
            ){
                // TITULO
                Text(
                    text = "¡Tiempo!",    // texto
                    color = Color(0xFF142E49),       // color del texto
                    style = TextStyle(
                        fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                        fontSize = 22.sp,                // tamaño del texto
                        fontWeight = FontWeight.Bold     // texto en negrita
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))  // separación vertical entre componentes

                // fila de puntuación
                FilaResultado(
                    etiqueta = "Puntuación",
                    valor = "${resultado.puntos} ⭐",
                    fuenteTipografica = fuenteTipografica,
                    color = Color.Black,
                    negrita = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(8.dp))  // separación vertical entre componentes

                // fila de fallos
                FilaResultado(
                    etiqueta = "Fallos",
                    valor = "${resultado.fallos}",
                    fuenteTipografica = fuenteTipografica,
                    color = Color.Red,
                    negrita = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(16.dp))  // separación vertical entre componentes

                // BOTÓN DE REPETIR PARTIDA
                Button(
                    onClick = repetir,   // al pulsar el botón se repite la partida sin guardar la puntuación
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF348AEC),    // color de fondo del botón
                        contentColor = Color.White             // color del texto del botón
                    ),
                    shape = RoundedCornerShape(size = 10.dp),  // bordes redondeados
                    modifier = Modifier.fillMaxWidth()         // ocupa el máximo ancho posible
                ){
                    Text(
                        text = "Repetir sin guardar",   // texto
                        style = TextStyle(
                            fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                            fontWeight = FontWeight.Bold     // texto en negrita
                        )
                    )
                }

                // BOTÓN DE GUARDAR PARTIDA Y SALIR
                OutlinedButton(
                    onClick = guardarYsalir,   // al pulsar el botón se guarda la partida y se sale del juego
                    border = BorderStroke(width = 1.dp, color = Color(0xFF348AEC)),  // grosor y color del borde
                    shape = RoundedCornerShape(size = 10.dp),  // bordes redondeados
                    modifier = Modifier.fillMaxWidth()         // ocupa el máximo ancho posible
                ){
                    Text(
                        text = "Guardar y salir",   // texto
                        color = Color(0xFF348AEC),        // color del texto
                        style = TextStyle(
                            fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                            fontWeight = FontWeight.Bold     // texto en negrita
                        )
                    )
                }
            }
        }
    }
}

// función auxiliar para cargar el modal de recortar la imagen
@SuppressLint("UseKtx")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalRecortarImagen (uri: Uri, context: Context, diametroCirculo: Int = 200, fuenteTipografica: FontFamily, confirmar: (String) -> Unit, cancelar: () -> Unit) {

    // se descodifica la imagen desde su URI a un objeto Bitmap en memoria
    val bitmapOriginal = remember(key1 = uri) {
        context.contentResolver.openInputStream(uri)?.use { stream -> BitmapFactory.decodeStream(stream) }
    }

    val density = LocalDensity.current

    val diametroCirculoPx = with(receiver = density) { diametroCirculo.dp.toPx() }  // se convierte el diametro del círculo de dp a px

    // escala mínima que tendrá la imagen para cubrir el círculo completo sin dejar huecos
    val escalaMinima = remember(key1 = bitmapOriginal, key2 = diametroCirculoPx) {
        val factorW = diametroCirculoPx / bitmapOriginal!!.width.toFloat()
        val factorH = diametroCirculoPx / bitmapOriginal.height.toFloat()
        max(a = factorW, b = factorH)
    }

    val escalaMaxima = escalaMinima * 3f  // escala máxima (se limita a tres veces la mínima para que no sobresalga del contenedor)

    var escala by remember { mutableFloatStateOf(value = escalaMinima) }    // escala actual de la imagen
    var desplazamiento by remember { mutableStateOf(value = Offset.Zero) }  // desplazamiento actual de la imagen
    var rotacion by remember { mutableFloatStateOf(value = 0f) }            // ángulo de rotación actual (múltiplos de 90°)

    // función interna para calcular los límites del desplazamiento teniendo en cuenta la rotación actual
    fun limitarDesplazamiento (nuevoDesplazamiento: Offset, escala: Float, rotacion: Float): Offset {
        val rotacionRadianes = Math.toRadians(rotacion.toDouble())  // conversión de grados a radianes
        val imgW = bitmapOriginal!!.width * escala                  // ancho de la imagen con la escala aplicada
        val imgH = bitmapOriginal.height * escala                   // alto de la imagen con la escala aplicada

        // bounding box del rectángulo rotado (fórmula del rectángulo girado)
        val boundW = (imgW * abs(x = cos(x = rotacionRadianes)) + imgH * abs(x = sin(x = rotacionRadianes))).toFloat()
        val boundH = (imgW * abs(x = sin(x = rotacionRadianes)) + imgH * abs(x = cos(x = rotacionRadianes))).toFloat()

        // máximo desplazamiento permitido en cada eje para que la imagen siga cubriendo el círculo
        val maxX = max(a = 0f, b = (boundW - diametroCirculoPx) / 2f)
        val maxY = max(a = 0f, b = (boundH - diametroCirculoPx) / 2f)

        // se limita el desplazamiento dentro del rango calculado
        return Offset(x = nuevoDesplazamiento.x.coerceIn(-maxX, maxX), y = nuevoDesplazamiento.y.coerceIn(-maxY, maxY))
    }

    // modal de pantalla completa sin ancho predeterminado de plataforma
    BasicAlertDialog(
        onDismissRequest = cancelar,
        modifier = Modifier,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        content = {
            // superficie exterior que define la forma del modal
            Surface(
                shape = RoundedCornerShape(size = 22.dp),           // bordes redondeados
                color = Color.Transparent,                          // fondo transparente
                modifier = Modifier.fillMaxWidth(fraction = 0.92f)  // ocupa el 92% del ancho de la pantalla
            ){
                // contenedor principal
                Box(
                    modifier = Modifier.fillMaxWidth()  // se ocupa el ancho disponible
                        .background(brush = fondoDegradadoDiagonal(color1 = Color(0xFF0D47A1), color2 = Color(0xFF1976D2), color3 = Color(0xFF42A5F5)), shape = RoundedCornerShape(size = 22.dp))  // color de fondo con bordes redondeados
                ){
                    // columna principal que organiza todos los elementos verticalmente
                    Column(
                        modifier = Modifier.fillMaxWidth()                   // se ocupa el ancho disponible
                            .padding(horizontal = 24.dp, vertical = 28.dp),  // padding interno
                        horizontalAlignment = Alignment.CenterHorizontally   // centrado horizontal
                    ){
                        // TITULO DEL DIALOGO
                        Text(
                            text = "Foto de perfil",             // texto
                            color = Color.White,                 // color del texto
                            style = TextStyle(
                                fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                                fontSize = 22.sp,                // tamaño del texto
                                fontWeight = FontWeight.Bold     // texto en negrita
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))  // separación vertical entre componentes

                        // instrucciones de uso para el usuario
                        Text(
                            text = "Pellizca para hacer zoom • Arrastra para mover",   // texto
                            color = Color.White,                 // color del texto
                            style = TextStyle(
                                fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                                fontSize = 11.sp                 // tamaño del texto
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))  // separación vertical entre componentes

                        // contenedor oscuro que enmarca visualmente el área de recorte
                        Box(
                            modifier = Modifier.fillMaxWidth()      // se ocupa el ancho disponible
                                .height((diametroCirculo + 48).dp)  // altura
                                .background(color = Color(0xFF060E1F), shape = RoundedCornerShape(size = 16.dp)), // color de fondo con bordes redondeados
                            contentAlignment = Alignment.Center  // contenido centrado
                        ){
                            // contenedor oscuro que enmarca visualmente el área de recorte
                            Box(
                                modifier = Modifier.fillMaxWidth()      // se ocupa el ancho disponible
                                    .height((diametroCirculo + 48).dp)  // altura
                                    .background(color = Color(0xFF060E1F), shape = RoundedCornerShape(size = 16.dp)),  // color de fondo con bordes redondeados
                                contentAlignment = Alignment.Center     // se centra el contenido
                            ) {
                                // contenedor con el tamaño del círculo de recorte que recibe los gestos y muestra la imagen
                                Box(
                                    modifier = Modifier.size(diametroCirculo.dp)  // tamaño del círculo (dp)
                                        .pointerInput(key1 = Unit) {
                                            // se detectan los gestos del zoom y arrastre
                                            detectTransformGestures { _, pan, zoom, _ ->
                                                // se actualiza la escala respetando los límites mínimos y máximos
                                                val nuevaEscala = (escala * zoom).coerceIn(
                                                    escalaMinima,
                                                    escalaMaxima
                                                )
                                                escala = nuevaEscala

                                                // se actualiza el desplazamiento limitando al área válida de la imagen
                                                desplazamiento = limitarDesplazamiento(
                                                    nuevoDesplazamiento = desplazamiento + pan,
                                                    escala = nuevaEscala,
                                                    rotacion = rotacion
                                                )
                                            }
                                        }
                                        .drawWithContent {
                                            // dimensiones del canvas (coinciden con el tamaño del contenedor)
                                            val canvasW = size.width
                                            val canvasH = size.height
                                            val cx = canvasW / 2f                // centro horizontal del canvas
                                            val cy = canvasH / 2f                // centro vertical del canvas
                                            val radius = diametroCirculoPx / 2f  // radio del círculo de recorte

                                            drawIntoCanvas { composeCanvas ->
                                                val nativeCanvas = composeCanvas.nativeCanvas  // canvas nativo de Android para operaciones avanzadas

                                                // se usa una Matrix de Android para aplicar la escala, rotación y desplazamiento en un solo paso
                                                val matrix = Matrix()
                                                matrix.postTranslate(-bitmapOriginal!!.width / 2f, -bitmapOriginal.height / 2f)  // se centra la imagen en el origen
                                                matrix.postScale(escala, escala)    // se aplica la escala actual
                                                matrix.postRotate(rotacion)         // se aplica la rotación actual
                                                matrix.postTranslate(cx + desplazamiento.x, cy + desplazamiento.y)  // se traslada al centro del canvas con desplazamiento del usuario

                                                nativeCanvas.drawBitmap(
                                                    bitmapOriginal,
                                                    matrix,
                                                    Paint().apply {
                                                        isFilterBitmap = true  // interpolación bilineal para mejor calidad al escalar
                                                        isAntiAlias = true     // suavizado de bordes
                                                    }
                                                )

                                                // se usa para que no se borren píxeles del canvas principal en lugar del overlay
                                                val saveCount = nativeCanvas.saveLayer(0f, 0f, canvasW, canvasH, Paint())

                                                // se dibuja el rectángulo oscuro semitransparente sobre toda el área
                                                nativeCanvas.drawRect(
                                                    0f, 0f, canvasW, canvasH,
                                                    Paint().apply {
                                                        color = android.graphics.Color.argb(180, 6, 14, 31)  // negro azulado semiopaco
                                                        isAntiAlias = true
                                                    }
                                                )

                                                // se borra el círculo central del overlay (efecto ventana circular)
                                                nativeCanvas.drawCircle(
                                                    cx, cy, radius,
                                                    Paint().apply {
                                                        isAntiAlias = true
                                                        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)  // elimina todos los píxeles en esa zona del layer
                                                    }
                                                )

                                                nativeCanvas.restoreToCount(saveCount)  // se restaura el layer aplicando el overlay correctamente sobre la imagen

                                                // borde sólido que delimita el área de recorte
                                                nativeCanvas.drawCircle(
                                                    cx, cy, radius,
                                                    Paint().apply {
                                                        color = "#017DB2".toColorInt()  // azul principal de la aplicación
                                                        style = Paint.Style.STROKE
                                                        strokeWidth = 3f
                                                        isAntiAlias = true
                                                    }
                                                )

                                                // pequeñas marcas blancas en los cuadrantes del círculo (estilo herramienta de recorte profesional)
                                                val guideLen = 18f  // longitud de cada trazo de guía en píxeles

                                                val guidePaint = android.graphics.Paint().apply {
                                                    color = android.graphics.Color.argb(200, 255, 255, 255)  // blanco semitransparente
                                                    style = Paint.Style.STROKE
                                                    strokeWidth = 2.5f
                                                    isAntiAlias = true
                                                    strokeCap = Paint.Cap.ROUND  // extremos redondeados
                                                }

                                                // se dibujan las guías en los 4 puntos diagonales del círculo (45°, 135°, 225°, 315°)
                                                listOf(45.0, 135.0, 225.0, 315.0).forEach { angDeg ->
                                                    val angRad = Math.toRadians(angDeg)
                                                    val px = cx + radius * cos(x = angRad).toFloat()  // coordenada X del punto en el borde del círculo
                                                    val py = cy + radius * sin(x = angRad).toFloat()  // coordenada Y del punto en el borde del círculo
                                                    val normX = cos(x = angRad).toFloat()             // componente X del vector normal en ese punto
                                                    val normY = sin(x = angRad).toFloat()             // componente Y del vector normal en ese punto

                                                    // trazo horizontal de la guía
                                                    nativeCanvas.drawLine(px, py, px - normX * guideLen, py, guidePaint)

                                                    // trazo vertical de la guía
                                                    nativeCanvas.drawLine(px, py, px, py - normY * guideLen, guidePaint)
                                                }
                                            }
                                        }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))  // separación vertical entre componentes

                        // BOTÓN DE ROTACIÓN 90º
                        Button(
                            onClick = {
                                rotacion = (rotacion + 90f) % 360f  // se incrementa 90° y se limita a [0, 360)

                                // se recalculan los límites del desplazamiento para la nueva rotación
                                desplazamiento = limitarDesplazamiento(
                                    nuevoDesplazamiento = desplazamiento,
                                    escala = escala,
                                    rotacion = rotacion
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF622D),  // color de fondo del botón
                                contentColor = Color.White           // color del texto del botón
                            ),
                            shape = RoundedCornerShape(size = 14.dp), // bordes redondeados
                            modifier = Modifier.height(42.dp)         // altura
                                .width(180.dp)                        // ancho
                        ){
                            // icono
                            Icon(
                                imageVector = Icons.Default.Rotate90DegreesCw,  // icono
                                contentDescription = "Girar imagen 90 grados",  // descripción del icono
                                modifier = Modifier.size(18.dp)                 // tamaño del icono
                            )

                            Spacer(modifier = Modifier.width(8.dp)) // separación horizontal entre componentes

                            Text(
                                text = "Girar 90º",                  // texto
                                style = TextStyle(
                                    fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                                    fontSize = 14.sp,                // tamaño del texto
                                    fontWeight = FontWeight.Bold     // texto en negrita
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))  // separación vertical entre componentes

                        Row(
                            modifier = Modifier.fillMaxWidth(),                  // se ocupa el ancho disponible
                            horizontalArrangement = Arrangement.spacedBy(12.dp)  // separación horizontal entre botones
                        ){
                            // BOTÓN DE CANCELAR
                            Button(
                                onClick = cancelar,  // se cierra el modal
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFB71C1C),  // color de fondo del botón
                                    contentColor = Color.White           // color del texto del botón
                                ),
                                shape = RoundedCornerShape(size = 12.dp), // bordes redondeados
                                modifier = Modifier.weight(1f)   // ocupa la mitad del ancho disponible
                                    .height(50.dp),              // alto
                            ){
                                // icono
                                Icon(
                                    imageVector = Icons.Default.Close,  // icono
                                    contentDescription = "Cancelar",    // descripción del icono
                                    modifier = Modifier.size(18.dp)     // tamaño del icono
                                )

                                Spacer(modifier = Modifier.width(8.dp)) // separación horizontal entre componentes

                                Text(
                                    text = "Cancelar",                   // texto
                                    style = TextStyle(
                                        fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                                        fontSize = 14.sp,                // tamaño del texto
                                        fontWeight = FontWeight.Bold     // texto en negrita
                                    )
                                )
                            }

                            // BOTÓN DE CONFIRMAR
                            Button(
                                onClick = {
                                    // se genera el string en Base64 de la imagen recortada con la transformación actual
                                    val base64 = recortarBitmap(
                                        bitmap = bitmapOriginal!!,
                                        escala = escala,
                                        desplazamiento = desplazamiento,
                                        rotacion = rotacion,
                                        areaRecorte = diametroCirculoPx.toInt()
                                    )

                                    confirmar(base64)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF0D47A1),  // color de fondo del botón
                                    contentColor = Color.White           // color del texto del botón
                                ),
                                shape = RoundedCornerShape(size = 12.dp), // bordes redondeados
                                modifier = Modifier.weight(1f)   // ocupa la mitad del ancho disponible
                                    .height(50.dp),              // alto
                            ){
                                // icono
                                Icon(
                                    imageVector = Icons.Default.Check,  // icono
                                    contentDescription = "Confirmar",   // descripción del icono
                                    modifier = Modifier.size(18.dp)     // tamaño del icono
                                )

                                Spacer(modifier = Modifier.width(8.dp)) // separación horizontal entre componentes

                                Text(
                                    text = "Usar foto",                  // texto
                                    style = TextStyle(
                                        fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                                        fontSize = 14.sp,                // tamaño del texto
                                        fontWeight = FontWeight.Bold     // texto en negrita
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}
