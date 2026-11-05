package edu.uadeapps.feriapp.data.remote

import com.google.gson.Gson
import edu.uadeapps.feriapp.data.remote.dto.ErrorBodyDto
import edu.uadeapps.feriapp.domain.model.ErrorApp
import retrofit2.HttpException
import java.io.IOException

/** Traduce cualquier excepción de red/HTTP a un ErrorApp con un mensaje claro para el usuario. */
fun Throwable.toErrorApp(): ErrorApp = when (this) {
    is ErrorApp -> this
    is HttpException -> {
        val mensajeServidor = leerMensaje(this)
        when (code()) {
            400, 422 -> ErrorApp(ErrorApp.Tipo.VALIDACION, mensajeServidor ?: "Los datos ingresados no son válidos.")
            401 -> ErrorApp(ErrorApp.Tipo.NO_AUTORIZADO, mensajeServidor ?: "Tu sesión venció. Iniciá sesión de nuevo.")
            403 -> ErrorApp(ErrorApp.Tipo.NO_AUTORIZADO, "Tu sesión venció o no tenés permiso para esta acción.")
            404 -> ErrorApp(ErrorApp.Tipo.NO_ENCONTRADO, mensajeServidor ?: "No encontramos lo que buscabas.")
            409 -> ErrorApp(ErrorApp.Tipo.CONFLICTO, mensajeServidor ?: "La operación entró en conflicto.")
            else -> ErrorApp(ErrorApp.Tipo.SERVIDOR, "El servidor tuvo un problema. Intentá de nuevo en un momento.")
        }
    }
    is IOException -> ErrorApp(
        ErrorApp.Tipo.SIN_CONEXION,
        "No hay conexión con el servidor. Revisá tu internet e intentá de nuevo."
    )
    else -> ErrorApp(ErrorApp.Tipo.DESCONOCIDO, "Ocurrió un error inesperado. Intentá de nuevo.")
}

private fun leerMensaje(e: HttpException): String? =
    try {
        val cuerpo = e.response()?.errorBody()?.string()
        if (cuerpo.isNullOrBlank()) null else Gson().fromJson(cuerpo, ErrorBodyDto::class.java)?.mensaje
    } catch (ex: Exception) {
        null
    }
