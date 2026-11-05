package edu.uadeapps.feriapp.data.repository

import edu.uadeapps.feriapp.data.mapper.toDomain
import edu.uadeapps.feriapp.data.remote.FeriAppApi
import edu.uadeapps.feriapp.data.remote.dto.ReservaRequestDto
import edu.uadeapps.feriapp.data.remote.toErrorApp
import edu.uadeapps.feriapp.domain.model.Reserva
import edu.uadeapps.feriapp.domain.repository.ReservaRepository
import kotlin.coroutines.cancellation.CancellationException

/**
 * Las reservas requieren conexión (RNF06): si el servidor no confirma, no se guarda nada
 * en el dispositivo y el puesto conserva su estado anterior (RNF07).
 */
class ReservaRepositoryImpl(private val api: FeriAppApi) : ReservaRepository {

    override suspend fun reservar(puestoId: Long): Result<Reserva> =
        llamar { api.reservar(ReservaRequestDto(puestoId)).toDomain() }

    override suspend fun obtenerMisReservas(): Result<List<Reserva>> =
        llamar { api.getMisReservas().map { it.toDomain() } }

    override suspend fun obtenerReserva(reservaId: Long): Result<Reserva> =
        llamar { api.getReserva(reservaId).toDomain() }

    private suspend fun <T> llamar(bloque: suspend () -> T): Result<T> =
        try {
            Result.success(bloque())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e.toErrorApp())
        }
}
