package edu.uadeapps.feriapp.data.repository

import edu.uadeapps.feriapp.data.local.db.Coleccion
import edu.uadeapps.feriapp.data.local.db.FeriAppDatabase
import edu.uadeapps.feriapp.data.local.db.toDomain
import edu.uadeapps.feriapp.data.local.db.toEntity
import edu.uadeapps.feriapp.data.mapper.toDomain
import edu.uadeapps.feriapp.data.remote.FeriAppApi
import edu.uadeapps.feriapp.data.remote.dto.CrearFeriaDto
import edu.uadeapps.feriapp.data.remote.dto.PosicionPuestoDto
import edu.uadeapps.feriapp.data.remote.toErrorApp
import edu.uadeapps.feriapp.domain.Feria
import edu.uadeapps.feriapp.domain.model.FeriaDetalle
import edu.uadeapps.feriapp.domain.model.NuevaFeria
import edu.uadeapps.feriapp.domain.model.ReservaDeFeria
import edu.uadeapps.feriapp.domain.repository.DetalleFeriaRepository
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class DetalleFeriaRepositoryImpl(
    private val api: FeriAppApi,
    private val db: FeriAppDatabase? = null
) : DetalleFeriaRepository {

    override suspend fun obtenerDetalle(feriaId: Long): Result<FeriaDetalle> =
        try {
            val dto = api.getFeriaDetalle(feriaId)
            val ahora = System.currentTimeMillis()
            guardarDetalle(feriaId, dto.feria.toEntity(Coleccion.DETALLE, ahora), dto.puestos.map { it.toEntity(feriaId, ahora) })
            Result.success(dto.toDomain().copy(actualizadoEn = ahora))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val guardado = if (e is IOException) leerDetalleGuardado(feriaId) else null
            if (guardado != null) Result.success(guardado) else Result.failure(e.toErrorApp())
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
                puestos = nueva.puestos.map { PosicionPuestoDto(it.posX, it.posY) }
            )
            Result.success(api.crearFeria(dto).toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e.toErrorApp())
        }

    override suspend fun obtenerMisFerias(): Result<List<Feria>> =
        try {
            val dtos = api.getMisFerias()
            val ahora = System.currentTimeMillis()
            try {
                db?.feriaDao()?.reemplazar(Coleccion.MIAS, dtos.map { it.toEntity(Coleccion.MIAS, ahora) })
            } catch (e: Exception) {
                // La copia local es opcional.
            }
            Result.success(dtos.map { it.toDomain().copy(actualizadoEn = ahora) })
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val guardadas = if (e is IOException) leerMisFeriasGuardadas() else null
            if (guardadas != null) Result.success(guardadas) else Result.failure(e.toErrorApp())
        }

    /** Las reservas de una feria las ve solo su organizador y siempre se piden al servidor. */
    override suspend fun obtenerReservasDeFeria(feriaId: Long): Result<List<ReservaDeFeria>> =
        try {
            Result.success(api.getReservasDeFeria(feriaId).map { it.toDomain() })
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e.toErrorApp())
        }

    private suspend fun guardarDetalle(
        feriaId: Long,
        feria: edu.uadeapps.feriapp.data.local.db.FeriaEntity,
        puestos: List<edu.uadeapps.feriapp.data.local.db.PuestoEntity>
    ) {
        try {
            db?.feriaDao()?.insertar(listOf(feria))
            db?.puestoDao()?.reemplazarDeFeria(feriaId, puestos)
        } catch (e: Exception) {
            // La copia local es opcional.
        }
    }

    private suspend fun leerDetalleGuardado(feriaId: Long): FeriaDetalle? =
        try {
            val feria = db?.feriaDao()?.obtener(feriaId, Coleccion.DETALLE)
            val puestos = db?.puestoDao()?.listarDeFeria(feriaId).orEmpty()
            if (feria == null || puestos.isEmpty()) null
            else FeriaDetalle(
                feria = feria.toDomain(),
                puestos = puestos.map { it.toDomain() },
                desdeCache = true,
                actualizadoEn = puestos.maxOf { it.actualizadoEn }
            )
        } catch (e: Exception) {
            null
        }

    private suspend fun leerMisFeriasGuardadas(): List<Feria>? =
        try {
            db?.feriaDao()?.listar(Coleccion.MIAS)?.takeIf { it.isNotEmpty() }?.map { it.toDomain() }
        } catch (e: Exception) {
            null
        }
}
