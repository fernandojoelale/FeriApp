package edu.uadeapps.feriapp.ui.organizador

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import edu.uadeapps.feriapp.FeriApp
import edu.uadeapps.feriapp.domain.Feria
import edu.uadeapps.feriapp.domain.model.NuevaFeria
import edu.uadeapps.feriapp.domain.usecase.CrearFeriaUseCase
import edu.uadeapps.feriapp.domain.usecase.ObtenerMisFeriasUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface MisFeriasUiState {
    data object Cargando : MisFeriasUiState
    data class Error(val mensaje: String) : MisFeriasUiState
    data class Contenido(val ferias: List<Feria>) : MisFeriasUiState
}

class MisFeriasViewModel(
    private val obtenerMisFeriasUseCase: ObtenerMisFeriasUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MisFeriasUiState>(MisFeriasUiState.Cargando)
    val uiState: StateFlow<MisFeriasUiState> = _uiState.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            _uiState.value = MisFeriasUiState.Cargando
            obtenerMisFeriasUseCase().fold(
                onSuccess = { _uiState.value = MisFeriasUiState.Contenido(it.sortedBy { f -> f.fecha }) },
                onFailure = { _uiState.value = MisFeriasUiState.Error(it.message ?: "No pudimos cargar tus ferias.") }
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FeriApp)
                MisFeriasViewModel(app.container.obtenerMisFeriasUseCase)
            }
        }
    }
}

data class PublicarUiState(
    val publicando: Boolean = false,
    val error: String? = null,
    val publicada: Boolean = false
)

class PublicarFeriaViewModel(
    private val crearFeriaUseCase: CrearFeriaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PublicarUiState())
    val uiState: StateFlow<PublicarUiState> = _uiState.asStateFlow()

    /** Recibe lo que escribió el usuario (texto) y arma la feria; los números mal escritos se avisan acá. */
    fun publicar(
        nombre: String, fecha: String, horaInicio: String, horaFin: String,
        direccion: String, zona: String, latitud: String, longitud: String,
        rubro: String, precio: String, cantidad: String
    ) {
        if (_uiState.value.publicando) return

        val lat = latitud.trim().replace(',', '.').toDoubleOrNull()
        val lon = longitud.trim().replace(',', '.').toDoubleOrNull()
        val precioNum = precio.trim().replace(',', '.').toDoubleOrNull()
        val cantidadNum = cantidad.trim().toIntOrNull()
        val errorNumerico = when {
            precioNum == null -> "Ingresá el precio por puesto (solo números)."
            cantidadNum == null -> "Ingresá la cantidad de puestos (número entero)."
            lat == null || lon == null -> "Ingresá latitud y longitud con números (ej: -34.5875)."
            else -> null
        }
        if (errorNumerico != null || precioNum == null || cantidadNum == null || lat == null || lon == null) {
            _uiState.value = PublicarUiState(error = errorNumerico)
            return
        }

        val nueva = NuevaFeria(
            nombre = nombre.trim(),
            fecha = fecha.trim(),
            horaInicio = horaInicio.trim(),
            horaFin = horaFin.trim(),
            direccion = direccion.trim(),
            zona = zona.trim(),
            latitud = lat,
            longitud = lon,
            rubro = rubro.trim(),
            precioPuesto = precioNum,
            cantidadPuestos = cantidadNum
        )
        viewModelScope.launch {
            _uiState.value = PublicarUiState(publicando = true)
            crearFeriaUseCase(nueva).fold(
                onSuccess = { _uiState.value = PublicarUiState(publicada = true) },
                onFailure = { _uiState.value = PublicarUiState(error = it.message ?: "No pudimos publicar la feria.") }
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FeriApp)
                PublicarFeriaViewModel(app.container.crearFeriaUseCase)
            }
        }
    }
}
