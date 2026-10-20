package com.feriapp.backend.model

import jakarta.persistence.*

enum class EstadoPuesto {
    DISPONIBLE,
    RESERVADO
}

@Entity
data class Puesto(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne
    @JoinColumn(name = "feria_id")
    val feria: Feria,
    val numero: Int,
    val posX: Int,
    val posY: Int,
    @Enumerated(EnumType.STRING)
    val estado: EstadoPuesto
)