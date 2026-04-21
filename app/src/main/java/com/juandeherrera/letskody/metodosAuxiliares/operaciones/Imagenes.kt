package com.juandeherrera.letskody.metodosAuxiliares.operaciones

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.core.graphics.scale
import com.juandeherrera.letskody.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.URL

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

// función auxiliar para descargar la foto de perfil de Google (se hace en hilo secundario porque es una operación de red)
suspend fun descargarFotoGoogleBase64 (context: Context, url: String) : String {
    try {
        // se descarga el objeto Bitmap desde la URL de la foto de perfil de Google
        val bitmap = withContext(Dispatchers.IO){
            BitmapFactory.decodeStream(URL(url).openStream())
        }

        val bitmapRedimensionado = bitmap.scale(width = 300, height = 300) // se redimensiona la imagen a 300x300px para reducir tamaño

        val outputStream = ByteArrayOutputStream() // se crea el flujo de salida en memoria para almacenar los bytes de la imagen comprimida

        // se comprime la imagen redimensionada a formato JPEG con calidad del 75% para reducir tamaño sin perder demasiada calidad visual
        bitmapRedimensionado.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)

        val bytes = outputStream.toByteArray()  // se convierte el flujo de salida en un array de bytes

        // se comprueba si la imagen no supera los 512 KB (se manda la imagen por defecto)
        return if (bytes.size > 512 * 1024) {
            convertirImagenDefectoBase64(context = context, recursoId = R.drawable.kody_orange)
        } else {
            // se devuelve los bytes en un string codificado en base64
            Base64.encodeToString(bytes, Base64.DEFAULT)
        }
    }
    catch (ex: Exception) {
        // si falla la descarga se muestra un mensaje en la terminal y se usa la imagen por defecto
        println("Error al descargar la foto de Google: ${ex.message}")
        return convertirImagenDefectoBase64(context = context, recursoId = R.drawable.kody_orange)
    }
}

// función auxiliar para convertir la imagen por defecto (recurso del sistema) en la imagen de perfil del usuario
fun convertirImagenDefectoBase64(context: Context, recursoId: Int): String {
    // se descodifica el recurso en un objeto Bitmap
    val bitmap = BitmapFactory.decodeResource(context.resources, recursoId)

    val bitmapRedimensionado = bitmap.scale(width = 300, height = 300) // se redimensiona la imagen a 300x300px para reducir tamaño

    val outputStream = ByteArrayOutputStream() // se crea el flujo de salida en memoria para almacenar los bytes de la imagen comprimida

    // se comprime la imagen redimensionada a formato JPEG con calidad del 75% para reducir tamaño sin perder demasiada calidad visual
    bitmapRedimensionado.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)

    // se devuelve los bytes en un string codificado en base64
    return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
}



