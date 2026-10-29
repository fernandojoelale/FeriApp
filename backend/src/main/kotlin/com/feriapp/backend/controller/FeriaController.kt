package com.feriapp.backend.controller

import com.feriapp.backend.dto.CrearFeriaRequest
import com.feriapp.backend.dto.FeriaResponseDto
import com.feriapp.backend.service.FeriaService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/ferias")
class FeriaController(
    private val feriaService: FeriaService
) {

    @GetMapping
    fun obtenerFerias(): ResponseEntity<List<FeriaResponseDto>> {
        val ferias = feriaService.obtenerTodas()
        return ResponseEntity.ok(ferias)
    }

    @PostMapping
    fun crearFeria(@RequestBody request: CrearFeriaRequest): ResponseEntity<FeriaResponseDto> {
        val nuevaFeria = feriaService.crearFeria(request)
        return ResponseEntity.ok(nuevaFeria)
    }
}