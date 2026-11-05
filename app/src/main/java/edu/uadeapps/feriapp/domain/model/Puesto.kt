package edu.uadeapps.feriapp.domain.model

enum class EstadoPuesto { DISPONIBLE, RESERVADO }

data class Puesto(
    val id: Long,
    val numero: Int,
    val posX: Int,
    val posY: Int,
    val estado: EstadoPuesto
) {
    val disponible: Boolean get() = estado == EstadoPuesto.DISPONIBLE
}
