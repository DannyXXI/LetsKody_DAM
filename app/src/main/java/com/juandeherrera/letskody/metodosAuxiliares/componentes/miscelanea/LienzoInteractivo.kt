package com.juandeherrera.letskody.metodosAuxiliares.componentes.miscelanea

import android.content.Context
import android.graphics.Path
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Eraser
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Pencil
import com.composables.icons.lucide.Save
import com.juandeherrera.letskody.clasesAuxiliares.TrazoLienzo
import com.juandeherrera.letskody.clasesAuxiliares.coloresBasicos
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.miscelanea.guardarDibujoDispositivo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// función auxiliar que carga en la pantalla el lienzo interactivo y sus herramientas
@Composable
fun LienzoInteractivo(context: Context, scope: CoroutineScope, fuenteTipografica: FontFamily) {
    var esGoma by remember { mutableStateOf(value = false) }  // comprueba si se elige dibujar (false) o borrar (true)

    var grosorTrazo by remember { mutableFloatStateOf(value = 8f) }  // grosor del trazo para dibujar o borrar

    var colorSeleccionado by remember { mutableStateOf(value = Color(0xFF212121)) }  // color activo para el lápiz

    var mostrarPaleta by remember { mutableStateOf(value = false) }  // controla la visibilidad de la paleta de colores

    val trazos = remember { mutableStateListOf<TrazoLienzo>() }    // lista de todos los trazos completos

    var puntosActuales by remember { mutableStateOf(value = listOf<Offset>()) } // puntos del trazo que se está dibujando ahora mismo

    var lienzoSize by remember { mutableStateOf(value = IntSize.Zero) } // tamaño real del Canvas (necesario para guardar el objeto Bitmap)

    Column(
        modifier = Modifier.fillMaxSize()  // se ocupa el espacio disponible
            .padding(vertical = 10.dp)     // padding en los laterales verticales
    ){
        // tarjeta con las herramientas
        Card(
            modifier = Modifier.fillMaxWidth()  // se ocupa el ancho disponible
                .wrapContentHeight(),
            shape = RoundedCornerShape(size = 16.dp),                         // bordes redondeados
            colors = CardDefaults.cardColors(containerColor = Color.White),   // color de fondo
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)   // sombre de elevación
        ){
            // fila con los iconos
            Row(
                modifier = Modifier.fillMaxWidth() // se ocupa el ancho disponible
                    .padding(horizontal = 12.dp, vertical = 10.dp),   // padding en los laterales
                horizontalArrangement = Arrangement.SpaceEvenly,      // espaciado horizontal proporcional
                verticalAlignment = Alignment.CenterVertically        // centrado vertical
            ){
                // icono del lápiz
                IconButton(
                    onClick = { esGoma = false }  // al pulsarlo se indica que se va a dibujar
                ){
                    Icon(
                        imageVector = Lucide.Pencil,   // icono
                        contentDescription = "Lápiz",  // descripción del icono
                        tint = if (!esGoma) Color(0xFF2979FF) else Color.Gray,  // color del icono si esta seleccionado
                        modifier = Modifier.size(26.dp)   // tamaño del icono
                    )
                }

                // icono de la goma de borrar
                IconButton(
                    onClick = { esGoma = true }  // al pulsarlo se indica que se va a borrar
                ){
                    Icon(
                        imageVector = Lucide.Eraser,            // icono
                        contentDescription = "Goma de borrar",  // descripción del icono
                        tint = if (esGoma) Color(0xFFE53935) else Color.Gray,  // color del icono si esta seleccionado
                        modifier = Modifier.size(26.dp)   // tamaño del icono
                    )
                }

                // slider del grosor de trazo
                Column(
                    modifier = Modifier.width(80.dp),  // ancho fijo
                    horizontalAlignment = Alignment.CenterHorizontally  // centrado horizontal
                ){
                    // se muestra el valor numérico actual encima del slider
                    Text(
                        text = "${grosorTrazo.toInt()} px",   // texto
                        color = Color.Gray,     // color del texto
                        style = TextStyle(
                            fontFamily = fuenteTipografica,  // fuente tipográfica del texto
                            fontSize = 12.sp                 // tamaño del texto
                        )
                    )
                    // slider
                    Slider(
                        value = grosorTrazo,   // valor actual del Slider
                        onValueChange = { grosorTrazo = it }, // se lee el cambio de valor
                        valueRange = 5f..40f,        // rangos de valores permitidos en el slider
                        modifier = Modifier.height(24.dp),  // altura fija
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF2979FF),                            // indicador
                            activeTrackColor = Color(0xFF2979FF),                      // parte izquierda (activa)
                            inactiveTrackColor = Color(0xFF2979FF).copy(alpha = 0.25f) // parte derecha (inactiva)
                        )
                    )
                }

                // círculo selector del color que muestra el color activo para dibujar
                Box(
                    modifier = Modifier.size(36.dp)      // tamaño del circulo
                        .clip(CircleShape)               // forma circular
                        .background(colorSeleccionado)   // color de fondo
                        .border(width = 2.dp, color = Color.DarkGray, shape = CircleShape)  // border y grosor circular
                        .clickable { mostrarPaleta = !mostrarPaleta }  // al pulsarlo se muestra la paleta de colores disponibles
                )

                // icono de guardado del dibujo en la galería del dispositivo
                IconButton(
                    onClick = {
                        // al pulsarlo se ejecuta en una corrutina el guardado del dibujo en la galería
                        scope.launch {
                            guardarDibujoDispositivo(
                                context = context,
                                trazos = trazos.toList(),
                                lienzoSize = lienzoSize,
                                colorFondo = Color.White
                            )
                        }
                    }
                ){
                    Icon(
                        imageVector = Lucide.Save,              // icono
                        contentDescription = "Guardar dibujo",  // descripción del icono
                        tint = Color(0xFF2979FF),               // color del icono
                        modifier = Modifier.size(26.dp)         // tamaño del icono
                    )
                }
            }

            // si se ha pulsado el selector, se muestra la paleta de colores
            if (mostrarPaleta) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth()  // se ocupa el ancho disponible
                        .padding(horizontal = 12.dp, vertical = 6.dp),  // padding en los laterales
                    horizontalArrangement = Arrangement.spacedBy(8.dp), // espaciado horizontal entre elementos
                    verticalArrangement   = Arrangement.spacedBy(8.dp)  // espaciado vertical entre elementos
                ){
                    // se recorre la paleta de colores básicos
                    coloresBasicos.forEach { (color) ->
                        Box(
                            modifier = Modifier.size(32.dp) // tamaño del circulo
                                .clip(CircleShape)  // se muestra en forma circular
                                .background(color)  // color de fondo
                                .border(
                                    width = if (colorSeleccionado == color) 3.dp else 1.dp,  // ancho del grosor del borde si está seleccionado el color
                                    color = if (colorSeleccionado == color) Color.Black else Color.Gray, // color del borde si está seleccionado el color
                                    shape = CircleShape  // borde de forma circular
                                )
                                .clickable {
                                    colorSeleccionado = color   // recoge el color como color seleccionado
                                    esGoma            = false   // vuelve al modo lápiz automáticamente
                                    mostrarPaleta     = false   // cierra la paleta de colores
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))  // espaciado vertical entre componentes
            }
        }

        Spacer(modifier = Modifier.height(10.dp))  // espaciado vertical entre componentes

        // tarjeta con las herramientas
        Card(
            modifier  = Modifier.fillMaxWidth()  // se ocupa el ancho disponible
                .weight(1f),                     // se ocupa el espacio restante
            shape = RoundedCornerShape(size = 16.dp),                         // bordes redondeados
            colors = CardDefaults.cardColors(containerColor = Color.White),   // color de fondo
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)   // sombre de elevación
        ){
            // canvas donde se dibujará
            Canvas(
                modifier = Modifier.fillMaxSize()  // se ocupa el espacio disponible
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset -> puntosActuales = listOf(offset) },                  // al tocar la pantalla, se inicia un nuevo trazo
                            onDrag = { change, _ -> puntosActuales = puntosActuales + change.position },  // mientras se arrastra el dedo, se añaden puntos al trazo
                            // al soltar el dedo, se guarda el trazo en la lista
                            onDragEnd = {
                                trazos.add(
                                    TrazoLienzo(
                                        puntos = puntosActuales,
                                        color = if (esGoma) Color.White else colorSeleccionado,  // la goma dibuja en blanco (borra visualmente)
                                        grosor = if (esGoma) grosorTrazo * 3f else grosorTrazo,  // la goma es tres veces más gruesa que el grosor elegido
                                        esGoma = esGoma
                                    )
                                )
                                puntosActuales = emptyList()  // se limpia el trazo actual para el siguiente
                            }
                        )
                    }
            ){
                lienzoSize = IntSize(size.width.toInt(), size.height.toInt())  // se guarda el tamaño real del Canvas para exportar el objeto Bitmap

                // se recorren los trazos guardados para dibujarlos
                trazos.forEach { trazo ->
                    // se comprueba que halla al menos dos puntos para dibujar una línea
                    if (trazo.puntos.size >= 2) {
                        // se configura la ruta de trazado del pincel
                        val ruta = Path().apply {
                            moveTo(trazo.puntos.first().x,trazo.puntos.first().y)    // punto inicial
                            trazo.puntos.drop(n = 1).forEach { lineTo(it.x, it.y) }  // se conectan los puntos como una línea
                        }

                        // se dibuja la ruta en el Canvas
                        drawPath(
                            path = ruta.asComposePath(),  // ruta
                            color = trazo.color,          // color
                            style = Stroke(
                                width = trazo.grosor,    // grosor definido por el usuario
                                cap = StrokeCap.Round,   // extremos redondeados que hace un trazo más natural
                                join = StrokeJoin.Round  // uniones redondeadas que hace no tenga picos
                            )
                        )
                    }
                }

                // se dibuja el trazo en tiempo real mientras el dedo está en contacto con la pantalla
                if (puntosActuales.size >= 2) {
                    // se configura la ruta de trazado del pincel
                    val ruta = Path().apply {
                        moveTo(puntosActuales.first().x,puntosActuales.first().y)    // punto inicial
                        puntosActuales.drop(n = 1).forEach { lineTo(it.x, it.y) }    // se conectan los puntos como una línea
                    }

                    // se dibuja la ruta en el Canvas
                    drawPath(
                        path = ruta.asComposePath(),  // ruta
                        color = if (esGoma) Color.White else colorSeleccionado, // color (blanco si se está borrando)
                        style = Stroke(
                            width = if (esGoma) grosorTrazo * 3f else grosorTrazo,  // grosor
                            cap = StrokeCap.Round,   // extremos redondeados que hace un trazo más natural
                            join = StrokeJoin.Round  // uniones redondeadas que hace no tenga picos
                        )
                    )
                }
            }
        }
    }
}

