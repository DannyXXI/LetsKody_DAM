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

    // OBTENER UNA DETERMINADA BANDERA
    @Query(value = "SELECT * FROM ${Estructura.BanderasEuropa.TABLE_NAME} WHERE ${Estructura.BanderasEuropa.UID_BANDERA} = :uidBandera")
    fun getBanderaEuropa(uidBandera: String): BanderasEuropaData?

    // AGREGAR UNA NUEVA BANDERA
    @Insert
    fun nuevaBanderaEuropa(banderaEuropaData: BanderasEuropaData)

    // AGREGAR LA LISTA DE BANDERAS DE FIREBASE A LOCAL
    @Insert
    fun agregarBanderas(banderas: List<BanderasEuropaData>)

    // ACTUALIZAR UNA BANDERA
    @Update
    fun actualizarBandera(banderaEuropaData: BanderasEuropaData)

    // ELIMINAR UNA BANDERA
    @Query(value = "DELETE FROM ${Estructura.BanderasEuropa.TABLE_NAME} WHERE ${Estructura.BanderasEuropa.UID_BANDERA} = :uidBandera")
    fun eliminarBandera(uidBandera: String): Int

    // ELIMINAR TODAS LAS BANDERAS DE LA BASE DE DATOS LOCAL
    @Query(value = "DELETE FROM ${Estructura.BanderasEuropa.TABLE_NAME}")
    fun eliminarTodasBanderasEuropa(): Int
}