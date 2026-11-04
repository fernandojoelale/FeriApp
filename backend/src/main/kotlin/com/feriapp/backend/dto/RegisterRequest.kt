package com.feriapp.backend.dto

import com.feriapp.backend.model.RolUsuario

data class RegisterRequest(
    val nombre: String,
    val email: String,
    val password: String,
    val rol: RolUsuario,
    val rubro: String? = null
)
