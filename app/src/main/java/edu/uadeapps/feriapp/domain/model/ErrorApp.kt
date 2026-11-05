package edu.uadeapps.feriapp.domain.model

/**
 * Error de la aplicación con un tipo claro, para que la UI decida qué mostrar
 * (sin conexión, conflicto de reserva, sesión vencida, etc.).
 */
class ErrorApp(val tipo: Tipo, mensaje: String) : Exception(mensaje) {
    enum class Tipo {
        SIN_CONEXION,
        CREDENCIALES,
        VALIDACION,
        NO_AUTORIZADO,
        NO_ENCONTRADO,
        CONFLICTO,
        SERVIDOR,
        DESCONOCIDO
    }
}
