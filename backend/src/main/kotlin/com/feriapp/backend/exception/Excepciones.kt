package com.feriapp.backend.exception

class RecursoNoEncontradoException(mensaje: String) : RuntimeException(mensaje)

/** El puesto ya fue reservado por otra persona (RNF05). Se traduce a HTTP 409. */
class PuestoYaReservadoException(mensaje: String = "El puesto ya está reservado") : RuntimeException(mensaje)
