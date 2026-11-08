package edu.uadeapps.feriapp

import edu.uadeapps.feriapp.domain.FiltroFecha
import edu.uadeapps.feriapp.domain.cumplePrecioMaximo
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class FiltrosTest {
    private val hoy = LocalDate.of(2026, 11, 7)

    @Test
    fun cualquierFecha_aceptaTodo() {
        assertTrue(FiltroFecha.Cualquiera.cumple("2020-01-01", hoy))
    }

    @Test
    fun estaSemana_incluyeHoyYSieteDias_peroNoPasadoNiMasLejos() {
        val f = FiltroFecha.ProximosDias(7, "Esta semana")
        assertTrue(f.cumple("2026-11-07", hoy))
        assertTrue(f.cumple("2026-11-14", hoy))
        assertFalse(f.cumple("2026-11-15", hoy))
        assertFalse(f.cumple("2026-11-06", hoy))
    }

    @Test
    fun diaElegido_soloEseDia() {
        val f = FiltroFecha.Dia(LocalDate.of(2026, 12, 20))
        assertTrue(f.cumple("2026-12-20", hoy))
        assertFalse(f.cumple("2026-12-21", hoy))
    }

    @Test
    fun fechaInvalida_noCumpleFiltroActivo() {
        assertFalse(FiltroFecha.ProximosDias(30, "Este mes").cumple("basura", hoy))
    }

    @Test
    fun precioMaximo() {
        assertTrue(cumplePrecioMaximo(18000.0, null))
        assertTrue(cumplePrecioMaximo(20000.0, 20000.0))
        assertFalse(cumplePrecioMaximo(20001.0, 20000.0))
    }
}
