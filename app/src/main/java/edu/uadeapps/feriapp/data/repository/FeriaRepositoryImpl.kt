package edu.uadeapps.feriapp.data.repository

import edu.uadeapps.feriapp.data.local.db.Coleccion
import edu.uadeapps.feriapp.data.local.db.FeriAppDatabase
import edu.uadeapps.feriapp.data.local.db.toDomain
import edu.uadeapps.feriapp.data.local.db.toEntity
import edu.uadeapps.feriapp.data.mapper.toDomain
import edu.uadeapps.feriapp.data.remote.FeriAppApi
import edu.uadeapps.feriapp.domain.Feria
import java.io.IOException
import java.time.LocalDate
import kotlin.coroutines.cancellation.CancellationException

/** Lista de ferias y si salió del servidor o de la copia guardada (Room). */
data class FeriasCargadas(
    val ferias: List<Feria>,
    val desdeCache: Boolean,
    val actualizadoEn: Long = 0L
)

/**
 * Offline First para la consulta: la lista se guarda en Room cada vez que llega del servidor y,
 * si no hay conexión, se muestra la última copia guardada.
 */
class FeriaRepository(
    private val api: FeriAppApi,
    private val db: FeriAppDatabase? = null
) {

    suspend fun obtenerFerias(): Result<List<Feria>> = runCatching {
        // Llama a la API y transforma cada FeriaDto en un objeto Feria
        api.getFerias().map { dto -> dto.toDomain() }
    }

    /** Lo último que quedó guardado en el celular (null si todavía no se descargó nada). */
    suspend fun leerGuardadas(): FeriasCargadas? {
        val filas = try {
            db?.feriaDao()?.listar(Coleccion.PUBLICAS)
        } catch (e: Exception) {
            null
        }
        if (filas.isNullOrEmpty()) return null
        return FeriasCargadas(filas.map { it.toDomain() }, desdeCache = true, actualizadoEn = filas.maxOf { it.actualizadoEn })
    }

    /**
     * Pide las ferias al servidor y las guarda en Room. Si no hay conexión usa la copia guardada;
     * si tampoco hay copia, devuelve el error original.
     */
    suspend fun obtenerFeriasConCache(): Result<FeriasCargadas> {
        try {
            val dtos = api.getFerias()
            val ahora = System.currentTimeMillis()
            try {
                db?.feriaDao()?.let { dao ->
                    dao.reemplazar(Coleccion.PUBLICAS, dtos.map { it.toEntity(Coleccion.PUBLICAS, ahora) })
                    dao.borrarVencidas(LocalDate.now().toString())
                    db.puestoDao().borrarHuerfanos()
                }
            } catch (e: Exception) {
                // Si no se puede guardar la copia, no afecta a la lista que ya llegó.
            }
            return Result.success(FeriasCargadas(dtos.map { it.toDomain() }, desdeCache = false, actualizadoEn = ahora))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val guardadas = if (e is IOException) leerGuardadas() else null
            return if (guardadas != null) Result.success(guardadas) else Result.failure(e)
        }
    }
}
