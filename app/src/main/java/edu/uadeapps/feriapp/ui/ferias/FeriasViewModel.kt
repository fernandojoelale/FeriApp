package edu.uadeapps.feriapp.ui.ferias

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import edu.uadeapps.feriapp.FeriApp
import edu.uadeapps.feriapp.core.model.FeriasUiState
import edu.uadeapps.feriapp.data.repository.FeriaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FeriasViewModel(
    private val repository: FeriaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FeriasUiState>(FeriasUiState.Cargando)
    val uiState: StateFlow<FeriasUiState> = _uiState.asStateFlow()

    init {
        cargarFerias()
    }

    fun cargarFerias() {
        viewModelScope.launch {
            _uiState.value = FeriasUiState.Cargando
            repository.obtenerFerias()
                .onSuccess { feriasDominio ->
                    if (feriasDominio.isEmpty()) {
                        _uiState.value = FeriasUiState.Vacio
                    } else {
                        val feriasMapeadas = feriasDominio.map { feria ->
                            FeriaItem(
                                id = feria.id,
                                nombre = feria.nombre,
                                fecha = feria.fecha,
                                barrio = feria.zona,
                                rubro = feria.rubro,
                                distancia = "A calcular",
                                precio = "$ ${feria.precioPuesto.toInt()}",
                                precioNumerico = feria.precioPuesto,
                                puestosLibres = feria.puestosLibres
                            )
                        }
                        _uiState.value = FeriasUiState.Contenido(feriasMapeadas)
                    }
                }
                .onFailure { e ->
                    Log.e("FeriasViewModel", "Error al obtener ferias", e)
                    _uiState.value = FeriasUiState.Error("Error: ${e.localizedMessage ?: e.message}")
                }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FeriApp)
                FeriasViewModel(application.container.feriaRepository)
            }
        }
    }
}
