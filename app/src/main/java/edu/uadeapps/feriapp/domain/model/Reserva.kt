package edu.uadeapps.feriapp.domain.model

data class Reserva(
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
    val precioPuesto: Double
)
