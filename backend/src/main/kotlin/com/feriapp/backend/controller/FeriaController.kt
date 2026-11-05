package com.feriapp.backend.controller

import com.feriapp.backend.dto.CrearFeriaRequest
import com.feriapp.backend.dto.FeriaDetalleDto
import com.feriapp.backend.dto.FeriaResponseDto
import com.feriapp.backend.model.Usuario
import com.feriapp.backend.service.FeriaService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/ferias")
class FeriaController(
    private val feriaService: FeriaService
) {

    // Público: cualquiera puede consultar las ferias.
    @GetMapping
    fun obtenerFerias(): ResponseEntity<List<FeriaResponseDto>> =
        ResponseEntity.ok(feriaService.obtenerTodas())

    // Público: detalle de una feria con sus puestos (mapa del predio).
    @GetMapping("/{id}")
    fun obtenerDetalle(@PathVariable id: Long): ResponseEntity<FeriaDetalleDto> =
        ResponseEntity.ok(feriaService.obtenerDetalle(id))

    // Solo ORGANIZADOR: sus propias ferias.
    @GetMapping("/mias")
    fun misFerias(@AuthenticationPrincipal organizador: Usuario): ResponseEntity<List<FeriaResponseDto>> =
        ResponseEntity.ok(feriaService.obtenerDeOrganizador(organizador.id))

    // Solo ORGANIZADOR: el organizador es quien tiene la sesión iniciada.
    @PostMapping
    fun crearFeria(
        @AuthenticationPrincipal organizador: Usuario,
        @RequestBody request: CrearFeriaRequest
    ): ResponseEntity<FeriaResponseDto> =
        ResponseEntity.status(HttpStatus.CREATED).body(feriaService.crearFeria(organizador.id, request))
}
