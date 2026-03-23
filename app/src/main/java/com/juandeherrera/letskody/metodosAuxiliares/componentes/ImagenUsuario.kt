package com.juandeherrera.letskody.metodosAuxiliares.componentes

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.juandeherrera.letskody.localdb.UsuarioData

// función auxiliar para cargar la imagen del usuario
@Composable
fun ImagenUsuario(usuario: UsuarioData) {
    val bytesImagen = Base64.decode(usuario.fotoUsuario, Base64.DEFAULT)   // se obtiene el array de bytes de la imagen

    val bitmap = BitmapFactory.decodeByteArray(bytesImagen, 0, bytesImagen.size)  // se convierte la imagen en un objeto Bitmap

    // se muestra la imagen
    Image(
        bitmap = bitmap.asImageBitmap(),        // se lee el objeto Bitmap como una imagen
        contentDescription = "Foto de perfil",  // descripción de la imagen
        modifier = Modifier.size(170.dp)        // tamaño de la imagen
            .clip(CircleShape),       // forma circular
        contentScale = ContentScale.Crop   // forma de escalar la imagen para que ocupe el círculo completo
    )
}
