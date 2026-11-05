package com.feriapp.backend.dto

import com.feriapp.backend.model.Puesto

data class PuestoDto(
    val id: Long,
    val numero: Int,
    val posX: Int,
    val posY: Int,
    val estado: String
)

fun Puesto.toDto() = PuestoDto(
    id = id,
    numero = numero,
    posX = posX,
    posY = posY,
    estado = estado.name
)
