package com.feriapp.backend.service

import com.feriapp.backend.model.Reserva
import com.feriapp.backend.model.Usuario

/** Envío de notificaciones push. Se separa de ReservaService para poder simularlo en los tests. */
interface NotificacionPush {
    /** Avisa al organizador de la feria que uno de sus puestos fue reservado (RF04). */
    fun avisarReservaAlOrganizador(reserva: Reserva)
}

/** Datos que viajan en el mensaje push; la app arma la notificación con ellos. */
data class MensajeReserva(val titulo: String, val cuerpo: String, val feriaId: Long)

fun mensajeDeReserva(reserva: Reserva): MensajeReserva =
    MensajeReserva(
        titulo = "Nuevo puesto reservado",
        cuerpo = "${reserva.feriante.nombre} reservó el puesto ${reserva.puesto.numero} de ${reserva.feria.nombre}",
        feriaId = reserva.feria.id
    )

fun Usuario.tieneTokenPush(): Boolean = !fcmToken.isNullOrBlank()
