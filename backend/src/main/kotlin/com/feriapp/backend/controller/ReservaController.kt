package com.feriapp.backend.controller

import com.feriapp.backend.dto.ReservaRequest
import com.feriapp.backend.model.Reserva
import com.feriapp.backend.service.ReservaService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/reservas")
class ReservaController(
    private val reservaService: ReservaService
) {

    @PostMapping
    fun reservarPuesto(@RequestBody request: ReservaRequest): ResponseEntity<Reserva> {
        return try {
            val reserva = reservaService.reservarPuesto(request)
            ResponseEntity.ok(reserva)
        } catch (e: Exception) {
            // Si el puesto ya está ocupado, devuelve un error 400 (Bad Request)
            ResponseEntity.badRequest().build()
        }
    }
}