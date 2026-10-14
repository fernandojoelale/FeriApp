package com.feriapp.backend.repository

import com.feriapp.backend.model.Reserva
import org.springframework.data.jpa.repository.JpaRepository

interface ReservaRepository : JpaRepository<Reserva, Long> {
    fun findByFerianteId(ferianteId: Long): List<Reserva>
}