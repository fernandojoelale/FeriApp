package com.feriapp.backend.controller

import com.feriapp.backend.dto.ReservaRequest
import com.feriapp.backend.dto.ReservaResponseDto
import com.feriapp.backend.dto.toDto
import com.feriapp.backend.model.Usuario
import com.feriapp.backend.service.ReservaService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

// Todo este controlador es solo para FERIANTE (ver SecurityConfig).
// Los errores (404 / 409) los traduce ManejadorErrores.
@RestController
@RequestMapping("/api/reservas")
class ReservaController(
    private val reservaService: ReservaService
) {

    @PostMapping
    fun reservarPuesto(
        @AuthenticationPrincipal feriante: Usuario,
        @RequestBody request: ReservaRequest
    ): ResponseEntity<ReservaResponseDto> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(reservaService.reservarPuesto(feriante, request.puestoId).toDto())

    @GetMapping("/mias")
    fun misReservas(@AuthenticationPrincipal feriante: Usuario): ResponseEntity<List<ReservaResponseDto>> =
        ResponseEntity.ok(reservaService.obtenerMisReservas(feriante).map { it.toDto() })

    @GetMapping("/{id}")
    fun obtenerReserva(
        @AuthenticationPrincipal feriante: Usuario,
        @PathVariable id: Long
    ): ResponseEntity<ReservaResponseDto> =
        ResponseEntity.ok(reservaService.obtenerReserva(feriante, id).toDto())
}
