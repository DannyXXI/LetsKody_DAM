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

    // OBTENER UNA PALABRA DETERMINADA
    @Query(value = "SELECT * FROM ${Estructura.PalabrasPalabrix1.TABLE_NAME} WHERE ${Estructura.PalabrasPalabrix1.UID_PALABRA} = :uidPalabra")
    fun getPalabra(uidPalabra: String): PalabrasPalabrix1Data?

    // AGREGAR UNA NUEVA PALABRA
    @Insert
    fun nuevaPalabra(palabraPalabrix1Data: PalabrasPalabrix1Data)

    // AGREGAR LA LISTA DE PALABRAS DE FIREBASE A LOCAL
    @Insert
    fun agregarPalabras(palabras: List<PalabrasPalabrix1Data>)

    // ACTUALIZAR UNA PALABRA
    @Update
    fun actualizarPalabra(palabraPalabrix1Data: PalabrasPalabrix1Data)

    // ELIMINAR UNA PALABRA
    @Query(value = "DELETE FROM ${Estructura.PalabrasPalabrix1.TABLE_NAME} WHERE ${Estructura.PalabrasPalabrix1.UID_PALABRA} = :uidPalabra")
    fun eliminarPalabra(uidPalabra: String): Int

    // ELIMINAR TODAS LAS PALABRAS DE LA BASE DE DATOS LOCAL
    @Query(value = "DELETE FROM ${Estructura.PalabrasPalabrix1.TABLE_NAME}")
    fun eliminarTodasPalabras(): Int
}