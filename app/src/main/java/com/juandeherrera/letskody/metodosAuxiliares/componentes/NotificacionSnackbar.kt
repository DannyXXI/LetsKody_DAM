package com.juandeherrera.letskody.metodosAuxiliares.componentes

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// metodo auxiliar para mostrar las noticaciones usando el Snackbar
fun notificationSnackbar(scope: CoroutineScope, snackbarHostState: SnackbarHostState, mensaje: String) {
    scope.launch {
        val result = snackbarHostState.showSnackbar(
            message = mensaje,                // mensaje de la notificacion
            withDismissAction = true,         // muestra la X para cerrar la notificacion
            duration = SnackbarDuration.Short // duracion de la notificacion por defecto (4s)
        )
    }
}