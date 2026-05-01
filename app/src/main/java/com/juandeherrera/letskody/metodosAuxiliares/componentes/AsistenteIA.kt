package com.juandeherrera.letskody.metodosAuxiliares.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.SendHorizontal
import com.juandeherrera.letskody.R
import com.juandeherrera.letskody.clasesAuxiliares.MensajeChat

// función auxiliar para cargar el mensaje de bienvenida del bot al usuario
@Composable
private fun MensajeBienvenida (fuenteTipografica: FontFamily) {
    Column(
        modifier = Modifier.fillMaxWidth()   // se ocupa el ancho disponible
            .padding(horizontal = 12.dp, vertical = 24.dp), // padding interno
        horizontalAlignment = Alignment.CenterHorizontally  // centrado horizontal
    ){
        // avatar circular que representa al asistente
        Image(
            painter = painterResource(id = R.drawable.kody_serviciotecnico),  // ruta al recurso (imagen)
            contentDescription = "KodyBot",                                   // descripción de la imagen
            modifier = Modifier.size(80.dp)    // tamaño de la imagen
                .clip(CircleShape)             // forma circular
                .background(color = Color(0xFF9FF3DB), shape = CircleShape),  // color de fondo con forma circular
            contentScale = ContentScale.Fit   // forma de escalar la imagen para que ocupe el círculo completo
        )

        Spacer(Modifier.height(8.dp))  // separación horizontal entre componentes

        // mensaje de bienvenida
        Text(
            text = "¡Hola! Soy tu asistente virtual.\nTe ayudare con cualquier duda que tengas.",   // texto
            color = Color.Black,                 // color del texto
            style = TextStyle(
                fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                fontSize = 13.sp,                // tamaño del texto
                textAlign = TextAlign.Center,   // texto justificado
                lineHeight = 20.sp               // interlineado
            )
        )
    }
}

// función auxiliar para indicar en la pantalla de conversación que el bot está escribiendo (tres puntos suspensivos)
@Composable
private fun IndicadorEscribiendo() {
    Row(
        modifier = Modifier.fillMaxWidth()  // se ocupa el ancho disponible
            .padding(start = 8.dp, end = 60.dp),          // padding
        verticalAlignment = Alignment.Top   // centrado vertical superior
    ){
        // avatar circular que representa al asistente
        Image(
            painter = painterResource(id = R.drawable.kody_serviciotecnico),  // ruta al recurso (imagen)
            contentDescription = "KodyBot",                                   // descripción de la imagen
            modifier = Modifier.size(40.dp)    // tamaño de la imagen
                .clip(CircleShape)             // forma circular
                .background(color = Color(0xFF9FF3DB), shape = CircleShape),  // color de fondo con forma circular
            contentScale = ContentScale.Crop   // forma de escalar la imagen para que ocupe el círculo completo
        )

        Spacer(Modifier.height(8.dp))  // separación horizontal entre componentes

        // burbuja con los tres puntos grises
        Box(
            modifier = Modifier.clip(shape = RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp)) // bordes redondeados
                .background(Color(0xFF9CFF91))                   // color de fondo
                .padding(horizontal = 16.dp, vertical = 12.dp),  // padding en los laterales horizontales y verticales
            contentAlignment = Alignment.Center                  // contenido centrado
        ){
            // tres puntos negros circulares separados entre sí
            Row(
                verticalAlignment = Alignment.CenterVertically,     // centrado vertical
                horizontalArrangement = Arrangement.spacedBy(4.dp)  // espaciado horizontal entre elementos
            ){
                repeat(times = 3) {
                    Box(
                        modifier = Modifier.size(7.dp)        // tamaño del punto
                            .clip(CircleShape)                // forma circular
                            .background(color = Color.Black)  // color de fondo
                    )
                }
            }
        }
    }
}

// función auxiliar para cargar una burbuja de un mensaje del chat
@Composable
private fun BurbujaMensaje(mensaje: MensajeChat, fuenteTipografica: FontFamily) {
    // se comprueba a quien pertenece el mensaje
    if (mensaje.esUsuario) {
        Column(
            modifier = Modifier.fillMaxWidth(),   // se ocupa el ancho disponible
            horizontalAlignment = Alignment.End   // centrado horizontal a la derecha
        ){
            // burbuja del mensaje
            Box(
                modifier = Modifier.widthIn(min = 60.dp, max = 230.dp)   // ancho mínimo y máximo de la burbuja
                    .clip(shape = RoundedCornerShape(topStart = 18.dp, topEnd = 4.dp, bottomStart = 18.dp, bottomEnd = 18.dp))  // bordes redondeados
                    .background(Color(0xFF9EE7FD))                 // verde claro WhatsApp
                    .padding(horizontal = 12.dp, vertical = 8.dp)  // padding en los laterales horizontales y verticales
            ){
                // mensaje de la burbuja
                Text(
                    text = mensaje.texto,   // texto
                    color = Color.Black,                 // color del texto
                    style = TextStyle(
                        fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                        fontSize = 14.sp,                // tamaño del texto
                        textAlign = TextAlign.Start,     // texto alineado a la izquierda
                        lineHeight = 20.sp               // interlineado
                    )
                )
            }

            Spacer(Modifier.height(4.dp))  // separación vertical entre componentes (burbujas)
        }
    }
    else {
        Row(
            modifier = Modifier.fillMaxWidth()  // se ocupa el ancho disponible
                .padding(end = 60.dp),          // padding a la derecha
            verticalAlignment = Alignment.Top   // centrado vertical superior
        ){
            // avatar circular que representa al asistente
            Image(
                painter = painterResource(id = R.drawable.kody_serviciotecnico),  // ruta al recurso (imagen)
                contentDescription = "KodyBot",                                   // descripción de la imagen
                modifier = Modifier.size(40.dp)    // tamaño de la imagen
                    .clip(CircleShape)             // forma circular
                    .background(color = Color(0xFF9FF3DB), shape = CircleShape),  // color de fondo con forma circular
                contentScale = ContentScale.Fit   // forma de escalar la imagen para que ocupe el círculo completo
            )

            Spacer(Modifier.width(6.dp))  // separación horizontal entre componentes

            // burbuja del mensaje
            Box(
                modifier = Modifier.widthIn(min = 60.dp, max = 230.dp)   // ancho mínimo y máximo de la burbuja
                    .clip(shape = RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp))  // bordes redondeados
                    .background(Color(0xFF9CFF91))                 // verde claro WhatsApp
                    .padding(horizontal = 12.dp, vertical = 8.dp)  // padding en los laterales horizontales y verticales
            ){
                // mensaje de la burbuja
                Text(
                    text = mensaje.texto,   // texto
                    color = Color.Black,                 // color del texto
                    style = TextStyle(
                        fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                        fontSize = 14.sp,                // tamaño del texto
                        textAlign = TextAlign.Justify,   // texto justificado
                        lineHeight = 20.sp               // interlineado
                    )
                )
            }

        }

        Spacer(Modifier.height(4.dp))  // separación vertical entre componentes (burbujas)
    }
}

// función auxiliar para cargar la tarjeta de conversación con el bot
@Composable
fun TarjetaConversacion (mensajes: List<MensajeChat>, cargando: Boolean, fuenteTipografica: FontFamily, modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()  // mantiene el estado del scroll entre recomposiciones

    // bloque que se ejecuta cada vez que se añade un mensaje o cambia el estado de carga
    LaunchedEffect(key1 = mensajes.size, key2 = cargando) {
        val totalItems = mensajes.size + if (cargando) 1 else 0  // se comprueba si se está cargando el mensaje

        if (totalItems > 0) {
            listState.animateScrollToItem(index = totalItems - 1) // se activa el scroll animado al último item
        }
    }

    // tarjeta que actuará como el contenedor de los mensajes del chat
    ElevatedCard(
        colors = CardDefaults.cardColors(containerColor = Color.White),  // color de fondo
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp), // sombreado de elevación de la tarjeta
        shape = RoundedCornerShape(size = 20.dp),                        // bordes redondeados
        modifier = modifier                                              // se aplican los modificadores
    ){
        // columna con scroll
        LazyColumn(
            state = listState,                   // estado del scroll de la columna
            modifier = Modifier.fillMaxWidth(),  // ocupa el ancho disponible
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 8.dp), // padding interno de la lista
            verticalArrangement = Arrangement.spacedBy(4.dp)                    // separación vertical entre burbujas
        ){
            // si la lista de mensajes está vacía y no hay nada cargando, se muestra el mensaje de bienvenida
            if (mensajes.isEmpty() && !cargando) {
                item {
                    MensajeBienvenida(fuenteTipografica = fuenteTipografica)
                }
            }

            // se muestra la lista de todos los mensajes del chat
            items(items = mensajes) { mensaje ->
                BurbujaMensaje(mensaje = mensaje, fuenteTipografica = fuenteTipografica)
            }

            // si el bot está procesando el mensaje del usuario, se muestra el indicador
            if (cargando) {
                item {
                    IndicadorEscribiendo()
                }
            }
        }
    }
}

// función auxiliar para cargar la tarjeta de entrada de mensajes del usuario
@Composable
fun TarjetaEntrada(cargando: Boolean, fuenteTipografica: FontFamily, enviar: (String) -> Unit) {
    var texto by remember { mutableStateOf(value = "") }  // variable de estado el texto que escriba el usuario

    // tarjeta que actuará como el contenedor de la parte de entrada de mensajes al chat
    ElevatedCard(
        colors = CardDefaults.cardColors(containerColor = Color.White),  // color de fondo
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp), // sombreado de elevación de la tarjeta
        shape = RoundedCornerShape(size = 20.dp),                        // bordes redondeados
        modifier = Modifier.fillMaxWidth()                 // ocupa el ancho disponible
            .padding(horizontal = 8.dp, vertical = 6.dp)   // padding interno
    ){
        Row(
            modifier = Modifier.fillMaxWidth()                  // ocupa el ancho disponible
                .padding(horizontal = 12.dp, vertical = 6.dp),  // padding interno en los laterales horizontales y verticales
            verticalAlignment = Alignment.CenterVertically      // centrado vertical
        ) {
            // campo de texto
            OutlinedTextField(
                value = texto,  // valor del campo de texto
                onValueChange = { if (it.length <= 100) { texto = it } },  // se limita la longitud a 100 caracteres
                placeholder = {
                    Text(
                        text = "Escribe tu duda...",    // texto
                        color = Color.Gray,             // color del texto
                        fontFamily = fuenteTipografica  // fuente tipográfica del texto
                    )
                },
                modifier = Modifier.weight(1f),  // se ocupa el ancho disponible
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
                    imeAction = ImeAction.Send,                   // se habilita la acción de ir al siguiente campo de texto desde el teclado
                    showKeyboardOnFocus = true                    // se muestra el teclado cuando el campo recibe el foco
                ),
                keyboardActions = KeyboardActions (
                    onSend = {
                        // se envía el texto solamente si no está vacío y el bot no está procesando
                        if (texto.isNotBlank() && !cargando) {
                            enviar(texto.trim())
                            texto = ""    // se limpia el campo de texto tras el envío
                        }
                    }
                ),
                textStyle = TextStyle(
                    color = Color.Black,              // color del texto introducido
                    fontFamily = fuenteTipografica    // fuente tipográfica del texto introducida
                )
            )

            Spacer(Modifier.width(8.dp))  // separación horizontal entre componentes

            // botón circular del envío de mensajes
            IconButton(
                onClick = {
                    // se envía el texto solamente si no está vacío y el bot no está procesando
                    if (texto.isNotBlank() && !cargando) {
                        enviar(texto.trim())
                        texto = ""    // se limpia el campo de texto tras el envío
                    }
                },
                enabled = texto.isNotBlank() && !cargando, // se habilita si texto no está vacío y el bot no está procesando
                modifier = Modifier.size(40.dp)            // tamaño del botón
                    .clip(CircleShape)                     // forma circular
                    .background(color = if (texto.isNotBlank() && !cargando) Color(0xFF017DB2) else Color.Gray) // color de fondo
            ) {
                // icono
                Icon(
                    imageVector = Lucide.SendHorizontal,   // icono
                    contentDescription = "Enviar mensaje", // descripción del icono
                    tint = Color.White,                    // color del icono
                    modifier = Modifier.size(20.dp)        // tamaño del icono

                )
            }
        }
    }
}