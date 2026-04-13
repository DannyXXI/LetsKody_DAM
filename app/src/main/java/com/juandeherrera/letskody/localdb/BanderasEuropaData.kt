package com.juandeherrera.letskody.localdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// se indica que la clase es una entidad que corresponde a una tabla en la base de datos, accediendo por el nombre
@Entity(
    tableName = Estructura.BanderasEuropa.TABLE_NAME
)
data class BanderasEuropaData (
    // clave primaria autogenerada incremental cuyo valor inicial es 0
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Estructura.BanderasEuropa.ID)
    val idBandera: Int = 0,

    // columna de la URL de la imagen de la bandera
    @ColumnInfo(name = Estructura.BanderasEuropa.URL_BANDERA)
    val urlBandera: String,

    // columna de la primera opción para elegir
    @ColumnInfo(name = Estructura.BanderasEuropa.OPCION1)
    val opcion1: String,

    // columna de la segunda opción para elegir
    @ColumnInfo(name = Estructura.BanderasEuropa.OPCION2)
    val opcion2: String,

    // columna de la tercera opción para elegir
    @ColumnInfo(name = Estructura.BanderasEuropa.OPCION3)
    val opcion3: String,

    // columna de la cuarta opción para elegir
    @ColumnInfo(name = Estructura.BanderasEuropa.OPCION4)
    val opcion4: String,

    // columna de la opción correcta
    @ColumnInfo(name = Estructura.BanderasEuropa.OPCION1)
    val opcionCorrecta: String
)