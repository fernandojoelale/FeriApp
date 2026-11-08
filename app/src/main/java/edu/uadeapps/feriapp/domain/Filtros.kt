package edu.uadeapps.feriapp.domain

import java.time.LocalDate

/** Filtro de fecha del listado de ferias (RF02). Lógica pura, sin Android, para poder testearla. */
sealed interface FiltroFecha {
    data object Cualquiera : FiltroFecha
    /** Desde hoy hasta dentro de [dias] días (inclusive). */
    data class ProximosDias(val dias: Long, val etiqueta: String) : FiltroFecha
    data class Dia(val fecha: LocalDate) : FiltroFecha

    fun cumple(fechaIso: String, hoy: LocalDate = LocalDate.now()): Boolean {
        if (this is Cualquiera) return true
        val f = try { LocalDate.parse(fechaIso) } catch (e: Exception) { return false }
        return when (this) {
            is Cualquiera -> true
            is ProximosDias -> !f.isBefore(hoy) && !f.isAfter(hoy.plusDays(dias))
            is Dia -> f == fecha
        }
    }
}

/** Precio máximo por puesto; null = sin límite. */
fun cumplePrecioMaximo(precio: Double, maximo: Double?): Boolean = maximo == null || precio <= maximo
