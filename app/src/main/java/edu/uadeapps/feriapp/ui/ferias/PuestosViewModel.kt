package edu.uadeapps.feriapp.ui.ferias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import edu.uadeapps.feriapp.FeriApp
import edu.uadeapps.feriapp.domain.model.ErrorApp
import edu.uadeapps.feriapp.domain.model.FeriaDetalle
import edu.uadeapps.feriapp.domain.model.Puesto
import edu.uadeapps.feriapp.domain.usecase.ObtenerDetalleFeriaUseCase
import edu.uadeapps.feriapp.domain.usecase.ReservarPuestoUseCase
import edu.uadeapps.feriapp.ui.components.Aviso
import edu.uadeapps.feriapp.ui.components.TipoAviso
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PuestosUiState(
    val cargando: Boolean = true,
    val errorCarga: String? = null,
    val detalle: FeriaDetalle? = null,
    val seleccionadoId: Long? = null,
    val reservando: Boolean = false,
    val aviso: Aviso? = null,
    val reservaCreadaId: Long? = null
) {
    val seleccionado: Puesto?
        get() = detalle?.puestos?.firstOrNull { it.id == seleccionadoId }
}

class PuestosViewModel(
    private val feriaId: Long,
    private val obtenerDetalleUseCase: ObtenerDetalleFeriaUseCase,
    private val reservarPuestoUseCase: ReservarPuestoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PuestosUiState())
    val uiState: StateFlow<PuestosUiState> = _uiState.asStateFlow()

    init {
        cargar()
    }

    fun cargar(conservarAviso: Boolean = false) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    cargando = it.detalle == null,
                    errorCarga = null,
                    aviso = if (conservarAviso) it.aviso else null
                )
            }
            obtenerDetalleUseCase(feriaId).fold(
                onSuccess = { detalle ->
                    _uiState.update { estado ->
                        // Si el puesto elegido ya no está libre, se deselecciona.
                        val sigueLibre = detalle.puestos.any { it.id == estado.seleccionadoId && it.disponible }
                        estado.copy(
                            cargando = false,
                            detalle = detalle,
                            seleccionadoId = if (sigueLibre) estado.seleccionadoId else null
                        )
                    }
                },
                onFailure = { error ->
                    val mensaje = error.message ?: "No pudimos cargar el plano de puestos."
                    _uiState.update { estado ->
                        if (estado.detalle == null) {
                            estado.copy(cargando = false, errorCarga = mensaje)
                        } else {
                            estado.copy(cargando = false, aviso = Aviso(mensaje, TipoAviso.ADVERTENCIA))
                        }
                    }
                }
            )
        }
    }

    fun seleccionar(puesto: Puesto) {
        val estado = _uiState.value
        if (!puesto.disponible || estado.reservando) return
        _uiState.update {
            it.copy(
                seleccionadoId = if (it.seleccionadoId == puesto.id) null else puesto.id,
                aviso = null
            )
        }
    }

    fun reservar() {
        val puesto = _uiState.value.seleccionado ?: return
        if (_uiState.value.reservando) return
        _uiState.update { it.copy(reservando = true, aviso = null) }

        viewModelScope.launch {
            reservarPuestoUseCase(puesto).fold(
                onSuccess = { reserva ->
                    _uiState.update { it.copy(reservando = false, reservaCreadaId = reserva.id) }
                },
                onFailure = { error ->
                    val tipo = (error as? ErrorApp)?.tipo
                    val aviso = when (tipo) {
                        ErrorApp.Tipo.CONFLICTO -> Aviso(
                            "Ese puesto ya fue reservado por otra persona. Actualizamos el plano: elegí otro puesto libre.",
                            TipoAviso.ERROR
                        )
                        ErrorApp.Tipo.SIN_CONEXION -> Aviso(
                            "Sin conexión: la reserva no se confirmó y el puesto sigue libre. Reintentá cuando tengas internet.",
                            TipoAviso.ADVERTENCIA
                        )
                        ErrorApp.Tipo.NO_AUTORIZADO -> Aviso(
                            error.message ?: "Iniciá sesión como feriante para reservar.",
                            TipoAviso.REQUIERE_LOGIN
                        )
                        else -> Aviso(error.message ?: "No pudimos completar la reserva.", TipoAviso.ERROR)
                    }
                    _uiState.update {
                        it.copy(
                            reservando = false,
                            aviso = aviso,
                            seleccionadoId = if (tipo == ErrorApp.Tipo.CONFLICTO) null else it.seleccionadoId
                        )
                    }
                    // Si alguien nos ganó el puesto, se refresca el plano conservando el aviso.
                    if (tipo == ErrorApp.Tipo.CONFLICTO) cargar(conservarAviso = true)
                }
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FeriApp)
                val feriaId = createSavedStateHandle().get<Long>("feriaId") ?: 0L
                PuestosViewModel(feriaId, app.container.obtenerDetalleFeriaUseCase, app.container.reservarPuestoUseCase)
            }
        }
    }
}
