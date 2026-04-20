package com.juandeherrera.letskody.localdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

// OPERACIONES QUE SE REALIZARAN EN LA TABLA DE PUNTUACIONES DEL JUEGO DE NUMINARIO 1
@Dao
interface PuntuacionNuminario1DAO {
    // OBTENER TODAS LAS PUNTUACIONES
    @Query(value = "SELECT * FROM ${Estructura.PuntuacionNuminario1.TABLE_NAME}")
    fun getListaPuntuacionesNuminario1(): List<PuntuacionNuminario1Data>

    // OBTENER UNA DETERMINADA PUNTUACIÓN
    @Query(value = "SELECT * FROM ${Estructura.PuntuacionNuminario1.TABLE_NAME} WHERE ${Estructura.PuntuacionNuminario1.USUARIO} = :uidUsuario")
    fun getPuntuacionNuminario1(uidUsuario: String): PuntuacionNuminario1Data?

    // OBTENER LAS PUNTUACIONES (NÚMERO ENTERO) SEGÚN LOS PUNTOS Y SI EMPATA POR TIEMPO
    @Query(value = "SELECT * FROM ${Estructura.PuntuacionNuminario1.TABLE_NAME} ORDER BY ${Estructura.PuntuacionNuminario1.PUNTOS} DESC, ${Estructura.PuntuacionNuminario1.FALLOS} ASC")
    fun getListaPuntuacionesNuminario1Ordenada(): List<PuntuacionNuminario1Data>

    // AGREGAR UNA NUEVA PUNTUACIÓN
    @Insert
    fun nuevaPuntuacionNuminario1(puntuacionNuminario1Data: PuntuacionNuminario1Data)

    // AGREGAR LA LISTA DE PUNTUACIONES DE FIREBASE A LOCAL
    @Insert
    fun agregarPuntuacionesNuminario1(puntuaciones: List<PuntuacionNuminario1Data>)

    // ACTUALIZAR UNA PUNTUACIÓN
    @Update
    fun actualizarPuntuacionesNuminario1(puntuacionNuminario1Data: PuntuacionNuminario1Data)

    // ELIMINAR UNA PUNTUACIÓN
    @Query(value = "DELETE FROM ${Estructura.PuntuacionNuminario1.TABLE_NAME} WHERE ${Estructura.PuntuacionNuminario1.USUARIO} = :uidUsuario")
    fun eliminarPuntuacionNuminario1(uidUsuario: String): Int

    // ELIMINAR TODAS LAS PUNTUACIONES DE LA BASE DE DATOS LOCAL
    @Query(value = "DELETE FROM ${Estructura.PuntuacionNuminario1.TABLE_NAME}")
    fun eliminarTodasPuntuacionesNuminario1(): Int
}