package com.juandeherrera.letskody.metodosAuxiliares.componentes

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.juandeherrera.letskody.R
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

// función auxiliar para cargar la imagen del Kody flotando
@Composable
fun ImagenKodyFlotando(flotacionVertical: Float) {
    // contenedor con la imagen
    Box(
        modifier = Modifier.offset(y = flotacionVertical.dp), // desplazamiento que genera como si flotase suavemente
        contentAlignment = Alignment.Center                   // contenido centrado
    ){
        // IMAGEN
        Image(
            painter = painterResource(id = R.drawable.kody_welcome),  // recurso de la imagen
            contentDescription = "Mascota Kody dando la bienvenida",  // descripción de la imagen
            modifier = Modifier.size(200.dp)   // tamaño de la imagen
        )
    }
}