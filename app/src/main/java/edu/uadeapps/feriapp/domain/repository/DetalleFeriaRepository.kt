package edu.uadeapps.feriapp.domain.repository

import edu.uadeapps.feriapp.domain.model.FeriaDetalle

interface DetalleFeriaRepository {
    suspend fun obtenerDetalle(feriaId: Long): Result<FeriaDetalle>
}
