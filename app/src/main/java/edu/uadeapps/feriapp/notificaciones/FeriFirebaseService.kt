package edu.uadeapps.feriapp.notificaciones

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import edu.uadeapps.feriapp.FeriApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/** Recibe los mensajes push de Firebase (RF04) y los convierte en notificaciones. */
class FeriFirebaseService : FirebaseMessagingService() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /** Firebase le asignó un token nuevo a este celular: se le avisa al servidor. */
    override fun onNewToken(token: String) {
        val contenedor = (application as FeriApp).container
        scope.launch { contenedor.pushTokenManager.registrarSiOrganizador(token) }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val datos = message.data
        if (datos["tipo"] != "RESERVA") return
        val feriaId = datos["feriaId"]?.toLongOrNull() ?: return
        Notificaciones.mostrarReserva(
            context = this,
            titulo = datos["titulo"] ?: "Nuevo puesto reservado",
            cuerpo = datos["cuerpo"] ?: "Uno de tus puestos fue reservado.",
            feriaId = feriaId
        )
    }
}
