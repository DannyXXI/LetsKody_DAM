package com.juandeherrera.letskody

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.juandeherrera.letskody.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU) // solo se permite Android 13 o superior (API 33+)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // se bloquea la rotación de la pantalla (no recomendado por Android)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT

        // se permite que la aplicación cree su contenido debajo de las barras del sistema
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // controlador para mostrar/ocultar las barras del sistema
        val controller = WindowInsetsControllerCompat(window, window.decorView)

        controller.hide(WindowInsetsCompat.Type.systemBars())  // se oculta todas las barras del sistema (superior e inferior)

        // se define el comportamiento de las barras ocultas -> el usuario las muestra temporalmente al deslizar el borde de la pantalla
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        enableEdgeToEdge()
        setContent {
            AppNavigation()  // maneja la navegación y mostrar la primera pantalla
        }
    }
}