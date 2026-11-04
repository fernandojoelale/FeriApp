package com.feriapp.backend.controller

import com.feriapp.backend.dto.ReservaRequest
import com.feriapp.backend.dto.ReservaResponseDto
import com.feriapp.backend.dto.toDto
import com.feriapp.backend.service.ReservaService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/reservas")
class ReservaController(
    private val reservaService: ReservaService
) {

    // Los errores (404 / 409) los traduce ManejadorErrores.
    @PostMapping
    fun reservarPuesto(@RequestBody request: ReservaRequest): ResponseEntity<ReservaResponseDto> =
        ResponseEntity.ok(reservaService.reservarPuesto(request).toDto())
}
