package com.feriapp.backend.service

import com.feriapp.backend.dto.ReservaRequest
import com.feriapp.backend.model.EstadoPuesto
import com.feriapp.backend.model.EstadoReserva
import com.feriapp.backend.model.Reserva
import com.feriapp.backend.repository.FeriaRepository
import com.feriapp.backend.repository.PuestoRepository
import com.feriapp.backend.repository.ReservaRepository
import com.feriapp.backend.repository.UsuarioRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReservaService(
    private val reservaRepository: ReservaRepository,
    private val puestoRepository: PuestoRepository,
    private val usuarioRepository: UsuarioRepository,
    private val feriaRepository: FeriaRepository
) {
    @Transactional // garantiza que no se reserve dos veces
    fun reservarPuesto(request: ReservaRequest): Reserva {
        val puesto = puestoRepository.findById(request.puestoId)
            .orElseThrow { Exception("Puesto no encontrado") }

        val feriante = usuarioRepository.findById(request.ferianteId)
            .orElseThrow { Exception("Feriante no encontrado") }

        // Validar que esté libre
        if (puesto.estado != EstadoPuesto.DISPONIBLE) {
            throw Exception("El puesto ya está reservado")
        }

        // Marcar puesto como reservado
        val puestoActualizado = puesto.copy(estado = EstadoPuesto.RESERVADO)
        puestoRepository.save(puestoActualizado)

        // Crear reserva
        val nuevaReserva = Reserva(
            puesto = puestoActualizado,
            feria = puestoActualizado.feria,
            feriante = feriante,
            estado = EstadoReserva.CONFIRMADA
        )

        // TODO: Acá iría la notificación push (RF04)[cite: 8]

        return reservaRepository.save(nuevaReserva)
    }
}