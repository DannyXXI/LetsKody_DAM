package com.juandeherrera.letskody.localdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// se indica que la clase es una entidad que corresponde a una tabla en la base de datos, accediendo por el nombre
@Entity(
    tableName = Estructura.PalabrasPalabrix1.TABLE_NAME
)
data class PalabrasPalabrix1Data (
    // clave primaria autogenerada incremental cuyo valor inicial es 0
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Estructura.PalabrasPalabrix1.ID)
    val idPalabra: Int = 0,

    // columna del uid de la palabra en Firebase
    @ColumnInfo(name = Estructura.PalabrasPalabrix1.UID_PALABRA)
    val uidPalabra: String,

    // columna de la palabra
    @ColumnInfo(name = Estructura.PalabrasPalabrix1.PALABRA)
    val palabra: String,

    // columna de la respuesta correcta
    @ColumnInfo(name = Estructura.PalabrasPalabrix1.RESPUESTA)
    val respuesta: String
)