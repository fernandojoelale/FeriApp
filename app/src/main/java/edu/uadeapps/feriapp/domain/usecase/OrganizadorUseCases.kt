package edu.uadeapps.feriapp.domain.usecase

import edu.uadeapps.feriapp.domain.Feria
import edu.uadeapps.feriapp.domain.model.ErrorApp
import edu.uadeapps.feriapp.domain.model.NuevaFeria
import edu.uadeapps.feriapp.domain.model.Rol
import edu.uadeapps.feriapp.domain.repository.AuthRepository
import edu.uadeapps.feriapp.domain.repository.DetalleFeriaRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException

const val MAX_PUESTOS_POR_FERIA = 200

/**
 * Validaciones de RF01 que se pueden hacer sin red. Devuelve el mensaje de error
 * para mostrar en pantalla, o null si los datos están bien. (El servidor vuelve a validar.)
 */
fun validarNuevaFeria(f: NuevaFeria, hoy: LocalDate = LocalDate.now()): String? {
    if (f.nombre.isBlank()) return "Ingresá el nombre de la feria."
    if (f.direccion.isBlank()) return "Ingresá la dirección."
    if (f.zona.isBlank()) return "Ingresá la zona o barrio."
    if (f.rubro.isBlank()) return "Ingresá el rubro."
    try {
        val fecha = LocalDate.parse(f.fecha)
        if (fecha.isBefore(hoy)) return "Elegí una fecha futura."
    } catch (e: DateTimeParseException) {
        return "La fecha debe tener el formato AAAA-MM-DD (ej: 2026-12-20)."
    }
    try {
        val inicio = LocalTime.parse(f.horaInicio)
        val fin = LocalTime.parse(f.horaFin)
        if (!fin.isAfter(inicio)) return "La hora de fin debe ser posterior a la de inicio."
    } catch (e: DateTimeParseException) {
        return "Los horarios deben tener el formato HH:mm (ej: 10:00)."
    }
    if (f.precioPuesto <= 0) return "El precio por puesto debe ser mayor a 0."
    if (f.cantidadPuestos !in 1..MAX_PUESTOS_POR_FERIA) {
        return "La cantidad de puestos debe estar entre 1 y $MAX_PUESTOS_POR_FERIA."
    }
    if (f.latitud !in -90.0..90.0 || f.longitud !in -180.0..180.0) {
        return "La ubicación (latitud/longitud) no es válida."
    }
    return null
}

/** RF01: solo un ORGANIZADOR con sesión iniciada puede publicar una feria. */
class CrearFeriaUseCase(
    private val feriaRepository: DetalleFeriaRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(nueva: NuevaFeria): Result<Feria> {
        val sesion = authRepository.session.first()
            ?: return Result.failure(
                ErrorApp(ErrorApp.Tipo.NO_AUTORIZADO, "Iniciá sesión como organizador para publicar una feria.")
            )
        if (sesion.rol != Rol.ORGANIZADOR) {
            return Result.failure(
                ErrorApp(ErrorApp.Tipo.NO_AUTORIZADO, "Solo las cuentas de organizador pueden publicar ferias.")
            )
        }
        validarNuevaFeria(nueva)?.let {
            return Result.failure(ErrorApp(ErrorApp.Tipo.VALIDACION, it))
        }
        return feriaRepository.crearFeria(nueva)
    }
}

class ObtenerMisFeriasUseCase(private val repository: DetalleFeriaRepository) {
    suspend operator fun invoke(): Result<List<Feria>> = repository.obtenerMisFerias()
}
