package com.juandeherrera.letskody.localdb

import androidx.room.Database
import androidx.room.RoomDatabase

// clase que define una base de datos local sqlite que hereda de RoomDatabase (clase para la base de datos Room)
@Database(
    // se establecen las tablas que conforman la base de datos
    // se indica la version, necesaria para migraciones y cambios realizados
    // se indica que la estructura de la bd se exportará a un archivo para mantener un historial de esquemas
    entities = [UsuarioData::class, BanderasEuropaData::class, PuntuacionEuroBanderasData::class, PuntuacionNuminario1Data::class], version = 1, exportSchema = true
)
abstract class AppDB: RoomDatabase() {
    // funciones abstractas que proporcionan instancias de acceso a las operaciones definidas en los DAO
    // se implementara estas funciones para proporcionar la instancia funcional de acceso a los datos

    abstract fun usuarioDao(): UsuarioDAO

    abstract fun banderasEuropaDao(): BanderasEuropaDAO

    abstract fun puntuacionEuroBanderasDao(): PuntuacionEuroBanderasDAO

    abstract fun puntuacionNuminario1Dao(): PuntuacionNuminario1DAO
}