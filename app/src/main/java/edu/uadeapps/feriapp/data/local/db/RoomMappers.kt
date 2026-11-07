package edu.uadeapps.feriapp.data.local.db

import edu.uadeapps.feriapp.data.remote.FeriaDto
import edu.uadeapps.feriapp.data.remote.dto.PuestoDto
import edu.uadeapps.feriapp.data.remote.dto.ReservaDto
import edu.uadeapps.feriapp.domain.Feria
import edu.uadeapps.feriapp.domain.model.EstadoPuesto
import edu.uadeapps.feriapp.domain.model.Puesto
import edu.uadeapps.feriapp.domain.model.Reserva

fun FeriaDto.toEntity(coleccion: String, ahora: Long) = FeriaEntity(
    id = id, coleccion = coleccion, nombre = nombre, fecha = fecha, horaInicio = horaInicio,
    horaFin = horaFin, direccion = direccion, zona = zona, latitud = latitud, longitud = longitud,
    rubro = rubro, precioPuesto = precioPuesto, puestosLibres = puestosLibres,
    organizadorNombre = organizadorNombre, actualizadoEn = ahora
)

/** Una feria leída de Room siempre se marca como "datos guardados". */
fun FeriaEntity.toDomain() = Feria(
    id = id, nombre = nombre, fecha = fecha, zona = zona, rubro = rubro, precioPuesto = precioPuesto,
    puestosLibres = puestosLibres, horaInicio = horaInicio, horaFin = horaFin, direccion = direccion,
    latitud = latitud, longitud = longitud, organizadorNombre = organizadorNombre,
    desdeCache = true, actualizadoEn = actualizadoEn
)

fun PuestoDto.toEntity(feriaId: Long, ahora: Long) = PuestoEntity(
    id = id, feriaId = feriaId, numero = numero, posX = posX, posY = posY, estado = estado, actualizadoEn = ahora
)

fun PuestoEntity.toDomain() = Puesto(
    id = id, numero = numero, posX = posX, posY = posY,
    estado = if (estado == "RESERVADO") EstadoPuesto.RESERVADO else EstadoPuesto.DISPONIBLE
)

fun ReservaDto.toEntity(ahora: Long) = ReservaEntity(
    id = id, comprobante = comprobante, puestoId = puestoId, numeroPuesto = numeroPuesto, feriaId = feriaId,
    feriaNombre = feriaNombre, feriaFecha = feriaFecha, horaInicio = horaInicio, horaFin = horaFin,
    direccion = direccion, zona = zona, latitud = latitud, longitud = longitud,
    precioPuesto = precioPuesto, actualizadoEn = ahora
)

fun ReservaEntity.toDomain() = Reserva(
    id = id, comprobante = comprobante, puestoId = puestoId, numeroPuesto = numeroPuesto, feriaId = feriaId,
    feriaNombre = feriaNombre, feriaFecha = feriaFecha, horaInicio = horaInicio, horaFin = horaFin,
    direccion = direccion, zona = zona, latitud = latitud, longitud = longitud,
    precioPuesto = precioPuesto, desdeCache = true, actualizadoEn = actualizadoEn
)
