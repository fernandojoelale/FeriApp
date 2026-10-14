package com.feriapp.backend.dto

data class CrearFeriaRequest(
    val organizadorId: Long,
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