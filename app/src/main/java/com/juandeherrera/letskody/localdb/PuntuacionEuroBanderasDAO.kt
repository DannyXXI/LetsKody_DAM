package com.juandeherrera.letskody.localdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

// OPERACIONES QUE SE REALIZARAN EN LA TABLA DE PUNTUACIONES DEL JUEGO DE EURO-BANDERAS
@Dao
interface PuntuacionEuroBanderasDAO {
    // OBTENER TODAS LAS PUNTUACIONES
    @Query(value = "SELECT * FROM ${Estructura.PuntuacionEuroBanderas.TABLE_NAME}")
    fun getListaPuntuacionesEuroBanderas(): List<PuntuacionEuroBanderasData>

    // OBTENER UNA DETERMINADA PUNTUACIÓN
    @Query(value = "SELECT * FROM ${Estructura.PuntuacionEuroBanderas.TABLE_NAME} WHERE ${Estructura.PuntuacionEuroBanderas.USUARIO} = :uidUsuario")
    fun getPuntuacionEuroBanderas(uidUsuario: String): PuntuacionEuroBanderasData?

    // OBTENER LA POSICION (NUMERO ENTERO) SEGUN LOS PUNTOS Y SI EMPATA POR TIEMPO, EN CASO DE QUE EXISTA, SINO ES NULL

    // AGREGAR UNA NUEVA PUNTUACIÓN
    @Insert
    fun nuevaPuntuacionEuroBanderas(puntuacionEuroBanderasData: PuntuacionEuroBanderasData)

    // AGREGAR LA LISTA DE PUNTUACIONES DE FIREBASE A LOCAL
    @Insert
    fun agregarPuntuacionesEuroBanderas(puntuaciones: List<PuntuacionEuroBanderasData>)

    // ACTUALIZAR UNA PUNTUACIÓN
    @Update
    fun actualizarPuntuacionesEuroBanderas(puntuacionEuroBanderasData: PuntuacionEuroBanderasData)

    // ELIMINAR UNA PUNTUACIÓN
    @Query(value = "DELETE FROM ${Estructura.PuntuacionEuroBanderas.TABLE_NAME} WHERE ${Estructura.PuntuacionEuroBanderas.USUARIO} = :uidUsuario")
    fun eliminarPuntuacionEuroBanderas(uidUsuario: String): Int

    // ELIMINAR TODAS LAS PUNTUACIONES DE LA BASE DE DATOS LOCAL
    @Query(value = "DELETE FROM ${Estructura.PuntuacionEuroBanderas.TABLE_NAME}")
    fun eliminarTodasPuntuacionesEuroBanderas(): Int
}