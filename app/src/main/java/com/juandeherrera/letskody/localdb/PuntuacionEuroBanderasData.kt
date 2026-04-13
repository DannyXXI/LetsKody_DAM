package com.juandeherrera.letskody.localdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// se indica que la clase es una entidad que corresponde a una tabla en la base de datos, accediendo por el nombre
@Entity(
    tableName = Estructura.PuntuacionEuroBanderas.TABLE_NAME
)
class PuntuacionEuroBanderasData (
    // clave primaria autogenerada incremental cuyo valor inicial es 0
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Estructura.PuntuacionEuroBanderas.ID)
    val idPuntuacion: Int = 0,

    // columna del uid de la bandera europea en Firebase
    @ColumnInfo(name = Estructura.PuntuacionEuroBanderas.UID_PUNTOS_EUROBANDERAS)
    val uidPuntosEuroBanderas: String,

    // columna de la puntuación del usuario
    @ColumnInfo(name = Estructura.PuntuacionEuroBanderas.PUNTOS)
    val puntos: Int,

    // columna con el tiempo (segundos) que ha tardado el usuario
    @ColumnInfo(name = Estructura.PuntuacionEuroBanderas.TIEMPO)
    val tiempo: Int,

    // columna con el UID del usuario
    @ColumnInfo(name = Estructura.PuntuacionEuroBanderas.USUARIO)
    val usuario: String,
)