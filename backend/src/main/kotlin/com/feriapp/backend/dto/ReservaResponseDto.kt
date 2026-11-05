package com.feriapp.backend.dto

import com.feriapp.backend.model.Reserva
import java.time.LocalDateTime

data class ReservaResponseDto(
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
    val ferianteId: Long,
    val fechaReserva: LocalDateTime,
    val estado: String
)

fun Reserva.toDto() = ReservaResponseDto(
    id = id,
    comprobante = "FA-${fechaReserva.year}-${id.toString().padStart(4, '0')}",
    puestoId = puesto.id,
    numeroPuesto = puesto.numero,
    feriaId = feria.id,
    feriaNombre = feria.nombre,
    feriaFecha = feria.fecha,
    horaInicio = feria.horaInicio,
    horaFin = feria.horaFin,
    direccion = feria.direccion,
    zona = feria.zona,
    latitud = feria.latitud,
    longitud = feria.longitud,
    precioPuesto = feria.precioPuesto,
    ferianteId = feriante.id,
    fechaReserva = fechaReserva,
    estado = estado.name
)
