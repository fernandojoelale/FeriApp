package com.feriapp.backend.exception

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException

data class ErrorResponse(val codigo: String, val mensaje: String)

@RestControllerAdvice
class ManejadorErrores {

    @ExceptionHandler(RecursoNoEncontradoException::class)
    fun noEncontrado(e: RecursoNoEncontradoException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse("NO_ENCONTRADO", e.message ?: "Recurso no encontrado"))

    @ExceptionHandler(PuestoYaReservadoException::class)
    fun conflicto(e: PuestoYaReservadoException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResponse("PUESTO_RESERVADO", e.message ?: "El puesto ya está reservado"))

    @ExceptionHandler(SolicitudInvalidaException::class)
    fun invalida(e: SolicitudInvalidaException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse("SOLICITUD_INVALIDA", e.message ?: "Datos inválidos"))

    // Dos reservas simultáneas: la restricción UNIQUE de la base frena a la segunda.
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun integridad(e: DataIntegrityViolationException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResponse("PUESTO_RESERVADO", "El puesto ya está reservado"))

    // Errores del login/registro (AuthService): devolver el motivo en el mismo formato.
    @ExceptionHandler(ResponseStatusException::class)
    fun estado(e: ResponseStatusException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(e.statusCode)
            .body(ErrorResponse(e.statusCode.value().toString(), e.reason ?: "Error"))
}
