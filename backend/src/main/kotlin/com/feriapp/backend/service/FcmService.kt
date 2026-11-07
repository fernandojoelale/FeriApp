package com.feriapp.backend.service

import com.feriapp.backend.model.Reserva
import com.feriapp.backend.repository.UsuarioRepository
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.AndroidConfig
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.MessagingErrorCode
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.io.FileInputStream
import java.util.concurrent.Executors

/**
 * Envía las notificaciones push con el SDK de administración de Firebase (RF04).
 *
 * Se activa solo si se indica la ruta del JSON de la cuenta de servicio, por ejemplo:
 *   FIREBASE_CREDENTIALS=C:\claves\feriapp-firebase.json   (variable de entorno)
 * Si no está configurado, el backend funciona igual y solo deja un aviso en el log.
 */
@Service
class FcmService(
    private val usuarioRepository: UsuarioRepository,
    @Value("\${firebase.credentials-path:}") private val rutaCredenciales: String
) : NotificacionPush {

    private val log = LoggerFactory.getLogger(FcmService::class.java)
    private val ejecutor = Executors.newSingleThreadExecutor()
    private var habilitado = false

    @PostConstruct
    fun iniciar() {
        if (rutaCredenciales.isBlank()) {
            log.warn("Firebase no configurado (falta FIREBASE_CREDENTIALS): las notificaciones push estan desactivadas.")
            return
        }
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FileInputStream(rutaCredenciales).use { archivo ->
                    val opciones = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(archivo))
                        .build()
                    FirebaseApp.initializeApp(opciones)
                }
            }
            habilitado = true
            log.info("Firebase Cloud Messaging listo.")
        } catch (e: Exception) {
            log.error("No se pudo iniciar Firebase con '{}': {}", rutaCredenciales, e.message)
        }
    }

    @PreDestroy
    fun cerrar() {
        ejecutor.shutdown()
    }

    override fun avisarReservaAlOrganizador(reserva: Reserva) {
        if (!habilitado) return
        val organizador = reserva.feria.organizador
        val token = organizador.fcmToken
        if (token.isNullOrBlank()) return
        val mensaje = mensajeDeReserva(reserva)

        // Se envía después del commit y en otro hilo: la reserva no espera a Firebase.
        val envio = Runnable { enviar(organizador.id, token, mensaje) }
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronization {
                override fun afterCommit() {
                    ejecutor.execute(envio)
                }
            })
        } else {
            ejecutor.execute(envio)
        }
    }

    private fun enviar(organizadorId: Long, token: String, datos: MensajeReserva) {
        try {
            // Mensaje solo de datos y prioridad alta: la app arma la notificacion (con canal y acceso
            // directo al detalle de la feria) tanto con la app abierta como cerrada.
            val mensaje = Message.builder()
                .setToken(token)
                .putData("tipo", "RESERVA")
                .putData("titulo", datos.titulo)
                .putData("cuerpo", datos.cuerpo)
                .putData("feriaId", datos.feriaId.toString())
                .setAndroidConfig(AndroidConfig.builder().setPriority(AndroidConfig.Priority.HIGH).build())
                .build()
            FirebaseMessaging.getInstance().send(mensaje)
        } catch (e: FirebaseMessagingException) {
            if (e.messagingErrorCode == MessagingErrorCode.UNREGISTERED) {
                // El celular ya no existe o se desinstaló la app: se borra el token.
                usuarioRepository.findById(organizadorId).ifPresent {
                    if (it.fcmToken == token) usuarioRepository.save(it.copy(fcmToken = null))
                }
            }
            log.warn("No se pudo enviar la notificacion: {}", e.message)
        } catch (e: Exception) {
            log.warn("No se pudo enviar la notificacion: {}", e.message)
        }
    }
}
