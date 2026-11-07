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

    private val _refrescando = MutableStateFlow(false)
    val refrescando: StateFlow<Boolean> = _refrescando.asStateFlow()

    fun cargarFerias() {
        viewModelScope.launch {
            _uiState.value = FeriasUiState.Cargando
            consultarFerias()
        }
    }

    /**
     * Actualiza la lista sin tapar la pantalla con el spinner (al volver a la pantalla o al
     * deslizar hacia abajo). Si falla y ya había ferias a la vista, se conservan las que había.
     */
    fun refrescar() {
        if (_refrescando.value || _uiState.value is FeriasUiState.Cargando) return
        viewModelScope.launch {
            _refrescando.value = true
            consultarFerias()
            _refrescando.value = false
        }
    }

    private suspend fun consultarFerias() {
        repository.obtenerFeriasConCache()
            .onSuccess { cargadas ->
                val feriasDominio = cargadas.ferias
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
                            distancia = "",
                            precio = "$ ${feria.precioPuesto.toInt()}",
                            precioNumerico = feria.precioPuesto,
                            puestosLibres = feria.puestosLibres,
                            latitud = feria.latitud,
                            longitud = feria.longitud
                        )
                    }
                    _uiState.value = FeriasUiState.Contenido(feriasMapeadas, cargadas.desdeCache)
                }
            }
            .onFailure { e ->
                Log.e("FeriasViewModel", "Error al obtener ferias", e)
                // Si ya se estaban mostrando ferias, no se pisan con un error.
                if (_uiState.value !is FeriasUiState.Contenido) {
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
