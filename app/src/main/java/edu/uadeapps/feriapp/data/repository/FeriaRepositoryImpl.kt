package edu.uadeapps.feriapp.data.repository

import edu.uadeapps.feriapp.data.local.FeriasCache
import edu.uadeapps.feriapp.data.mapper.toDomain
import edu.uadeapps.feriapp.data.remote.FeriAppApi
import edu.uadeapps.feriapp.domain.Feria
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

/** Lista de ferias y si salió del servidor o de la copia guardada en el dispositivo. */
data class FeriasCargadas(val ferias: List<Feria>, val desdeCache: Boolean)

class FeriaRepository(
    private val api: FeriAppApi,
    private val cache: FeriasCache? = null
) {

    suspend fun obtenerFerias(): Result<List<Feria>> = runCatching {
        // Llama a la API y transforma cada FeriaDto en un objeto Feria
        api.getFerias().map { dto -> dto.toDomain() }
    }

    /**
     * Intenta el servidor y guarda el resultado. Si no hay conexión usa la última lista guardada;
     * si tampoco hay copia, devuelve el error original.
     */
    suspend fun obtenerFeriasConCache(): Result<FeriasCargadas> {
        try {
            val dtos = api.getFerias()
            try {
                cache?.guardar(dtos)
            } catch (e: Exception) {
                // Si no se puede guardar la copia, no afecta a la lista que ya llegó.
            }
            return Result.success(FeriasCargadas(dtos.map { it.toDomain() }, desdeCache = false))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val guardadas = if (e is IOException) cache?.leer() else null
            return if (guardadas != null) {
                Result.success(FeriasCargadas(guardadas.map { it.toDomain() }, desdeCache = true))
            } else {
                Result.failure(e)
            }
        }
    }
}
