package edu.uadeapps.feriapp.domain

data class Feria(
    val id: Long,
    val nombre: String,
    val fecha: String,
    val zona: String,
    val rubro: String,
    val precioPuesto: Double,
    val puestosLibres: Int,
    val horaInicio: String = "",
    val horaFin: String = "",
    val direccion: String = "",
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val organizadorNombre: String = ""
)
