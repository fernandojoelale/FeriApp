package edu.uadeapps.feriapp.data.repository

import edu.uadeapps.feriapp.data.mapper.toDomain
import edu.uadeapps.feriapp.data.remote.FeriAppApi
import edu.uadeapps.feriapp.domain.Feria

class FeriaRepository(private val api: FeriAppApi) {

    suspend fun obtenerFerias(): Result<List<Feria>> = runCatching {
        // Llama a la API y transforma cada FeriaDto en un objeto Feria
        api.getFerias().map { dto -> dto.toDomain() }
    }
}
