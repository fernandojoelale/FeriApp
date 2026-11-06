package edu.uadeapps.feriapp.data.remote.dto

/** Cuerpo de POST /api/ferias. El organizador sale del token, no se envía. */
data class CrearFeriaDto(
    val nombre: String,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val direccion: String,
    val zona: String,
    val latitud: Double,
    val longitud: Double,
    val rubro: String,
    val precioPuesto: Double,
    val cantidadPuestos: Int
)
