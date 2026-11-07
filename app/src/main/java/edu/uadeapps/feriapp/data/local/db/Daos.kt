package edu.uadeapps.feriapp.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
abstract class FeriaDao {
    @Query("SELECT * FROM ferias WHERE coleccion = :coleccion ORDER BY fecha ASC, id ASC")
    abstract suspend fun listar(coleccion: String): List<FeriaEntity>

    @Query("SELECT * FROM ferias WHERE id = :id AND coleccion = :coleccion LIMIT 1")
    abstract suspend fun obtener(id: Long, coleccion: String): FeriaEntity?

    @Query("DELETE FROM ferias WHERE coleccion = :coleccion")
    abstract suspend fun borrarColeccion(coleccion: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertar(ferias: List<FeriaEntity>)

    /** Reemplaza toda la colección de una vez: o queda la lista nueva completa o queda la anterior. */
    @Transaction
    open suspend fun reemplazar(coleccion: String, ferias: List<FeriaEntity>) {
        borrarColeccion(coleccion)
        insertar(ferias)
    }

    /** Limpieza del caché: ferias que ya pasaron y en las que el usuario no tiene reservas. */
    @Query("DELETE FROM ferias WHERE fecha < :hoy AND id NOT IN (SELECT feriaId FROM reservas)")
    abstract suspend fun borrarVencidas(hoy: String)
}

@Dao
abstract class PuestoDao {
    @Query("SELECT * FROM puestos WHERE feriaId = :feriaId ORDER BY numero ASC")
    abstract suspend fun listarDeFeria(feriaId: Long): List<PuestoEntity>

    @Query("DELETE FROM puestos WHERE feriaId = :feriaId")
    abstract suspend fun borrarDeFeria(feriaId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertar(puestos: List<PuestoEntity>)

    @Transaction
    open suspend fun reemplazarDeFeria(feriaId: Long, puestos: List<PuestoEntity>) {
        borrarDeFeria(feriaId)
        insertar(puestos)
    }

    @Query("DELETE FROM puestos WHERE feriaId NOT IN (SELECT id FROM ferias)")
    abstract suspend fun borrarHuerfanos()
}

@Dao
abstract class ReservaDao {
    @Query("SELECT * FROM reservas ORDER BY feriaFecha ASC, id ASC")
    abstract suspend fun listar(): List<ReservaEntity>

    @Query("SELECT * FROM reservas WHERE id = :id LIMIT 1")
    abstract suspend fun obtener(id: Long): ReservaEntity?

    @Query("DELETE FROM reservas")
    abstract suspend fun borrarTodas()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertar(reserva: ReservaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertarTodas(reservas: List<ReservaEntity>)

    @Transaction
    open suspend fun reemplazarTodas(reservas: List<ReservaEntity>) {
        borrarTodas()
        insertarTodas(reservas)
    }
}
