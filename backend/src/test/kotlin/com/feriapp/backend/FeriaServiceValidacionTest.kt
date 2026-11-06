package com.feriapp.backend

import com.feriapp.backend.dto.CrearFeriaRequest
import com.feriapp.backend.exception.SolicitudInvalidaException
import com.feriapp.backend.repository.FeriaRepository
import com.feriapp.backend.repository.PuestoRepository
import com.feriapp.backend.repository.UsuarioRepository
import com.feriapp.backend.service.FeriaService
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import java.time.LocalDate

/** Las validaciones corren antes de tocar la base, así que alcanza con repositorios simulados. */
class FeriaServiceValidacionTest {

    private val service = FeriaService(
        mock(FeriaRepository::class.java),
        mock(UsuarioRepository::class.java),
        mock(PuestoRepository::class.java)
    )

    private fun request(
        nombre: String = "Feria Test",
        fecha: String = LocalDate.now().plusDays(10).toString(),
        horaInicio: String = "10:00",
        horaFin: String = "18:00",
        precio: Double = 1000.0,
        cantidad: Int = 10
    ) = CrearFeriaRequest(
        nombre = nombre, fecha = fecha, horaInicio = horaInicio, horaFin = horaFin,
        direccion = "Calle 123", zona = "Palermo", latitud = -34.5, longitud = -58.4,
        rubro = "Artesanias", precioPuesto = precio, cantidadPuestos = cantidad
    )

    @Test
    fun nombreVacio_seRechaza() {
        assertThrows(SolicitudInvalidaException::class.java) { service.crearFeria(1, request(nombre = " ")) }
    }

    @Test
    fun fechaPasada_seRechaza() {
        val ayer = LocalDate.now().minusDays(1).toString()
        assertThrows(SolicitudInvalidaException::class.java) { service.crearFeria(1, request(fecha = ayer)) }
    }

    @Test
    fun horarioInvertido_seRechaza() {
        assertThrows(SolicitudInvalidaException::class.java) {
            service.crearFeria(1, request(horaInicio = "18:00", horaFin = "10:00"))
        }
    }

    @Test
    fun precioCero_seRechaza() {
        assertThrows(SolicitudInvalidaException::class.java) { service.crearFeria(1, request(precio = 0.0)) }
    }

    @Test
    fun cantidadDePuestosInvalida_seRechaza() {
        assertThrows(SolicitudInvalidaException::class.java) { service.crearFeria(1, request(cantidad = 0)) }
    }
}
