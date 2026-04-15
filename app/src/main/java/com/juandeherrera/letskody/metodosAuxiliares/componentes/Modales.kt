package com.juandeherrera.letskody.metodosAuxiliares.componentes

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.juandeherrera.letskody.clasesAuxiliares.ResultadoJuego
import com.juandeherrera.letskody.metodosAuxiliares.componentes.juegos.FilaResultado
import com.juandeherrera.letskody.metodosAuxiliares.componentes.juegos.FilaTiempoYPenalizacion
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.formatearSegundos

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
fun ModalEliminarCuenta(context: Context, fuenteTipografica: FontFamily, password: TextFieldState, passVisible: Boolean, mostrarPassword: () -> Unit, cerrar: () -> Unit, enviar: () -> Unit) {
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
                                password.text.length < 8 -> {
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

// función auxiliar para cargar el modal con la puntuación del usuario en el juego Euro-banderas
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalPuntuacionEuroBanderas(resultado: ResultadoJuego, fuenteTipografica: FontFamily, repetir: () -> Unit, guardarYsalir: () -> Unit) {
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

