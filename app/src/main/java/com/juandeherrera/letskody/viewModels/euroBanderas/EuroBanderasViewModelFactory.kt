package com.juandeherrera.letskody.viewModels.euroBanderas

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.juandeherrera.letskody.localdb.AppDB

// clase que se encarga de crear correctamente el ViewModel con los parámetros necesarios
class EuroBanderasViewModelFactory (private val db: AppDB, private val context: Context) : ViewModelProvider.Factory {

    // función que se encarga de crear el ViewModel
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        // se comprueba que se está pidiendo crear el ViewModel del juego Euro-banderas
        if (modelClass.isAssignableFrom(EuroBanderasViewModel::class.java)) {
            return EuroBanderasViewModel(db = db, context = context) as T
        }
        else {
            throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
    }
}