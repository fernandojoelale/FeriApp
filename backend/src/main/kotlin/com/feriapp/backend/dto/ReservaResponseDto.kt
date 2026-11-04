package com.feriapp.backend.dto

import com.feriapp.backend.model.Reserva
import java.time.LocalDateTime

data class ReservaResponseDto(
    val id: Long,
    val puestoId: Long,
    val numeroPuesto: Int,
    val feriaId: Long,
    val feriaNombre: String,
    val ferianteId: Long,
    val fechaReserva: LocalDateTime,
    val estado: String
)

fun Reserva.toDto() = ReservaResponseDto(
    id = id,
    puestoId = puesto.id,
    numeroPuesto = puesto.numero,
    feriaId = feria.id,
    feriaNombre = feria.nombre,
    ferianteId = feriante.id,
    fechaReserva = fechaReserva,
    estado = estado.name
)
