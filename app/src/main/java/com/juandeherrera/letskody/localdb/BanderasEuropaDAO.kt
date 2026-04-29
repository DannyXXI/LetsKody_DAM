package com.juandeherrera.letskody.localdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

// OPERACIONES QUE SE REALIZARAN EN LA TABLA DE LAS BANDERAS DE EUROPA
@Dao
interface BanderasEuropaDAO {
    // OBTENER TODAS LAS BANDERAS DE EUROPA
    @Query(value = "SELECT * FROM ${Estructura.BanderasEuropa.TABLE_NAME}")
    fun getListaBanderasEuropa(): List<BanderasEuropaData>

    // AGREGAR LA LISTA DE BANDERAS DE FIREBASE A LOCAL
    @Insert
    fun agregarBanderas(banderas: List<BanderasEuropaData>)

    // ELIMINAR TODAS LAS BANDERAS DE LA BASE DE DATOS LOCAL
    @Query(value = "DELETE FROM ${Estructura.BanderasEuropa.TABLE_NAME}")
    fun eliminarTodasBanderasEuropa(): Int
}