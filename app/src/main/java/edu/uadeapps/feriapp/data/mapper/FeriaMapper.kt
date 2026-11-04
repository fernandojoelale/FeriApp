package edu.uadeapps.feriapp.data.mapper

import edu.uadeapps.feriapp.data.remote.FeriaDto
import edu.uadeapps.feriapp.domain.Feria

fun FeriaDto.toDomain(): Feria {
    return Feria(
        id = this.id,
        nombre = this.nombre,
        fecha = this.fecha,
        zona = this.zona,
        rubro = this.rubro,
        precioPuesto = this.precioPuesto,
        puestosLibres = this.puestosLibres
    )
}