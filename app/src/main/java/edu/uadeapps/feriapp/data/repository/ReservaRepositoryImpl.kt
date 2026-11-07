package edu.uadeapps.feriapp.data.repository

import edu.uadeapps.feriapp.data.local.db.FeriAppDatabase
import edu.uadeapps.feriapp.data.local.db.toDomain
import edu.uadeapps.feriapp.data.local.db.toEntity
import edu.uadeapps.feriapp.data.mapper.toDomain
import edu.uadeapps.feriapp.data.remote.FeriAppApi
import edu.uadeapps.feriapp.data.remote.dto.ReservaRequestDto
import edu.uadeapps.feriapp.data.remote.toErrorApp
import edu.uadeapps.feriapp.domain.model.Reserva
import edu.uadeapps.feriapp.domain.repository.ReservaRepository
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

/**
 * Reservar requiere conexión (RNF06): solo si el servidor confirma, la reserva se guarda en Room.
 * Si falla, no se guarda nada y el puesto conserva su estado anterior (RNF07).
 * Consultar las reservas funciona sin conexión con la última copia guardada.
 */
class ReservaRepositoryImpl(
    private val api: FeriAppApi,
    private val db: FeriAppDatabase? = null
) : ReservaRepository {

    override suspend fun reservar(puestoId: Long): Result<Reserva> =
        try {
            val dto = api.reservar(ReservaRequestDto(puestoId))
            val ahora = System.currentTimeMillis()
            guardar { db?.reservaDao()?.insertar(dto.toEntity(ahora)) }
            Result.success(dto.toDomain().copy(actualizadoEn = ahora))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e.toErrorApp())
        }

    override suspend fun obtenerMisReservas(): Result<List<Reserva>> =
        try {
            val dtos = api.getMisReservas()
            val ahora = System.currentTimeMillis()
            guardar { db?.reservaDao()?.reemplazarTodas(dtos.map { it.toEntity(ahora) }) }
            Result.success(dtos.map { it.toDomain().copy(actualizadoEn = ahora) })
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val guardadas = if (e is IOException) leerGuardadas() else null
            if (guardadas != null) Result.success(guardadas) else Result.failure(e.toErrorApp())
        }

    override suspend fun obtenerReserva(reservaId: Long): Result<Reserva> =
        try {
            val dto = api.getReserva(reservaId)
            val ahora = System.currentTimeMillis()
            guardar { db?.reservaDao()?.insertar(dto.toEntity(ahora)) }
            Result.success(dto.toDomain().copy(actualizadoEn = ahora))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val guardada = if (e is IOException) {
                try { db?.reservaDao()?.obtener(reservaId)?.toDomain() } catch (ex: Exception) { null }
            } else null
            if (guardada != null) Result.success(guardada) else Result.failure(e.toErrorApp())
        }

    private suspend fun guardar(bloque: suspend () -> Unit) {
        try {
            bloque()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // La copia local es opcional: no debe romper una operación que el servidor ya confirmó.
        }
    }

    private suspend fun leerGuardadas(): List<Reserva>? =
        try {
            db?.reservaDao()?.listar()?.takeIf { it.isNotEmpty() }?.map { it.toDomain() }
        } catch (e: Exception) {
            null
        }
}
