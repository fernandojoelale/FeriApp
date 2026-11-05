package edu.uadeapps.feriapp.data.mapper

import edu.uadeapps.feriapp.data.remote.FeriaDto
import edu.uadeapps.feriapp.data.remote.dto.AuthResponseDto
import edu.uadeapps.feriapp.data.remote.dto.FeriaDetalleDto
import edu.uadeapps.feriapp.data.remote.dto.PuestoDto
import edu.uadeapps.feriapp.data.remote.dto.ReservaDto
import edu.uadeapps.feriapp.domain.Feria
import edu.uadeapps.feriapp.domain.model.EstadoPuesto
import edu.uadeapps.feriapp.domain.model.FeriaDetalle
import edu.uadeapps.feriapp.domain.model.Puesto
import edu.uadeapps.feriapp.domain.model.Reserva
import edu.uadeapps.feriapp.domain.model.Rol
import edu.uadeapps.feriapp.domain.model.Session

fun FeriaDto.toDomain(): Feria {
    return Feria(
        id = this.id,
        nombre = this.nombre,
        fecha = this.fecha,
        zona = this.zona,
        rubro = this.rubro,
        precioPuesto = this.precioPuesto,
        puestosLibres = this.puestosLibres,
        horaInicio = this.horaInicio,
        horaFin = this.horaFin,
        direccion = this.direccion,
        latitud = this.latitud,
        longitud = this.longitud,
        organizadorNombre = this.organizadorNombre
    )
}

fun PuestoDto.toDomain() = Puesto(
    id = id,
    numero = numero,
    posX = posX,
    posY = posY,
    estado = if (estado == "RESERVADO") EstadoPuesto.RESERVADO else EstadoPuesto.DISPONIBLE
)

fun FeriaDetalleDto.toDomain() = FeriaDetalle(
    feria = feria.toDomain(),
    puestos = puestos.map { it.toDomain() }
)

fun ReservaDto.toDomain() = Reserva(
    id = id,
    comprobante = comprobante,
    puestoId = puestoId,
    numeroPuesto = numeroPuesto,
    feriaId = feriaId,
    feriaNombre = feriaNombre,
    feriaFecha = feriaFecha,
    horaInicio = horaInicio,
    horaFin = horaFin,
    direccion = direccion,
    zona = zona,
    latitud = latitud,
    longitud = longitud,
    precioPuesto = precioPuesto
)

fun AuthResponseDto.toSession() = Session(
    token = token,
    rol = runCatching { Rol.valueOf(rol) }.getOrDefault(Rol.FERIANTE),
    nombre = nombre,
    email = email,
    usuarioId = usuarioId
)
