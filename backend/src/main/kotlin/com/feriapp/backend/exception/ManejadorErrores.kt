package com.feriapp.backend.exception

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

data class ErrorResponse(val codigo: String, val mensaje: String)

@RestControllerAdvice
class ManejadorErrores {

    @ExceptionHandler(RecursoNoEncontradoException::class)
    fun noEncontrado(e: RecursoNoEncontradoException) =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse("NO_ENCONTRADO", e.message ?: "Recurso no encontrado"))

    @ExceptionHandler(PuestoYaReservadoException::class)
    fun conflicto(e: PuestoYaReservadoException) =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResponse("PUESTO_RESERVADO", e.message ?: "El puesto ya está reservado"))

    // Dos reservas simultáneas: la restricción UNIQUE de la base frena a la segunda.
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun integridad(e: DataIntegrityViolationException) =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResponse("PUESTO_RESERVADO", "El puesto ya está reservado"))
}
