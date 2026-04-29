package com.juandeherrera.letskody.metodosAuxiliares.componentes.juegos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.formatearSegundos
import com.juandeherrera.letskody.viewModels.numinario1.EstadoNuminario1
import com.juandeherrera.letskody.viewModels.numinario1.Pista

// función auxiliar para cargar la parte superior de la pantalla del juego (cronometro y puntuación)
@Composable
fun CabeceraJuego(tiempoRestante: Int, puntosActuales: Int, fuenteTipografica: FontFamily) {
    // columna con la parte superior del juego
    Column(
        modifier = Modifier.fillMaxWidth() // se ocupa el ancho disponible
            .padding(horizontal = 16.dp, vertical = 8.dp)  // padding interno
    ){
        // contenido de la columna (fila)
        Row(
            modifier = Modifier.fillMaxWidth(),  // se ocupa el ancho disponible
            horizontalArrangement = Arrangement.SpaceBetween,  // espaciado horizontal entre elementos
            verticalAlignment = Alignment.CenterVertically     // centrado vertical
        ){
            // el temporizador se pone en rojo en los últimos 15 segundos
            val colorTemporizador = if (tiempoRestante <= 15) Color(0xFFC62828) else Color(0xFF1565C0)

            // tarjeta con el temporizador
            ElevatedCard(
                shape = RoundedCornerShape(size = 12.dp),                              // bordes redondeados
                colors = CardDefaults.cardColors(containerColor = colorTemporizador)   // color de fondo de la tarjeta
            ){
                Text(
                    text = formatearSegundos(segundos = tiempoRestante),   // texto
                    color = Color.White,       // color del texto
                    style = TextStyle(
                        fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                        fontSize = 20.sp,                // tamaño del texto
                        fontWeight = FontWeight.Bold    // texto en negrita
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp) // padding interno
                )
            }

            // tarjeta con la puntuación
            ElevatedCard(
                shape = RoundedCornerShape(size = 12.dp),                              // bordes redondeados
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0))   // color de fondo de la tarjeta
            ){
                Text(
                    text = "⭐ $puntosActuales",   // texto
                    color = Color.White,       // color del texto
                    style = TextStyle(
                        fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                        fontSize = 20.sp,                // tamaño del texto
                        fontWeight = FontWeight.Bold    // texto en negrita
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp) // padding interno
                )
            }
        }
    }
}

// función auxiliar para cargar la tarjeta con la operación actual a resolver
@Composable
fun TarjetaOperacion(numero1: Int, numero2: Int, esSuma: Boolean, fuenteTipografica: FontFamily) {

    val simbolo = if (esSuma) "+" else "-"  // variable para obtener el simbolo de la operacion

    // tarjeta que contiene la operacion
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()   // se ocupa el ancho disponible
            .padding(horizontal = 24.dp),    // padding en los laterales horizontales
        shape = RoundedCornerShape(size = 12.dp),                          // bordes redondeados
        colors    = CardDefaults.cardColors(containerColor = Color.White), // color de fondo de la tarjeta
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)    // sombreado de elevación
    ){
        Text(
            text = "$numero1  $simbolo  $numero2  =  ?",   // texto
            color = Color.Black,                           // color del texto
            style = TextStyle(
                fontFamily = fuenteTipografica,   // fuente tipográfica del texto
                fontSize = 38.sp,                 // tamaño de fuente del texto
                fontWeight = FontWeight.Bold,     // texto en negrita
                textAlign = TextAlign.Center      // alinear en el centro
            ),
            modifier = Modifier.fillMaxWidth()                     // se ocupa el ancho disponible
                .padding(vertical = 32.dp, horizontal = 16.dp),    // padding interno
        )
    }
}

// función auxiliar para cargar el campo de texto de respuesta y el botón de comprobarla
@Composable
fun CampoRespuesta(valor: String, pista: Pista?, fuenteTipografica: FontFamily, cambiar: (String) -> Unit, comprobar: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth()   // se ocupa el ancho disponible
            .padding(horizontal = 24.dp),    // padding en los laterales horizontales
        horizontalAlignment =  Alignment.CenterHorizontally  // centrado horizontal
    ){
        // se pide el nombre del usuario
        OutlinedTextField(
            value = valor,  // valor del campo de texto
            onValueChange = { nuevoValor ->
                if (nuevoValor.all { it.isDigit() }) cambiar(nuevoValor)  // se filtra para que solo admitan los dígitos
            },
            label = {
                Text(
                    text = "Tu respuesta",             // texto
                    color = Color.Black,               // color del texto
                    fontFamily = fuenteTipografica     // fuente tipográfica
                )
            },
            modifier = Modifier.fillMaxWidth(),   // se ocupa el ancho disponible
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color(0xFF1565C0),   // borde del campo cuando está activo
                unfocusedIndicatorColor = Color(0xFF1565C0), // borde del campo cuando no está activo
                focusedContainerColor = Color(0xFFC2DAFD),         // color del fondo del campo cuando está activo
                unfocusedContainerColor = Color(0xFFC2DAFD),       // color del fondo del campo cuando no está activo
                cursorColor = Color(0xFF1565C0)              // color del cursor en el campo de texto
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,            // tipo de teclado para el campo de texto
                capitalization = KeyboardCapitalization.None,  // no se capitaliza (no se trata las mayúsculas) el texto del usuario
                autoCorrectEnabled = false,                    // se inhabilita el autocorrector mientras escribe el usuario
                imeAction = ImeAction.Done,                    // se habilita la acción de aceptar el valor desde el teclado
                showKeyboardOnFocus = false                    // no se muestra el teclado cuando el campo recibe el foco
            ),
            keyboardActions = KeyboardActions(
                onDone = { if (valor.isNotBlank()) comprobar() }  // se comprueba el valor introducido por el usuario
            ),
            textStyle = TextStyle(
                color = Color.Black,              // color del texto introducido
                fontFamily = fuenteTipografica,   // fuente tipográfica del texto introducido
                fontSize = 24.sp,                 // tamaño de fuente del texto introducido
                fontWeight = FontWeight.Bold,     // texto introducido en negrita
                textAlign = TextAlign.Center      // alinear en el centro
            ),
            singleLine = true // el campo de texto solo puede tener una sola línea de texto (con TAB se pasa al siguiente campo)
        )

        Spacer(modifier = Modifier.height(10.dp))  // separación vertical entre componentes

        // pista orientativa para el usuario que se muestra debajo del campo de texto para que el usuario la vea aunque tenga el teclado desplegado
        if (pista != null) {
            // texto que se muestra como pista al usuario
            val textoPista = when (pista) {
                Pista.MAYOR -> "El resultado es menor al introducido."
                Pista.MENOR -> "El resultado es mayor al introducido."
            }

            // PISTA QUE SE MUESTRA AL USUARIO
            Text(
                text = textoPista,                     // texto
                color = Color(0xFFC62828),             // color del texto
                style = TextStyle(
                    fontFamily = fuenteTipografica,    // fuente tipográfica del texto
                    fontSize = 16.sp,                  // tamaño de fuente del texto
                    fontWeight = FontWeight.SemiBold,  // texto en negrita
                    textAlign = TextAlign.Center       // alinear en el centro
                ),
                modifier = Modifier.fillMaxWidth()     // se ocupa el ancho disponible
            )
        }

        Spacer(modifier = Modifier.height(10.dp))  // separación vertical entre componentes

        // BOTÓN DE COMPROBAR EL RESULTADO
        Button(
            onClick = comprobar,                  // al pulsarlo se comprueba el valor introducido por el usuario
            enabled = valor.isNotBlank(),         // se puede pulsar solamente si hay algo escrito en el campo de texto
            modifier = Modifier.fillMaxWidth()    // se ocupa el ancho disponible
                .height(50.dp),                   // altura del botón
            shape = RoundedCornerShape(size = 14.dp),   // bordes redondeados
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1565C0),     // color de fondo del botón
                contentColor = Color.White              // color del texto del botón
            )
        ){
            Text(
                text = "Comprobar",                   // texto del botón
                style = TextStyle(
                    fontFamily = fuenteTipografica,   // fuente tipográfica del texto
                    fontSize = 18.sp,                 // tamaño de fuente del texto
                    fontWeight = FontWeight.Bold,     // texto en negrita
                )
            )
        }
    }
}

// función auxiliar que agrupa la pantalla del juego mientras el usuario está jugándolo
@Composable
fun PantallaJugando(estado: EstadoNuminario1.Jugando, respuesta: String, fuenteTipografica: FontFamily, cambiar: (String) -> Unit, comprobar: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),  // ocupa el espacio disponible
        horizontalAlignment = Alignment.CenterHorizontally,  // centrado horizontal
        verticalArrangement = Arrangement.spacedBy(32.dp)    // espaciado vertical
    ){
        // temporizador y puntuación
        CabeceraJuego(
            tiempoRestante = estado.tiempoRestante,
            puntosActuales = estado.puntos,
            fuenteTipografica = fuenteTipografica
        )

        // tarjeta con la operación matemática
        TarjetaOperacion(
            numero1 = estado.numero1,
            numero2 = estado.numero2,
            esSuma = estado.esSuma,
            fuenteTipografica = fuenteTipografica
        )

        // campo de texto para la respuesta, texto para pista y botón de comprobar resultado
        CampoRespuesta(
            valor = respuesta,
            pista = estado.pista,
            fuenteTipografica = fuenteTipografica,
            cambiar = cambiar,
            comprobar = comprobar
        )
    }
}