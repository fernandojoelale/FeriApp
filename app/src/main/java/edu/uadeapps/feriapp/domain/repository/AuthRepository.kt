package edu.uadeapps.feriapp.domain.repository

import edu.uadeapps.feriapp.domain.model.Rol
import edu.uadeapps.feriapp.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    /** Sesión actual (null si no hay sesión iniciada). */
    val session: Flow<Session?>

    suspend fun login(email: String, password: String): Result<Session>
    suspend fun registrar(nombre: String, email: String, password: String, rol: Rol, rubro: String?): Result<Session>
    suspend fun cerrarSesion()
}
