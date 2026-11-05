package edu.uadeapps.feriapp.ui.sesion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import edu.uadeapps.feriapp.FeriApp
import edu.uadeapps.feriapp.domain.model.Session
import edu.uadeapps.feriapp.domain.usecase.CerrarSesionUseCase
import edu.uadeapps.feriapp.domain.usecase.ObservarSesionUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Sesión actual de toda la app (se crea una sola vez, a nivel de la Activity). */
class SesionViewModel(
    observarSesionUseCase: ObservarSesionUseCase,
    private val cerrarSesionUseCase: CerrarSesionUseCase
) : ViewModel() {

    val sesion: StateFlow<Session?> =
        observarSesionUseCase().stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun cerrarSesion() {
        viewModelScope.launch { cerrarSesionUseCase() }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FeriApp)
                SesionViewModel(app.container.observarSesionUseCase, app.container.cerrarSesionUseCase)
            }
        }
    }
}
