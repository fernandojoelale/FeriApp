package edu.uadeapps.feriapp.ui.reservas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import edu.uadeapps.feriapp.FeriApp
import edu.uadeapps.feriapp.domain.model.Reserva
import edu.uadeapps.feriapp.domain.usecase.ObtenerMisReservasUseCase
import edu.uadeapps.feriapp.domain.usecase.ObtenerReservaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface MisReservasUiState {
    data object Cargando : MisReservasUiState
    data class Error(val mensaje: String) : MisReservasUiState
    data class Contenido(val reservas: List<Reserva>) : MisReservasUiState
}

class MisReservasViewModel(
    private val obtenerMisReservasUseCase: ObtenerMisReservasUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MisReservasUiState>(MisReservasUiState.Cargando)
    val uiState: StateFlow<MisReservasUiState> = _uiState.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            _uiState.value = MisReservasUiState.Cargando
            obtenerMisReservasUseCase().fold(
                onSuccess = { _uiState.value = MisReservasUiState.Contenido(it) },
                onFailure = { _uiState.value = MisReservasUiState.Error(it.message ?: "No pudimos cargar tus reservas.") }
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FeriApp)
                MisReservasViewModel(app.container.obtenerMisReservasUseCase)
            }
        }
    }
}

sealed interface ReservaUiState {
    data object Cargando : ReservaUiState
    data class Error(val mensaje: String) : ReservaUiState
    data class Contenido(val reserva: Reserva) : ReservaUiState
}

class ReservaConfirmadaViewModel(
    private val reservaId: Long,
    private val obtenerReservaUseCase: ObtenerReservaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReservaUiState>(ReservaUiState.Cargando)
    val uiState: StateFlow<ReservaUiState> = _uiState.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _uiState.value = ReservaUiState.Cargando
            obtenerReservaUseCase(reservaId).fold(
                onSuccess = { _uiState.value = ReservaUiState.Contenido(it) },
                onFailure = { _uiState.value = ReservaUiState.Error(it.message ?: "No pudimos cargar la reserva.") }
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FeriApp)
                val reservaId = createSavedStateHandle().get<Long>("reservaId") ?: 0L
                ReservaConfirmadaViewModel(reservaId, app.container.obtenerReservaUseCase)
            }
        }
    }
}
