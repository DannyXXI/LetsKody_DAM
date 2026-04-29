package com.juandeherrera.letskody.metodosAuxiliares.interfaz

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.juandeherrera.letskody.clasesAuxiliares.MomentoDelDia

// función auxiliar para mostrar el tipo mensaje según el tiempo de espera tras enviar el ticket al servicio técnico
fun mensajeTiempoRestante(msRestantes: Long): String {

    val segundos = (msRestantes / 1000).toInt()  // se obtienen los segundos restantes

    // se devuelve el tipo de mensaje en función del tiempo de espera
    when {
        segundos >= 120 -> {
            val minutos = segundos / 60  // se obtienen los minutos restantes

            return "Faltan $minutos minutos para enviar otro mensaje."
        }
        segundos >= 60 -> {
            return "Falta 1 minuto para enviar otro mensaje."
        }
        else -> {
            return "Falta menos de 1 minuto para enviar otro mensaje."
        }
    }
}

// función auxiliar para cargar el mensaje de bienvenida personalizado
@Composable
fun MensajeBienvenida(momento: MomentoDelDia, nombreUsuario: String, fuenteTipografica: FontFamily) {

    // mensaje de saludo
    val saludo = when (momento) {
        MomentoDelDia.MANANA -> "Buenos dias"
        MomentoDelDia.TARDE  -> "Buenas tardes"
        MomentoDelDia.NOCHE  -> "Buenas noches"
    }

    // color del texto
    val colorTexto = when (momento) {
        MomentoDelDia.MANANA -> Color(0xFF1A4A8A)
        MomentoDelDia.TARDE  -> Color(0xFF4A1A00)
        MomentoDelDia.NOCHE  -> Color(0xFFF0F4FF)
    }

    // columna con el mensaje completo
    Column(
        horizontalAlignment = Alignment.CenterHorizontally  // centrado horizontal
    ){
        Text(
            text = saludo,                             // texto
            color = colorTexto,                        // color del texto
            style = TextStyle(
                fontFamily = fuenteTipografica,        // fuente tipográfica
                fontSize = 32.sp,                      // tamaño de fuente
                textAlign = TextAlign.Center           // texto alineado en el centro
            )
        )

        Spacer(modifier = Modifier.height(10.dp))  // separación vertical entre componentes

        Text(
            text = nombreUsuario,                      // texto
            color = colorTexto,                        // color del texto
            style = TextStyle(
                fontFamily = fuenteTipografica,        // fuente tipográfica
                fontSize = 30.sp,                      // tamaño de fuente
                textAlign = TextAlign.Center,           // texto alineado en el centro
                fontWeight = FontWeight.Bold
            )
        )
    }
}