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
import edu.uadeapps.feriapp.data.repository.FeriasCargadas
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FeriasViewModel(
    private val repository: FeriaRepository,
    conectado: Flow<Boolean> = emptyFlow()
) : ViewModel() {

    private val _uiState = MutableStateFlow<FeriasUiState>(FeriasUiState.Cargando)
    val uiState: StateFlow<FeriasUiState> = _uiState.asStateFlow()

    private val _refrescando = MutableStateFlow(false)
    val refrescando: StateFlow<Boolean> = _refrescando.asStateFlow()

    init {
        cargarFerias()
        // Al recuperar la conexión, si se estaban mostrando datos guardados, se sincroniza solo.
        viewModelScope.launch {
            conectado.collect { hayConexion ->
                val estado = _uiState.value
                if (hayConexion && estado is FeriasUiState.Contenido && estado.desdeCache) refrescar()
            }
        }
    }

    /**
     * Offline First: si hay una copia guardada se muestra de inmediato y se actualiza desde el
     * servidor en segundo plano; si no hay copia, se muestra el spinner.
     */
    fun cargarFerias() {
        viewModelScope.launch {
            _refrescando.value = true
            val guardadas = repository.leerGuardadas()
            if (guardadas != null) mostrar(guardadas) else _uiState.value = FeriasUiState.Cargando
            consultarFerias()
            _refrescando.value = false
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

    private fun mostrar(cargadas: FeriasCargadas) {
        if (cargadas.ferias.isEmpty()) {
            _uiState.value = FeriasUiState.Vacio
            return
        }
        val items = cargadas.ferias.map { feria ->
            FeriaItem(
                id = feria.id,
                nombre = feria.nombre,
                fecha = feria.fecha,
                horaInicio = feria.horaInicio,
                horaFin = feria.horaFin,
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
        _uiState.value = FeriasUiState.Contenido(items, cargadas.desdeCache, cargadas.actualizadoEn)
    }

    private suspend fun consultarFerias() {
        repository.obtenerFeriasConCache()
            .onSuccess { mostrar(it) }
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
                FeriasViewModel(application.container.feriaRepository, application.container.conectado)
            }
        }
    }
}
