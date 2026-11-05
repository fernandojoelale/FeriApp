package com.feriapp.backend.dto

/** El feriante NO se envía: sale del token de la sesión. */
data class ReservaRequest(
    val puestoId: Long
)
