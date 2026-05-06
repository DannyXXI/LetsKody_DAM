package com.juandeherrera.letskody.metodosAuxiliares.operaciones

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.util.Base64
import androidx.compose.ui.geometry.Offset
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import java.io.ByteArrayOutputStream

// función auxiliar que recorta el objetoBitMap final a partir de la transformación visual del recortador de imagen
fun recortarBitmap(bitmap: Bitmap, escala: Float, desplazamiento: Offset, rotacion: Float, areaRecorte: Int) : String {
    val outputBitmap = createBitmap(width = areaRecorte, height = areaRecorte)  // se crea un objeto Bitmap de salida cuadrado con el tamaño exacto del círculo de recorte

    val canvas = Canvas(outputBitmap)  // se crea el canvas

    val centro = areaRecorte / 2f   // centro del canvas de salida

    // se aplica la misma transformación que en la previsualización para obtener un resultado fiel
    val matrix = Matrix()                                                     // se genera la matriz
    matrix.postTranslate(-bitmap.width / 2f, -bitmap.height / 2f)  // se centra la imagen en el origen
    matrix.postScale(escala, escala)                               // se aplica la escala del usuario
    matrix.postRotate(rotacion)                                    // se aplica la rotación del usuario
    matrix.postTranslate(centro + desplazamiento.x, centro + desplazamiento.y)  // se traslada el centro del canvas con un desplazamiento

    // se dibuja el objeto bitmap transformado sobre el canvas de salida
    canvas.drawBitmap(bitmap, matrix, Paint().apply {
        isFilterBitmap = true  // interpolación bilineal para mejorar la calidad
        isAntiAlias = true     // suavizado de los bordes
    })

    val escaladoFinal = outputBitmap.scale(width = 300, height = 300)  // se redimensiona la imagen a 300x300px para reducir tamaño

    val outputStream = ByteArrayOutputStream() // se crea el flujo de salida en memoria para almacenar los bytes de la imagen comprimida

    // se comprime la imagen redimensionada a formato JPEG con calidad del 75% para reducir tamaño sin perder demasiada calidad visual
    escaladoFinal.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)

    // se devuelve los bytes en un string codificado en base64
    return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
}