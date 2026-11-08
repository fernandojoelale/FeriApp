package com.feriapp.backend.dto

import com.feriapp.backend.model.Reserva

/** Una reserva vista por el organizador de la feria: quién reservó qué puesto. */
data class ReservaDeFeriaDto(
    val reservaId: Long,
    val comprobante: String,
    val puestoId: Long,
    val numeroPuesto: Int,
    val ferianteNombre: String,
    val ferianteEmail: String,
    val ferianteRubro: String?,
    val fechaReserva: String
)

fun Reserva.toReservaDeFeriaDto() = ReservaDeFeriaDto(
    reservaId = id,
    comprobante = "FA-${fechaReserva.year}-${id.toString().padStart(4, '0')}",
    puestoId = puesto.id,
    numeroPuesto = puesto.numero,
    ferianteNombre = feriante.nombre,
    ferianteEmail = feriante.email,
    ferianteRubro = feriante.rubro,
    fechaReserva = fechaReserva.toString()
)
