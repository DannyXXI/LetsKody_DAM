package com.juandeherrera.letskody.localdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// se indica que la clase es una entidad que corresponde a una tabla en la base de datos, accediendo por el nombre
@Entity(
    tableName = Estructura.PuntuacionNuminario1.TABLE_NAME
)
data class PuntuacionNuminario1Data (
    // clave primaria autogenerada incremental cuyo valor inicial es 0
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Estructura.PuntuacionNuminario1.ID)
    val idPuntuacion: Int = 0,

    // columna del uid del registro de puntuación en Firebase
    @ColumnInfo(name = Estructura.PuntuacionNuminario1.UID_PUNTOS_NUMINARIO1)
    val uidPuntosNuminario1: String,

    // columna de la puntuación del usuario
    @ColumnInfo(name = Estructura.PuntuacionNuminario1.PUNTOS)
    val puntos: Int,

    // columna con los fallos que ha tenido el usuario
    @ColumnInfo(name = Estructura.PuntuacionNuminario1.FALLOS)
    val fallos: Int,

    // columna con el UID del usuario
    @ColumnInfo(name = Estructura.PuntuacionNuminario1.USUARIO)
    val usuario: String,
)