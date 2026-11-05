package edu.uadeapps.feriapp.data.repository

import edu.uadeapps.feriapp.data.local.SessionManager
import edu.uadeapps.feriapp.data.mapper.toSession
import edu.uadeapps.feriapp.data.remote.AuthApi
import edu.uadeapps.feriapp.data.remote.dto.AuthResponseDto
import edu.uadeapps.feriapp.data.remote.dto.LoginRequestDto
import edu.uadeapps.feriapp.data.remote.dto.RegisterRequestDto
import edu.uadeapps.feriapp.data.remote.toErrorApp
import edu.uadeapps.feriapp.domain.model.ErrorApp
import edu.uadeapps.feriapp.domain.model.Rol
import edu.uadeapps.feriapp.domain.model.Session
import edu.uadeapps.feriapp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.cancellation.CancellationException

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val sessionManager: SessionManager
) : AuthRepository {

    override val session: Flow<Session?> = sessionManager.session

    override suspend fun login(email: String, password: String): Result<Session> =
        autenticar(esLogin = true) { api.login(LoginRequestDto(email, password)) }

    override suspend fun registrar(
        nombre: String,
        email: String,
        password: String,
        rol: Rol,
        rubro: String?
    ): Result<Session> =
        autenticar(esLogin = false) {
            api.register(RegisterRequestDto(nombre, email, password, rol.name, rubro))
        }

    override suspend fun cerrarSesion() = sessionManager.cerrar()

    private suspend fun autenticar(
        esLogin: Boolean,
        llamada: suspend () -> AuthResponseDto
    ): Result<Session> =
        try {
            val sesion = llamada().toSession()
            sessionManager.guardar(sesion)
            Result.success(sesion)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val error = e.toErrorApp()
            // En el login, un 401 significa email o contraseña incorrectos.
            Result.failure(
                if (esLogin && error.tipo == ErrorApp.Tipo.NO_AUTORIZADO) {
                    ErrorApp(ErrorApp.Tipo.CREDENCIALES, "Revisá tu email y tu contraseña.")
                } else {
                    error
                }
            )
        }
}
