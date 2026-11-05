package edu.uadeapps.feriapp.domain.usecase

import edu.uadeapps.feriapp.domain.model.ErrorApp
import edu.uadeapps.feriapp.domain.model.Rol
import edu.uadeapps.feriapp.domain.model.Session
import edu.uadeapps.feriapp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class IniciarSesionUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<Session> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(ErrorApp(ErrorApp.Tipo.VALIDACION, "Completá tu email y tu contraseña."))
        }
        return repository.login(email.trim(), password)
    }
}

class RegistrarUsuarioUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(
        nombre: String,
        email: String,
        password: String,
        rol: Rol,
        rubro: String
    ): Result<Session> {
        // RNF04: se piden solo los datos necesarios (el rubro es opcional y solo para feriantes).
        if (nombre.isBlank()) {
            return Result.failure(ErrorApp(ErrorApp.Tipo.VALIDACION, "Ingresá tu nombre."))
        }
        if (!email.contains("@") || email.trim().length < 5) {
            return Result.failure(ErrorApp(ErrorApp.Tipo.VALIDACION, "Ingresá un email válido."))
        }
        if (password.length < 6) {
            return Result.failure(
                ErrorApp(ErrorApp.Tipo.VALIDACION, "La contraseña debe tener al menos 6 caracteres.")
            )
        }
        val rubroFinal = if (rol == Rol.FERIANTE) rubro.trim().ifBlank { null } else null
        return repository.registrar(nombre.trim(), email.trim(), password, rol, rubroFinal)
    }
}

class ObservarSesionUseCase(private val repository: AuthRepository) {
    operator fun invoke(): Flow<Session?> = repository.session
}

class CerrarSesionUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() = repository.cerrarSesion()
}
