package com.feriapp.backend.service

import com.feriapp.backend.exception.PuestoYaReservadoException
import com.feriapp.backend.exception.RecursoNoEncontradoException
import com.feriapp.backend.model.EstadoPuesto
import com.feriapp.backend.model.EstadoReserva
import com.feriapp.backend.model.Reserva
import com.feriapp.backend.model.Usuario
import com.feriapp.backend.repository.PuestoRepository
import com.feriapp.backend.repository.ReservaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReservaService(
    private val reservaRepository: ReservaRepository,
    private val puestoRepository: PuestoRepository
) {
    /**
     * Reserva un puesto para el feriante autenticado. Si dos personas reservan a la vez,
     * la restricción UNIQUE de Reserva.puesto_id hace fallar a la segunda (RNF05).
     */
    @Transactional
    fun reservarPuesto(feriante: Usuario, puestoId: Long): Reserva {
        val puesto = puestoRepository.findById(puestoId)
            .orElseThrow { RecursoNoEncontradoException("Puesto no encontrado") }

        if (puesto.estado != EstadoPuesto.DISPONIBLE) {
            throw PuestoYaReservadoException()
        }

        val puestoReservado = puestoRepository.save(puesto.copy(estado = EstadoPuesto.RESERVADO))

        return reservaRepository.save(
            Reserva(
                puesto = puestoReservado,
                feria = puestoReservado.feria,
                feriante = feriante,
                estado = EstadoReserva.CONFIRMADA
            )
        )
    }

    fun obtenerMisReservas(feriante: Usuario): List<Reserva> =
        reservaRepository.findByFerianteIdOrderByFechaReservaDesc(feriante.id)

    fun obtenerReserva(feriante: Usuario, reservaId: Long): Reserva {
        val reserva = reservaRepository.findById(reservaId)
            .orElseThrow { RecursoNoEncontradoException("Reserva no encontrada") }
        // Una reserva ajena se trata como inexistente.
        if (reserva.feriante.id != feriante.id) throw RecursoNoEncontradoException("Reserva no encontrada")
        return reserva
    }
}
