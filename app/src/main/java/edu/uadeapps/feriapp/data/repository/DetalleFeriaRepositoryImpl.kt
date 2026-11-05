package edu.uadeapps.feriapp.data.repository

import edu.uadeapps.feriapp.data.mapper.toDomain
import edu.uadeapps.feriapp.data.remote.FeriAppApi
import edu.uadeapps.feriapp.data.remote.toErrorApp
import edu.uadeapps.feriapp.domain.model.FeriaDetalle
import edu.uadeapps.feriapp.domain.repository.DetalleFeriaRepository
import kotlin.coroutines.cancellation.CancellationException

class DetalleFeriaRepositoryImpl(private val api: FeriAppApi) : DetalleFeriaRepository {

    override suspend fun obtenerDetalle(feriaId: Long): Result<FeriaDetalle> =
        try {
            Result.success(api.getFeriaDetalle(feriaId).toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e.toErrorApp())
        }
}
