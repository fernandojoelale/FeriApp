package edu.uadeapps.feriapp.data.local.db

import androidx.room.Entity

/** Colecciones de ferias guardadas: lista pública, ferias del organizador y detalle consultado. */
object Coleccion {
    const val PUBLICAS = "PUBLICAS"
    const val MIAS = "MIAS"
    const val DETALLE = "DETALLE"
}

@Entity(tableName = "ferias", primaryKeys = ["id", "coleccion"])
data class FeriaEntity(
    val id: Long,
    val coleccion: String,
    val nombre: String,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val direccion: String,
    val zona: String,
    val latitud: Double,
    val longitud: Double,
    val rubro: String,
    val precioPuesto: Double,
    val puestosLibres: Int,
    val totalPuestos: Int,
    val organizadorNombre: String,
    val actualizadoEn: Long
)

@Entity(tableName = "puestos", primaryKeys = ["id"])
data class PuestoEntity(
    val id: Long,
    val feriaId: Long,
    val numero: Int,
    val posX: Int,
    val posY: Int,
    val estado: String,
    val actualizadoEn: Long
)

@Entity(tableName = "reservas", primaryKeys = ["id"])
data class ReservaEntity(
    val id: Long,
    val comprobante: String,
    val puestoId: Long,
    val numeroPuesto: Int,
    val feriaId: Long,
    val feriaNombre: String,
    val feriaFecha: String,
    val horaInicio: String,
    val horaFin: String,
    val direccion: String,
    val zona: String,
    val latitud: Double,
    val longitud: Double,
    val precioPuesto: Double,
    val actualizadoEn: Long
)
