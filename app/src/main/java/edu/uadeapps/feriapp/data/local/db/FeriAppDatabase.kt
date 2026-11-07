package edu.uadeapps.feriapp.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/** Base local (Room): copia de ferias, puestos y reservas para consultar sin conexión. */
@Database(
    entities = [FeriaEntity::class, PuestoEntity::class, ReservaEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FeriAppDatabase : RoomDatabase() {
    abstract fun feriaDao(): FeriaDao
    abstract fun puestoDao(): PuestoDao
    abstract fun reservaDao(): ReservaDao

    companion object {
        fun crear(context: Context): FeriAppDatabase =
            Room.databaseBuilder(context.applicationContext, FeriAppDatabase::class.java, "feriapp.db")
                // Es solo una copia de lo que tiene el servidor: si cambia el esquema se vuelve a descargar.
                .fallbackToDestructiveMigration(true)
                .build()
    }
}
