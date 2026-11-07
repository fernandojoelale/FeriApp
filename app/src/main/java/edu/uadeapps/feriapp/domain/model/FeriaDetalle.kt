package edu.uadeapps.feriapp.domain.model

import edu.uadeapps.feriapp.domain.Feria

data class FeriaDetalle(
    val feria: Feria,
    val puestos: List<Puesto>,
    val desdeCache: Boolean = false,
    val actualizadoEn: Long = 0L
) {
    val totalPuestos: Int get() = puestos.size
}
