package edu.uadeapps.feriapp.data.repository

import edu.uadeapps.feriapp.data.mapper.toDomain
import edu.uadeapps.feriapp.data.remote.FeriAppApi
import edu.uadeapps.feriapp.data.remote.dto.CrearFeriaDto
import edu.uadeapps.feriapp.domain.Feria
import edu.uadeapps.feriapp.domain.model.NuevaFeria
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

    override suspend fun crearFeria(nueva: NuevaFeria): Result<Feria> =
        try {
            val dto = CrearFeriaDto(
                nombre = nueva.nombre,
                fecha = nueva.fecha,
                horaInicio = nueva.horaInicio,
                horaFin = nueva.horaFin,
                direccion = nueva.direccion,
                zona = nueva.zona,
                latitud = nueva.latitud,
                longitud = nueva.longitud,
                rubro = nueva.rubro,
                precioPuesto = nueva.precioPuesto,
                cantidadPuestos = nueva.cantidadPuestos
            )
            Result.success(api.crearFeria(dto).toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e.toErrorApp())
        }

    override suspend fun obtenerMisFerias(): Result<List<Feria>> =
        try {
            Result.success(api.getMisFerias().map { it.toDomain() })
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e.toErrorApp())
        }
}
