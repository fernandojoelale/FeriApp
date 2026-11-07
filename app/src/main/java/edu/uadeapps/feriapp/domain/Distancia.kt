package edu.uadeapps.feriapp.domain

import java.util.Locale
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

private const val RADIO_TIERRA_KM = 6371.0

/** Distancia en km en línea recta entre dos coordenadas (fórmula de Haversine). */
fun distanciaKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).pow(2) +
        cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
    return 2 * RADIO_TIERRA_KM * asin(sqrt(a))
}

/** 0.4 -> "a 400 m"; 3.24 -> "a 3,2 km"; 25.6 -> "a 26 km". */
fun formatearDistancia(km: Double): String = when {
    km < 1.0 -> "a ${(km * 1000).toInt()} m"
    km < 10.0 -> "a ${String.format(Locale.forLanguageTag("es-AR"), "%.1f", km)} km"
    else -> "a ${km.toInt()} km"
}

/** Las ferias sin coordenadas cargadas vienen en (0,0): no se puede calcular su distancia. */
fun tieneUbicacion(latitud: Double, longitud: Double): Boolean = latitud != 0.0 || longitud != 0.0
