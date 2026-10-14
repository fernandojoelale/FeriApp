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
    val feria: Feria, // Relación con la feria[cite: 22]
    val numero: Int,
    val posX: Int, // Posición en el mapa del predio[cite: 22]
    val posY: Int,
    @Enumerated(EnumType.STRING)
    val estado: EstadoPuesto
)