package edu.uadeapps.feriapp.domain

data class Feria(
    val id: Long,
    val nombre: String,
    val fecha: String,
    val zona: String,
    val rubro: String,
    val precioPuesto: Double,
    val puestosLibres: Int
)