package com.juandeherrera.letskody.metodosAuxiliares.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// función auxiliar para cargar el tipo de snackbarHost (mensaje de error o éxito) en las pantallas
@Composable
fun MensajeSnackbarHost(snackbarHostState: SnackbarHostState, fuenteTipografica: FontFamily, tipo: String = "error") {

    // variable para el color de fondo del Snackbar
    val colorFondo = when(tipo) {
        "success" -> Color(0xFF38803A)
        "error" -> Color(0xFFB00020)
        else -> Color.Gray
    }

    // variable para el color del círculo de la X
    val colorCirculo = when(tipo) {
        "success" -> Color(0xFF5DA660)
        "error" -> Color(0xFFFF5252)
        else -> Color.LightGray
    }

    // componente que gestiona y muestra los snackbars en la pantalla
    SnackbarHost(
        hostState = snackbarHostState, // estado que controla los mensajes que se mostrarán
        // se define como se va a crear el snackbar
        snackbar = { snackbarData ->
            // componente visual del snackbar
            Snackbar(
                modifier = Modifier.padding(8.dp),  // padding externo para separarlo de los bordes de la pantalla
                containerColor = colorFondo,             // color de fondo del snackbar
                // acción para cerrar manualmente el snackbar
                dismissAction = {
                    // botón pulsable para cerrar el snackbar actual
                    TextButton(
                        onClick = { snackbarData.dismiss() } // al pulsar el botón, se cierra el snackbar
                    ){
                        // contenedor para dibujar el círculo en la X
                        Box(
                            modifier = Modifier.size(24.dp)                  // tamaño del circulo
                                .background(colorCirculo, CircleShape),      // color de fondo y forma circular
                            contentAlignment = Alignment.Center              // contenido alineado en el centro del círculo
                        ){
                            // texto que representa la X de cerrar
                            Text(
                                text = "X",                     // texto
                                color = Color.White,            // color del texto
                                fontFamily = fuenteTipografica  // fuente tipográfica del texto
                            )
                        }
                    }
                }
            ){
                // texto principal del snackbar
                Text(
                    text = snackbarData.visuals.message, // mensaje que se quiere mostrar en el snackbar
                    fontFamily = fuenteTipografica       // fuente tipográfica del texto
                )
            }
        }
    )
}

// función auxiliar para mostrar las notificaciones usando el Snackbar
fun notificationSnackbar(scope: CoroutineScope, snackbarHostState: SnackbarHostState, mensaje: String) {
    scope.launch {
        val result = snackbarHostState.showSnackbar(
            message = mensaje,                 // mensaje de la notificación
            withDismissAction = true,          // muestra la X para cerrar la notificación
            duration = SnackbarDuration.Short  // duración de la notificación por defecto (4 segundos)
        )
    }
}