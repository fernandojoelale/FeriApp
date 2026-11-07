package edu.uadeapps.feriapp

import edu.uadeapps.feriapp.domain.distanciaKm
import edu.uadeapps.feriapp.domain.formatearDistancia
import edu.uadeapps.feriapp.domain.tieneUbicacion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DistanciaTest {

    @Test
    fun mismoPunto_distanciaCero() {
        assertEquals(0.0, distanciaKm(-34.6, -58.4, -34.6, -58.4), 0.001)
    }

    @Test
    fun obeliscoAPlazaDeMayo_ronda1y3Km() {
        // Obelisco (-34.6037, -58.3816) y Plaza de Mayo (-34.6083, -58.3712): ~1,1 km
        val km = distanciaKm(-34.6037, -58.3816, -34.6083, -58.3712)
        assertTrue(km in 0.9..1.4)
    }

    @Test
    fun buenosAiresACordoba_ronda646Km() {
        val km = distanciaKm(-34.6037, -58.3816, -31.4201, -64.1888)
        assertTrue(km in 630.0..660.0)
    }

    @Test
    fun formato_segunMagnitud() {
        assertEquals("a 400 m", formatearDistancia(0.4))
        assertEquals("a 3,2 km", formatearDistancia(3.24))
        assertEquals("a 26 km", formatearDistancia(25.6))
    }

    @Test
    fun coordenadasEnCero_sinUbicacion() {
        assertFalse(tieneUbicacion(0.0, 0.0))
        assertTrue(tieneUbicacion(-34.6, -58.4))
    }
}
