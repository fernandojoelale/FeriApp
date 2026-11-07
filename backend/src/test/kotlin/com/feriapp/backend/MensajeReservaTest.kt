package com.feriapp.backend

import com.feriapp.backend.model.EstadoPuesto
import com.feriapp.backend.model.EstadoReserva
import com.feriapp.backend.model.Feria
import com.feriapp.backend.model.Puesto
import com.feriapp.backend.model.Reserva
import com.feriapp.backend.model.RolUsuario
import com.feriapp.backend.model.Usuario
import com.feriapp.backend.service.mensajeDeReserva
import com.feriapp.backend.service.tieneTokenPush
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MensajeReservaTest {

    private val organizador = Usuario(id = 1, nombre = "Org", password = "x", email = "o@t.com", rol = RolUsuario.ORGANIZADOR)
    private val feriante = Usuario(id = 2, nombre = "Lucia", password = "x", email = "l@t.com", rol = RolUsuario.FERIANTE)
    private val feria = Feria(
        id = 7, organizador = organizador, nombre = "Feria Test", fecha = "2026-12-20",
        horaInicio = "10:00", horaFin = "18:00", direccion = "Calle 1", zona = "Palermo",
        latitud = 0.0, longitud = 0.0, rubro = "Arte", precioPuesto = 1000.0
    )
    private val puesto = Puesto(id = 3, feria = feria, numero = 4, posX = 3, posY = 0, estado = EstadoPuesto.RESERVADO)
    private val reserva = Reserva(id = 9, puesto = puesto, feria = feria, feriante = feriante, estado = EstadoReserva.CONFIRMADA)

    @Test
    fun elMensajeIdentificaAlFerianteElPuestoYLaFeria() {
        val m = mensajeDeReserva(reserva)
        assertEquals(7L, m.feriaId)
        assertTrue(m.cuerpo.contains("Lucia"))
        assertTrue(m.cuerpo.contains("puesto 4"))
        assertTrue(m.cuerpo.contains("Feria Test"))
    }

    @Test
    fun tokenPush_soloSiNoEstaVacio() {
        assertFalse(organizador.tieneTokenPush())
        assertTrue(organizador.copy(fcmToken = "abc").tieneTokenPush())
    }
}
