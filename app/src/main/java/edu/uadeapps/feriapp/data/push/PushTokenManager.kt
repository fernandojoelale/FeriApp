package edu.uadeapps.feriapp.data.push

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import edu.uadeapps.feriapp.data.local.SessionManager
import edu.uadeapps.feriapp.data.remote.FeriAppApi
import edu.uadeapps.feriapp.data.remote.dto.TokenPushDto
import edu.uadeapps.feriapp.domain.model.Rol
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume

/**
 * Le dice al servidor a qué celular enviar las notificaciones de reserva (RF04).
 * Solo las cuentas de organizador registran su celular. Si el proyecto todavía no tiene
 * Firebase configurado (falta google-services.json), no hace nada y la app funciona igual.
 */
class PushTokenManager(
    private val context: Context,
    private val api: FeriAppApi,
    private val sessionManager: SessionManager
) {
    private fun firebaseListo(): Boolean =
        try {
            FirebaseApp.getApps(context).isNotEmpty()
        } catch (e: Exception) {
            false
        }

    /** Registra el celular en el servidor si hay un organizador con sesión iniciada. */
    suspend fun registrarSiOrganizador(tokenNuevo: String? = null) {
        if (!firebaseListo()) return
        val sesion = sessionManager.session.firstOrNull() ?: return
        if (sesion.rol != Rol.ORGANIZADOR) return
        val token = tokenNuevo ?: obtenerToken() ?: return
        try {
            api.registrarTokenPush(TokenPushDto(token))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w("PushTokenManager", "No se pudo registrar el celular para notificaciones", e)
        }
    }

    /** Al cerrar sesión: el servidor deja de avisarle a este celular y se descarta su token. */
    suspend fun quitar() {
        if (!firebaseListo()) return
        try {
            api.borrarTokenPush()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w("PushTokenManager", "No se pudo quitar el celular del servidor", e)
        }
        try {
            FirebaseMessaging.getInstance().deleteToken()
        } catch (e: Exception) {
            // Se genera uno nuevo la próxima vez.
        }
    }

    private suspend fun obtenerToken(): String? =
        suspendCancellableCoroutine { cont ->
            try {
                FirebaseMessaging.getInstance().token.addOnCompleteListener { tarea ->
                    cont.resume(if (tarea.isSuccessful) tarea.result else null)
                }
            } catch (e: Exception) {
                cont.resume(null)
            }
        }
}
