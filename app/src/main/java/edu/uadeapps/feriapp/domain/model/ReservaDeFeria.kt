package edu.uadeapps.feriapp.domain.model

/** Reserva vista por el organizador: quién reservó qué puesto de su feria. */
data class ReservaDeFeria(
    val reservaId: Long,
    val comprobante: String,
    val puestoId: Long,
    val numeroPuesto: Int,
    val ferianteNombre: String,
    val ferianteEmail: String,
    val ferianteRubro: String?,
    val fechaReserva: String
)
