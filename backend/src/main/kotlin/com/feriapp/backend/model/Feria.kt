package com.feriapp.backend.model

import jakarta.persistence.*

@Entity
data class Feria(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne
    @JoinColumn(name = "organizador_id")
    val organizador: Usuario,
    val nombre: String,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val direccion: String,
    val zona: String,
    val latitud: Double,
    val longitud: Double,
    val rubro: String,
    val precioPuesto: Double
)