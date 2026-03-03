package com.juandeherrera.letskody.metodosAuxiliares.operaciones

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.core.graphics.scale
import java.io.ByteArrayOutputStream

// metodo auxiliar para convertir la URI de la imagen de la galería en un string codificado en Base64
fun convertirURIenBase64(uriImagen: Uri, context: Context, error: (String) -> Unit): String {

    // se abre el flujo de datos desde el sistema de archivos/galería para leer la imagen a partir de su URI
    val inputStream = context.contentResolver.openInputStream(uriImagen)

    // se transforma el flujo de bytes en un objeto Bitmap (representación de la imagen en la memoria)
    val originalBitmap = BitmapFactory.decodeStream(inputStream)

    val bitmapRedimensionado = originalBitmap.scale(width = 300, height = 300) // se redimensiona la imagen a 300x300px para reducir tamaño

    val outputStream = ByteArrayOutputStream() // se crea el flujo de salida en memoria para almacenar los bytes de la imagen comprimida

    // se comprime la imagen redimensionada a formato JPEG con calidad del 75% para reducir tamaño sin perder demasiada calidad visual
    bitmapRedimensionado.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)

    val bytes = outputStream.toByteArray()  // se convierte el flujo de salida en un array de bytes

    // se comprueba si la imagen no supera los 512 KB
    if (bytes.size > 512 * 1024) {
        error("La imagen es demasiado grande. Máx 512 KB")  // mensaje de error si supera el límite permitido
        return ""                                           // se devuelve un string vacío
    }
    else {
        // se devuelve los bytes en un string codificado en base64
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }
}