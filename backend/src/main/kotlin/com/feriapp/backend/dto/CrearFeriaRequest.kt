package com.feriapp.backend.dto

/** Posición de un puesto dentro del predio: columna (posX) y fila (posY), desde 0. */
data class PosicionPuestoRequest(
    val posX: Int,
    val posY: Int
)

/**
 * El organizador NO se envía: sale del token de la sesión.
 *
 * Los puestos se pueden definir de dos formas:
 *  - `puestos`: lista con la posición de cada puesto (el organizador arma el plano), o
 *  - `cantidadPuestos`: se genera una grilla automática de 5 columnas.
 * Si se envían las dos, se usa `puestos`.
 */
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
    val cantidadPuestos: Int? = null,
    val puestos: List<PosicionPuestoRequest>? = null
)
