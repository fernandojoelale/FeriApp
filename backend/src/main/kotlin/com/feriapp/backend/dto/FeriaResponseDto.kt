package com.feriapp.backend.dto

import com.feriapp.backend.model.Feria
import com.feriapp.backend.model.Usuario

data class FeriaResponseDto(
    val id: Long,
    val organizador: Usuario,
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
    val puestosLibres: Int
)

fun Feria.toDto(puestosLibres: Int): FeriaResponseDto = FeriaResponseDto(
    id = this.id,
    organizador = this.organizador,
    nombre = this.nombre,
    fecha = this.fecha,
    horaInicio = this.horaInicio,
    horaFin = this.horaFin,
    direccion = this.direccion,
    zona = this.zona,
    latitud = this.latitud,
    longitud = this.longitud,
    rubro = this.rubro,
    precioPuesto = this.precioPuesto,
    puestosLibres = puestosLibres
)