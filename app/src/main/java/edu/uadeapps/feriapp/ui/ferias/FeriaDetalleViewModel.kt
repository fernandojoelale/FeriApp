package edu.uadeapps.feriapp.ui.ferias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import edu.uadeapps.feriapp.FeriApp
import edu.uadeapps.feriapp.domain.model.FeriaDetalle
import edu.uadeapps.feriapp.domain.usecase.ObtenerDetalleFeriaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface DetalleUiState {
    data object Cargando : DetalleUiState
    data class Error(val mensaje: String) : DetalleUiState
    data class Contenido(val detalle: FeriaDetalle) : DetalleUiState
}

class FeriaDetalleViewModel(
    private val feriaId: Long,
    private val obtenerDetalleUseCase: ObtenerDetalleFeriaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetalleUiState>(DetalleUiState.Cargando)
    val uiState: StateFlow<DetalleUiState> = _uiState.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _uiState.value = DetalleUiState.Cargando
            obtenerDetalleUseCase(feriaId).fold(
                onSuccess = { _uiState.value = DetalleUiState.Contenido(it) },
                onFailure = { _uiState.value = DetalleUiState.Error(it.message ?: "No pudimos cargar la feria.") }
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FeriApp)
                val feriaId = createSavedStateHandle().get<Long>("feriaId") ?: 0L
                FeriaDetalleViewModel(feriaId, app.container.obtenerDetalleFeriaUseCase)
            }
        }
    }
}
