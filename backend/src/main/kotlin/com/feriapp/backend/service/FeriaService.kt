package com.feriapp.backend.service

import com.feriapp.backend.dto.CrearFeriaRequest
import com.feriapp.backend.model.EstadoPuesto
import com.feriapp.backend.model.Feria
import com.feriapp.backend.model.Puesto
import com.feriapp.backend.repository.FeriaRepository
import com.feriapp.backend.repository.PuestoRepository
import com.feriapp.backend.repository.UsuarioRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FeriaService(
    private val feriaRepository: FeriaRepository,
    private val usuarioRepository: UsuarioRepository,
    private val puestoRepository: PuestoRepository
) {
    fun obtenerTodas(): List<Feria> = feriaRepository.findAll()

    @Transactional // si algo falla, se deshace toda la operacion
    fun crearFeria(request: CrearFeriaRequest): Feria {
        val organizador = usuarioRepository.findById(request.organizadorId)
            .orElseThrow { Exception("Organizador no encontrado") }

        val nuevaFeria = Feria(
            organizador = organizador,
            nombre = request.nombre,
            fecha = request.fecha,
            horaInicio = request.horaInicio,
            horaFin = request.horaFin,
            direccion = request.direccion,
            zona = request.zona,
            latitud = request.latitud,
            longitud = request.longitud,
            rubro = request.rubro,
            precioPuesto = request.precioPuesto
        )

        val feriaGuardada = feriaRepository.save(nuevaFeria)


        for (i in 1..request.cantidadPuestos) {
            val nuevoPuesto = Puesto(
                feria = feriaGuardada,
                numero = i,
                posX = i * 10,
                posY = 10,
                estado = EstadoPuesto.DISPONIBLE
            )
            puestoRepository.save(nuevoPuesto)
        }

        return feriaGuardada
    }
}