package com.juandeherrera.letskody.localdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

// OPERACIONES QUE SE REALIZARAN EN LA TABLA DE PUNTUACIONES DEL JUEGO DE PALABRIX 1
@Dao
interface PuntuacionPalabrix1DAO {
    // OBTENER TODAS LAS PUNTUACIONES
    @Query(value = "SELECT * FROM ${Estructura.PuntuacionPalabrix1.TABLE_NAME}")
    fun getListaPuntuacionesPalabrix1(): List<PuntuacionPalabrix1Data>

    // OBTENER UNA DETERMINADA PUNTUACIÓN
    @Query(value = "SELECT * FROM ${Estructura.PuntuacionPalabrix1.TABLE_NAME} WHERE ${Estructura.PuntuacionPalabrix1.USUARIO} = :uidUsuario")
    fun getPuntuacionPalabrix1(uidUsuario: String): PuntuacionPalabrix1Data?

    // OBTENER LAS PUNTUACIONES (NÚMERO ENTERO) SEGÚN LOS PUNTOS Y SI EMPATA POR TIEMPO
    @Query(value = "SELECT * FROM ${Estructura.PuntuacionPalabrix1.TABLE_NAME} ORDER BY ${Estructura.PuntuacionPalabrix1.PUNTOS} DESC, ${Estructura.PuntuacionPalabrix1.TIEMPO} ASC")
    fun getListaPuntuacionesPalabrix1Ordenada(): List<PuntuacionPalabrix1Data>

    // AGREGAR UNA NUEVA PUNTUACIÓN
    @Insert
    fun nuevaPuntuacionPalabrix1(puntuacionPalabrix1Data: PuntuacionPalabrix1Data)

    // AGREGAR LA LISTA DE PUNTUACIONES DE FIREBASE A LOCAL
    @Insert
    fun agregarPuntuacionesPalabrix1(puntuaciones: List<PuntuacionPalabrix1Data>)

    // ACTUALIZAR UNA PUNTUACIÓN
    @Update
    fun actualizarPuntuacionesPalabrix1(puntuacionPalabrix1Data: PuntuacionPalabrix1Data)

    // ELIMINAR UNA PUNTUACIÓN
    @Query(value = "DELETE FROM ${Estructura.PuntuacionPalabrix1.TABLE_NAME} WHERE ${Estructura.PuntuacionPalabrix1.USUARIO} = :uidUsuario")
    fun eliminarPuntuacionPalabrix1(uidUsuario: String): Int

    // ELIMINAR TODAS LAS PUNTUACIONES DE LA BASE DE DATOS LOCAL
    @Query(value = "DELETE FROM ${Estructura.PuntuacionPalabrix1.TABLE_NAME}")
    fun eliminarTodasPuntuacionesPalabrix1(): Int
}