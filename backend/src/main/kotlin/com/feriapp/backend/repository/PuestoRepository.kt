package com.feriapp.backend.repository

import com.feriapp.backend.model.EstadoPuesto
import com.feriapp.backend.model.Puesto
import org.springframework.data.jpa.repository.JpaRepository

interface PuestoRepository : JpaRepository<Puesto, Long> {
    fun findByFeriaId(feriaId: Long): List<Puesto>
    fun countByFeriaIdAndEstado(feriaId: Long, estado: EstadoPuesto): Int
}