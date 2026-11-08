package edu.uadeapps.feriapp

import edu.uadeapps.feriapp.domain.model.NuevaFeria
import edu.uadeapps.feriapp.domain.model.PosicionPuesto
import edu.uadeapps.feriapp.domain.usecase.validarNuevaFeria
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class ValidacionFeriaTest {

    private val hoy = LocalDate.of(2026, 11, 5)

    private fun feriaValida() = NuevaFeria(
        nombre = "Feria de las Artes",
        fecha = "2026-12-20",
        horaInicio = "10:00",
        horaFin = "18:00",
        direccion = "Av. Santa Fe 1234",
        zona = "Palermo",
        latitud = -34.58,
        longitud = -58.42,
        rubro = "Artesanias",
        precioPuesto = 18000.0,
        puestos = plano(4, 5)
    )

    private fun plano(columnas: Int, filas: Int) =
        (0 until filas).flatMap { y -> (0 until columnas).map { x -> PosicionPuesto(x, y) } }

    @Test
    fun feriaValida_noTieneErrores() {
        assertNull(validarNuevaFeria(feriaValida(), hoy))
    }

    @Test
    fun nombreVacio_esInvalido() {
        assertNotNull(validarNuevaFeria(feriaValida().copy(nombre = "  "), hoy))
    }

    @Test
    fun fechaPasada_esInvalida() {
        assertEquals("Elegí una fecha futura.", validarNuevaFeria(feriaValida().copy(fecha = "2026-01-01"), hoy))
    }

    @Test
    fun fechaConFormatoIncorrecto_esInvalida() {
        assertNotNull(validarNuevaFeria(feriaValida().copy(fecha = "20/12/2026"), hoy))
    }

    @Test
    fun horaFinAnteriorAlInicio_esInvalida() {
        assertNotNull(validarNuevaFeria(feriaValida().copy(horaInicio = "18:00", horaFin = "10:00"), hoy))
    }

    @Test
    fun precioCero_esInvalido() {
        assertNotNull(validarNuevaFeria(feriaValida().copy(precioPuesto = 0.0), hoy))
    }

    @Test
    fun sinPuestosEnElPlano_esInvalido() {
        assertEquals("Marcá en el plano al menos un puesto.", validarNuevaFeria(feriaValida().copy(puestos = emptyList()), hoy))
    }

    @Test
    fun masDe200Puestos_esInvalido() {
        assertNotNull(validarNuevaFeria(feriaValida().copy(puestos = plano(6, 40)), hoy))
    }

    @Test
    fun posicionesRepetidas_sonInvalidas() {
        assertNotNull(validarNuevaFeria(feriaValida().copy(puestos = listOf(PosicionPuesto(0, 0), PosicionPuesto(0, 0))), hoy))
    }

    @Test
    fun posicionFueraDelPlano_esInvalida() {
        assertNotNull(validarNuevaFeria(feriaValida().copy(puestos = listOf(PosicionPuesto(9, 0))), hoy))
        assertNotNull(validarNuevaFeria(feriaValida().copy(puestos = listOf(PosicionPuesto(-1, 0))), hoy))
    }

    @Test
    fun planoConPasillos_esValido() {
        val sinPasillo = plano(4, 3).filterNot { it.posX == 2 }
        assertNull(validarNuevaFeria(feriaValida().copy(puestos = sinPasillo), hoy))
    }

    @Test
    fun latitudFueraDeRango_esInvalida() {
        assertNotNull(validarNuevaFeria(feriaValida().copy(latitud = 123.0), hoy))
    }
}
