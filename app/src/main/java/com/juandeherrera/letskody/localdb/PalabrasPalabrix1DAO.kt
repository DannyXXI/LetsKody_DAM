package com.juandeherrera.letskody.localdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

// OPERACIONES QUE SE REALIZARAN EN LA TABLA DE LAS PALABRAS DEL JUEGO PALABRIX 1
@Dao
interface PalabrasPalabrix1DAO {
    // OBTENER TODAS LAS PALABRAS
    @Query(value = "SELECT * FROM ${Estructura.PalabrasPalabrix1.TABLE_NAME}")
    fun getListaPalabras(): List<PalabrasPalabrix1Data>

    // AGREGAR LA LISTA DE PALABRAS DE FIREBASE A LOCAL
    @Insert
    fun agregarPalabras(palabras: List<PalabrasPalabrix1Data>)

    // ELIMINAR TODAS LAS PALABRAS DE LA BASE DE DATOS LOCAL
    @Query(value = "DELETE FROM ${Estructura.PalabrasPalabrix1.TABLE_NAME}")
    fun eliminarTodasPalabras(): Int
}