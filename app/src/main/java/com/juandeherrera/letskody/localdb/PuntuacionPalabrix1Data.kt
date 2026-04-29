package com.juandeherrera.letskody.localdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// se indica que la clase es una entidad que corresponde a una tabla en la base de datos, accediendo por el nombre
@Entity(
    tableName = Estructura.PuntuacionPalabrix1.TABLE_NAME
)
data class PuntuacionPalabrix1Data (
    // clave primaria autogenerada incremental cuyo valor inicial es 0
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Estructura.PuntuacionPalabrix1.ID)
    val idPuntuacion: Int = 0,

    // columna del uid de la puntuación en Firebase
    @ColumnInfo(name = Estructura.PuntuacionPalabrix1.UID_PUNTOS_PALABRIX1)
    val uidPuntosPalabrix1: String,

    // columna de la puntuación del usuario
    @ColumnInfo(name = Estructura.PuntuacionPalabrix1.PUNTOS)
    val puntos: Int,

    // columna con el tiempo (segundos) que ha tardado el usuario
    @ColumnInfo(name = Estructura.PuntuacionPalabrix1.TIEMPO)
    val tiempo: Int,

    // columna con el UID del usuario
    @ColumnInfo(name = Estructura.PuntuacionPalabrix1.USUARIO)
    val usuario: String,
)