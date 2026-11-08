package com.feriapp.backend.service

import com.feriapp.backend.dto.CrearFeriaRequest
import com.feriapp.backend.dto.FeriaDetalleDto
import com.feriapp.backend.dto.FeriaResponseDto
import com.feriapp.backend.dto.ReservaDeFeriaDto
import com.feriapp.backend.dto.toReservaDeFeriaDto
import com.feriapp.backend.dto.toDto
import com.feriapp.backend.exception.RecursoNoEncontradoException
import com.feriapp.backend.exception.SolicitudInvalidaException
import com.feriapp.backend.model.EstadoPuesto
import com.feriapp.backend.model.Feria
import com.feriapp.backend.model.Puesto
import com.feriapp.backend.repository.FeriaRepository
import com.feriapp.backend.repository.PuestoRepository
import com.feriapp.backend.repository.ReservaRepository
import com.feriapp.backend.repository.UsuarioRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException

@Service
class FeriaService(
    private val feriaRepository: FeriaRepository,
    private val usuarioRepository: UsuarioRepository,
    private val puestoRepository: PuestoRepository,
    private val reservaRepository: ReservaRepository
) {
    companion object {
        private const val COLUMNAS_PLANO = 5
        private const val MAX_PUESTOS = 200
        private const val MAX_COLUMNAS = 10
        private const val MAX_FILAS = 30
    }

    fun obtenerTodas(): List<FeriaResponseDto> =
        feriaRepository.findAll().map { it.conPuestosLibres() }

    fun obtenerDeOrganizador(organizadorId: Long): List<FeriaResponseDto> =
        feriaRepository.findByOrganizadorId(organizadorId).map { it.conPuestosLibres() }

    fun obtenerDetalle(feriaId: Long): FeriaDetalleDto {
        val feria = feriaRepository.findById(feriaId)
            .orElseThrow { RecursoNoEncontradoException("Feria no encontrada") }
        val puestos = puestoRepository.findByFeriaId(feriaId).sortedBy { it.numero }
        val libres = puestos.count { it.estado == EstadoPuesto.DISPONIBLE }
        return FeriaDetalleDto(feria = feria.toDto(libres, puestos.size), puestos = puestos.map { it.toDto() })
    }

    @Transactional // si algo falla, se deshace toda la operacion
    fun crearFeria(organizadorId: Long, request: CrearFeriaRequest): FeriaResponseDto {
        validar(request)

        val organizador = usuarioRepository.findById(organizadorId)
            .orElseThrow { RecursoNoEncontradoException("Organizador no encontrado") }

        val feriaGuardada = feriaRepository.save(
            Feria(
                organizador = organizador,
                nombre = request.nombre.trim(),
                fecha = request.fecha,
                horaInicio = request.horaInicio,
                horaFin = request.horaFin,
                direccion = request.direccion.trim(),
                zona = request.zona.trim(),
                latitud = request.latitud,
                longitud = request.longitud,
                rubro = request.rubro.trim(),
                precioPuesto = request.precioPuesto
            )
        )

        // Plano del predio: las posiciones que eligió el organizador o, si no envió ninguna,
        // una grilla de 5 columnas. Se numeran de izquierda a derecha y de arriba hacia abajo.
        val posiciones = request.puestos
            ?.sortedWith(compareBy({ it.posY }, { it.posX }))
            ?.map { it.posX to it.posY }
            ?: (1..(request.cantidadPuestos ?: 0)).map { i -> (i - 1) % COLUMNAS_PLANO to (i - 1) / COLUMNAS_PLANO }

        posiciones.forEachIndexed { indice, (x, y) ->
            puestoRepository.save(
                Puesto(
                    feria = feriaGuardada,
                    numero = indice + 1,
                    posX = x,
                    posY = y,
                    estado = EstadoPuesto.DISPONIBLE
                )
            )
        }

        return feriaGuardada.conPuestosLibres()
    }

    /** Reservas de una feria, solo para su organizador. Una feria ajena se trata como inexistente. */
    fun obtenerReservasDeFeria(organizadorId: Long, feriaId: Long): List<ReservaDeFeriaDto> {
        val feria = feriaRepository.findById(feriaId)
            .orElseThrow { RecursoNoEncontradoException("Feria no encontrada") }
        if (feria.organizador.id != organizadorId) throw RecursoNoEncontradoException("Feria no encontrada")
        return reservaRepository.findByFeriaIdOrderByPuestoNumero(feriaId).map { it.toReservaDeFeriaDto() }
    }

    private fun Feria.conPuestosLibres(): FeriaResponseDto {
        val libres = puestoRepository.countByFeriaIdAndEstado(id, EstadoPuesto.DISPONIBLE)
        val reservados = puestoRepository.countByFeriaIdAndEstado(id, EstadoPuesto.RESERVADO)
        return toDto(libres, libres + reservados)
    }

    private fun validar(r: CrearFeriaRequest) {
        if (r.nombre.isBlank()) throw SolicitudInvalidaException("El nombre de la feria es obligatorio")
        if (r.direccion.isBlank()) throw SolicitudInvalidaException("La dirección es obligatoria")
        if (r.zona.isBlank()) throw SolicitudInvalidaException("La zona es obligatoria")
        if (r.rubro.isBlank()) throw SolicitudInvalidaException("El rubro es obligatorio")
        if (r.precioPuesto <= 0) throw SolicitudInvalidaException("El precio por puesto debe ser mayor a 0")
        val plano = r.puestos
        if (plano != null) {
            if (plano.size !in 1..MAX_PUESTOS) {
                throw SolicitudInvalidaException("La cantidad de puestos debe estar entre 1 y $MAX_PUESTOS")
            }
            if (plano.any { it.posX !in 0 until MAX_COLUMNAS || it.posY !in 0 until MAX_FILAS }) {
                throw SolicitudInvalidaException("Hay puestos fuera del plano del predio")
            }
            if (plano.map { it.posX to it.posY }.toSet().size != plano.size) {
                throw SolicitudInvalidaException("Hay dos puestos en la misma posición del plano")
            }
        } else if ((r.cantidadPuestos ?: 0) !in 1..MAX_PUESTOS) {
            throw SolicitudInvalidaException("La cantidad de puestos debe estar entre 1 y $MAX_PUESTOS")
        }

        try {
            val fecha = LocalDate.parse(r.fecha)
            if (fecha.isBefore(LocalDate.now())) throw SolicitudInvalidaException("Elegí una fecha futura")
            val inicio = LocalTime.parse(r.horaInicio)
            val fin = LocalTime.parse(r.horaFin)
            if (!fin.isAfter(inicio)) {
                throw SolicitudInvalidaException("La hora de fin debe ser posterior al inicio")
            }
        } catch (e: DateTimeParseException) {
            throw SolicitudInvalidaException("Fecha u horario inválidos. Formato: AAAA-MM-DD y HH:mm")
        }
    }
}
