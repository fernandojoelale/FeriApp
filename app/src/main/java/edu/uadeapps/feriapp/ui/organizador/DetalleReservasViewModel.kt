package edu.uadeapps.feriapp.ui.organizador

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.createSavedStateHandle
import edu.uadeapps.feriapp.FeriApp
import edu.uadeapps.feriapp.domain.model.FeriaDetalle
import edu.uadeapps.feriapp.domain.model.ReservaDeFeria
import edu.uadeapps.feriapp.domain.usecase.ObtenerDetalleFeriaUseCase
import edu.uadeapps.feriapp.domain.usecase.ObtenerReservasDeFeriaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface DetalleReservasUiState {
    data object Cargando : DetalleReservasUiState
    data class Error(val mensaje: String) : DetalleReservasUiState
    data class Contenido(val detalle: FeriaDetalle, val reservas: List<ReservaDeFeria>) : DetalleReservasUiState
}

/** "Detalle con reservas" del organizador: plano de su feria + quién reservó cada puesto. */
class DetalleReservasViewModel(
    private val feriaId: Long,
    private val obtenerDetalleUseCase: ObtenerDetalleFeriaUseCase,
    private val obtenerReservasUseCase: ObtenerReservasDeFeriaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetalleReservasUiState>(DetalleReservasUiState.Cargando)
    val uiState: StateFlow<DetalleReservasUiState> = _uiState.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _uiState.value = DetalleReservasUiState.Cargando
            val detalle = obtenerDetalleUseCase(feriaId)
            val reservas = obtenerReservasUseCase(feriaId)
            val d = detalle.getOrNull()
            val r = reservas.getOrNull()
            _uiState.value = if (d != null && r != null) {
                DetalleReservasUiState.Contenido(d, r)
            } else {
                val causa = reservas.exceptionOrNull() ?: detalle.exceptionOrNull()
                DetalleReservasUiState.Error(causa?.message ?: "No pudimos cargar las reservas. Revisá tu conexión.")
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FeriApp)
                val feriaId = createSavedStateHandle().get<Long>("feriaId") ?: 0L
                DetalleReservasViewModel(feriaId, app.container.obtenerDetalleFeriaUseCase, app.container.obtenerReservasDeFeriaUseCase)
            }
        }
    }
}
