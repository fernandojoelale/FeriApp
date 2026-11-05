package edu.uadeapps.feriapp.data.remote.dto

data class LoginRequestDto(val email: String, val password: String)

data class RegisterRequestDto(
    val nombre: String,
    val email: String,
    val password: String,
    val rol: String,
    val rubro: String? = null
)

data class AuthResponseDto(
    val token: String = "",
    val rol: String = "",
    val nombre: String = "",
    val email: String = "",
    val usuarioId: Long = 0
)
