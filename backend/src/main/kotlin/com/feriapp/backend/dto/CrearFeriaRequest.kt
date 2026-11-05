package com.feriapp.backend.dto

/** El organizador NO se envía: sale del token de la sesión. */
data class CrearFeriaRequest(
    val nombre: String,
    val fecha: String,        // AAAA-MM-DD
    val horaInicio: String,   // HH:mm
    val horaFin: String,      // HH:mm
    val direccion: String,
    val zona: String,
    val latitud: Double,
    val longitud: Double,
    val rubro: String,
    val precioPuesto: Double,
    val cantidadPuestos: Int
)
