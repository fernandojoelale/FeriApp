package edu.uadeapps.feriapp.domain.repository

import edu.uadeapps.feriapp.domain.Feria
import edu.uadeapps.feriapp.domain.model.FeriaDetalle
import edu.uadeapps.feriapp.domain.model.NuevaFeria

interface DetalleFeriaRepository {
    suspend fun obtenerDetalle(feriaId: Long): Result<FeriaDetalle>
    suspend fun crearFeria(nueva: NuevaFeria): Result<Feria>
    suspend fun obtenerMisFerias(): Result<List<Feria>>
}
