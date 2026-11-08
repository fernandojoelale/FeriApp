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
    /** Posición de cada puesto en el plano del predio, definida por el organizador. */
    val puestos: List<PosicionPuesto>
) {
    val cantidadPuestos: Int get() = puestos.size
}

/** Columna (posX) y fila (posY) de un puesto dentro del plano, contadas desde 0. */
data class PosicionPuesto(val posX: Int, val posY: Int)
