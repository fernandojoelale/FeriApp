package com.feriapp.backend.dto

/** Una feria con todos sus puestos (para dibujar el mapa del predio). */
data class FeriaDetalleDto(
    val feria: FeriaResponseDto,
    val puestos: List<PuestoDto>
)
