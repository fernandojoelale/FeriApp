package edu.uadeapps.feriapp.data.remote.dto

import edu.uadeapps.feriapp.data.remote.FeriaDto

data class PuestoDto(
    val id: Long = 0,
    val numero: Int = 0,
    val posX: Int = 0,
    val posY: Int = 0,
    val estado: String = "DISPONIBLE"
)

data class FeriaDetalleDto(
    val feria: FeriaDto = FeriaDto(),
    val puestos: List<PuestoDto> = emptyList()
)

data class ReservaRequestDto(val puestoId: Long)

data class ReservaDto(
    val id: Long = 0,
    val comprobante: String = "",
    val puestoId: Long = 0,
    val numeroPuesto: Int = 0,
    val feriaId: Long = 0,
    val feriaNombre: String = "",
    val feriaFecha: String = "",
    val horaInicio: String = "",
    val horaFin: String = "",
    val direccion: String = "",
    val zona: String = "",
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val precioPuesto: Double = 0.0
)

/** Cuerpo de error del backend: {"codigo": "...", "mensaje": "..."} */
data class ErrorBodyDto(val codigo: String? = null, val mensaje: String? = null)
