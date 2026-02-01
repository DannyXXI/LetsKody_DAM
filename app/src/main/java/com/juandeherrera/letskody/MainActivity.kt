package com.juandeherrera.letskody

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.juandeherrera.letskody.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU) // solo se permite Android 13 o superior (API 33+)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavigation()  // se encarga de manegar la navegacion y mostrar la primera pantalla
        }
    }
}