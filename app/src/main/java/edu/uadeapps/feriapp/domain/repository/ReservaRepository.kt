package edu.uadeapps.feriapp.domain.repository

import edu.uadeapps.feriapp.domain.model.Reserva

interface ReservaRepository {
    suspend fun reservar(puestoId: Long): Result<Reserva>
    suspend fun obtenerMisReservas(): Result<List<Reserva>>
    suspend fun obtenerReserva(reservaId: Long): Result<Reserva>
}
