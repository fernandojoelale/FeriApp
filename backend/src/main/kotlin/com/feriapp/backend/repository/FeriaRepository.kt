package com.feriapp.backend.repository

import com.feriapp.backend.model.Feria
import org.springframework.data.jpa.repository.JpaRepository

interface FeriaRepository : JpaRepository<Feria, Long> {
    fun findByOrganizadorId(organizadorId: Long): List<Feria>
}
