package edu.uadeapps.feriapp.domain.model

/** Datos de la sesión iniciada. El token se envía al backend en cada pedido. */
data class Session(
    val token: String,
    val rol: Rol,
    val nombre: String,
    val email: String,
    val usuarioId: Long
)
