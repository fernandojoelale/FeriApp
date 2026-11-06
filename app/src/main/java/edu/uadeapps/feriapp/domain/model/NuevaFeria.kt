package edu.uadeapps.feriapp.domain.model

data class NuevaFeria(
    val nombre: String,
    val fecha: String,       // AAAA-MM-DD
    val horaInicio: String,  // HH:mm
    val horaFin: String,     // HH:mm
    val direccion: String,
    val zona: String,
    val latitud: Double,
    val longitud: Double,
    val rubro: String,
    val precioPuesto: Double,
    val cantidadPuestos: Int
)
