package com.feriapp.backend.dto

data class AuthResponse(
    val token: String,
    val rol: String,
    val nombre: String,
    val email: String,
    val usuarioId: Long
)
