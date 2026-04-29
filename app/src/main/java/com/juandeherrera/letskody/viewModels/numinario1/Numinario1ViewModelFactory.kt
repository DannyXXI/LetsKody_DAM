package com.juandeherrera.letskody.viewModels.numinario1

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// clase que se encarga de crear correctamente el ViewModel con los parámetros necesarios
class Numinario1ViewModelFactory(private val context: Context) : ViewModelProvider.Factory{

    // función que se encarga de crear el ViewModel
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        // se comprueba que se está pidiendo crear el ViewModel del juego Numinario 1
        if (modelClass.isAssignableFrom(Numinario1ViewModel::class.java)) {
            return Numinario1ViewModel(context = context) as T
        }
        else {
            throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
    }
}