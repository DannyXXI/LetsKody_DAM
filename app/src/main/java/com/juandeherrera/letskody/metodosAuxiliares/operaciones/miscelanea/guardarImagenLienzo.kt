package com.juandeherrera.letskody.metodosAuxiliares.operaciones.miscelanea

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.core.graphics.createBitmap
import com.juandeherrera.letskody.clasesAuxiliares.TrazoLienzo

// función auxiliar para guardar el dibujo realizado en el juego de Draw Arena en la galería del dispositivo
fun guardarDibujoDispositivo(context: Context, trazos: List<TrazoLienzo>, lienzoSize: IntSize, colorFondo: Color) {

    if (lienzoSize.width == 0 || lienzoSize.height == 0) return  // si el lienzo no tiene tamaño todavía, no se haca nada

    val bitmap = createBitmap(width = lienzoSize.width, height = lienzoSize.height)  // se crea un objeto Bitmap con el tamaño del lienzo

    val lienzo = Canvas(bitmap)  // se crea un lienzo con las mismas dimensiones que el lienzo original

    // se pinta el fondo del lienzo
    lienzo.drawColor(
        android.graphics.Color.argb(
            (colorFondo.alpha * 255).toInt(),
            (colorFondo.red * 255).toInt(),
            (colorFondo.green * 255).toInt(),
            (colorFondo.blue * 255).toInt()
        )
    )

    // se configura el pincel para que reproduzca los trazos que hemos dibujado
    val pincel = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    // se reproduce cada trazo guardado sobre el objeto Bitmap
    trazos.forEach { trazo ->
        // se comprueba que halla al menos dos puntos para dibujar una línea
        if (trazo.puntos.size >= 2) {
            // se indica el color del pincel
            pincel.color = android.graphics.Color.argb(
                (trazo.color.alpha * 255).toInt(),
                (trazo.color.red * 255).toInt(),
                (trazo.color.green * 255).toInt(),
                (trazo.color.blue * 255).toInt()
            )

            pincel.strokeWidth = trazo.grosor  // se indica el grosor

            // se configura la ruta de trazado del pincel
            val ruta = Path().apply {
                moveTo(trazo.puntos.first().x,trazo.puntos.first().y)    // punto inicial
                trazo.puntos.drop(n = 1).forEach { lineTo(it.x, it.y) }  // se conectan los puntos como una línea
            }

            lienzo.drawPath(ruta, pincel)  // se dibuja el trazado en el canvas
        }
    }

    // se indican los metadatos del archivo en el que se va a guardar el lienzo
    val metadatos = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "dibujo_${System.currentTimeMillis()}.png")
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/LetsKody")
    }

    // Insertamos el archivo y escribimos el bitmap en el stream resultante
    val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, metadatos)

    // se muestra un mensaje al usuario si el envío del archivo a la galería fue exitoso
    uri?.let {
        context.contentResolver.openOutputStream(it)?.use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }
        Toast.makeText(context, "¡Dibujo guardado en galería!", Toast.LENGTH_SHORT).show()
    } ?: Toast.makeText(context, "Error al guardar el dibujo", Toast.LENGTH_SHORT).show()
}