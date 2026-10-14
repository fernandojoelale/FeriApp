package com.feriapp.backend.model

import jakarta.persistence.*


enum class RolUsuario {
    FERIANTE,
    ORGANIZADOR
}

@Entity
data class Usuario(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val nombre: String,
    val email: String,
    @Enumerated(EnumType.STRING)
    val rol: RolUsuario,
    val rubro: String? = null
)