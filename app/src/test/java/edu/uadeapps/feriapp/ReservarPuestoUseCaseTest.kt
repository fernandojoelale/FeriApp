package edu.uadeapps.feriapp

import edu.uadeapps.feriapp.domain.model.ErrorApp
import edu.uadeapps.feriapp.domain.model.EstadoPuesto
import edu.uadeapps.feriapp.domain.model.Puesto
import edu.uadeapps.feriapp.domain.model.Reserva
import edu.uadeapps.feriapp.domain.model.Rol
import edu.uadeapps.feriapp.domain.model.Session
import edu.uadeapps.feriapp.domain.repository.AuthRepository
import edu.uadeapps.feriapp.domain.repository.ReservaRepository
import edu.uadeapps.feriapp.domain.usecase.ReservarPuestoUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReservarPuestoUseCaseTest {

    private class AuthFalso(sesion: Session?) : AuthRepository {
        override val session: Flow<Session?> = flowOf(sesion)
        override suspend fun login(email: String, password: String) = Result.failure<Session>(Exception())
        override suspend fun registrar(nombre: String, email: String, password: String, rol: Rol, rubro: String?) =
            Result.failure<Session>(Exception())
        override suspend fun cerrarSesion() {}
    }

    private class ReservasFalsas : ReservaRepository {
        var llamadas = 0
        override suspend fun reservar(puestoId: Long): Result<Reserva> {
            llamadas++
            return Result.success(
                Reserva(1, "FA-2026-0001", puestoId, 3, 1, "Feria", "2026-12-20", "10:00", "18:00", "Calle 1", "Palermo", 0.0, 0.0, 1000.0)
            )
        }
        override suspend fun obtenerMisReservas() = Result.success(emptyList<Reserva>())
        override suspend fun obtenerReserva(reservaId: Long) = Result.failure<Reserva>(Exception())
    }

    private fun sesion(rol: Rol) = Session("token", rol, "Nombre", "mail@test.com", 1)
    private fun puesto(estado: EstadoPuesto) = Puesto(10, 3, 2, 0, estado)

    private fun tipoDeError(r: Result<Reserva>) = (r.exceptionOrNull() as ErrorApp).tipo

    @Test
    fun sinSesion_noAutorizado_yNoLlamaAlServidor() = runBlocking {
        val reservas = ReservasFalsas()
        val r = ReservarPuestoUseCase(reservas, AuthFalso(null))(puesto(EstadoPuesto.DISPONIBLE))
        assertEquals(ErrorApp.Tipo.NO_AUTORIZADO, tipoDeError(r))
        assertEquals(0, reservas.llamadas)
    }

    @Test
    fun organizador_noPuedeReservar() = runBlocking {
        val reservas = ReservasFalsas()
        val r = ReservarPuestoUseCase(reservas, AuthFalso(sesion(Rol.ORGANIZADOR)))(puesto(EstadoPuesto.DISPONIBLE))
        assertEquals(ErrorApp.Tipo.NO_AUTORIZADO, tipoDeError(r))
        assertEquals(0, reservas.llamadas)
    }

    @Test
    fun puestoReservado_dacConflicto() = runBlocking {
        val reservas = ReservasFalsas()
        val r = ReservarPuestoUseCase(reservas, AuthFalso(sesion(Rol.FERIANTE)))(puesto(EstadoPuesto.RESERVADO))
        assertEquals(ErrorApp.Tipo.CONFLICTO, tipoDeError(r))
        assertEquals(0, reservas.llamadas)
    }

    @Test
    fun feriantePuestoLibre_reservaOk() = runBlocking {
        val reservas = ReservasFalsas()
        val r = ReservarPuestoUseCase(reservas, AuthFalso(sesion(Rol.FERIANTE)))(puesto(EstadoPuesto.DISPONIBLE))
        assertTrue(r.isSuccess)
        assertEquals(1, reservas.llamadas)
    }
}
