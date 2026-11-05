package edu.uadeapps.feriapp.domain.usecase

import edu.uadeapps.feriapp.domain.model.ErrorApp
import edu.uadeapps.feriapp.domain.model.FeriaDetalle
import edu.uadeapps.feriapp.domain.model.Puesto
import edu.uadeapps.feriapp.domain.model.Reserva
import edu.uadeapps.feriapp.domain.model.Rol
import edu.uadeapps.feriapp.domain.repository.AuthRepository
import edu.uadeapps.feriapp.domain.repository.DetalleFeriaRepository
import edu.uadeapps.feriapp.domain.repository.ReservaRepository
import kotlinx.coroutines.flow.first

class ObtenerDetalleFeriaUseCase(private val repository: DetalleFeriaRepository) {
    suspend operator fun invoke(feriaId: Long): Result<FeriaDetalle> = repository.obtenerDetalle(feriaId)
}

/**
 * Regla de negocio (etapa 1): solo se puede reservar un puesto DISPONIBLE
 * y con la sesión iniciada como feriante.
 */
class ReservarPuestoUseCase(
    private val reservaRepository: ReservaRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(puesto: Puesto): Result<Reserva> {
        val sesion = authRepository.session.first()
            ?: return Result.failure(
                ErrorApp(ErrorApp.Tipo.NO_AUTORIZADO, "Iniciá sesión como feriante para reservar un puesto.")
            )
        if (sesion.rol != Rol.FERIANTE) {
            return Result.failure(
                ErrorApp(ErrorApp.Tipo.NO_AUTORIZADO, "Solo las cuentas de feriante pueden reservar puestos.")
            )
        }
        if (!puesto.disponible) {
            return Result.failure(
                ErrorApp(ErrorApp.Tipo.CONFLICTO, "Ese puesto ya fue reservado. Elegí otro puesto libre.")
            )
        }
        return reservaRepository.reservar(puesto.id)
    }
}

class ObtenerMisReservasUseCase(private val repository: ReservaRepository) {
    suspend operator fun invoke(): Result<List<Reserva>> = repository.obtenerMisReservas()
}

class ObtenerReservaUseCase(private val repository: ReservaRepository) {
    suspend operator fun invoke(reservaId: Long): Result<Reserva> = repository.obtenerReserva(reservaId)
}
