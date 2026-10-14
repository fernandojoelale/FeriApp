package com.feriapp.backend.model

import jakarta.persistence.*
import java.time.LocalDateTime

enum class EstadoReserva {
    CONFIRMADA
}

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["puesto_id"])])
data class Reserva(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne
    @JoinColumn(name = "puesto_id", unique = true)
    val puesto: Puesto,

    @ManyToOne
    @JoinColumn(name = "feria_id")
    val feria: Feria,

    @ManyToOne
    @JoinColumn(name = "feriante_id")
    val feriante: Usuario,

    val fechaReserva: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    val estado: EstadoReserva
)