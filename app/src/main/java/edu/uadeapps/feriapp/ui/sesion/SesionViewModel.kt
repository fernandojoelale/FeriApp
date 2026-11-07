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
import edu.uadeapps.feriapp.data.push.PushTokenManager
import edu.uadeapps.feriapp.domain.model.Rol
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Sesión actual de toda la app (se crea una sola vez, a nivel de la Activity). */
class SesionViewModel(
    observarSesionUseCase: ObservarSesionUseCase,
    private val cerrarSesionUseCase: CerrarSesionUseCase,
    private val pushTokenManager: PushTokenManager
) : ViewModel() {

    val sesion: StateFlow<Session?> =
        observarSesionUseCase().stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init {
        // Cuando un organizador inicia sesión (o se abre la app con sesión), registra su celular para RF04.
        viewModelScope.launch {
            sesion.map { it?.usuarioId to it?.rol }.distinctUntilChanged().collect { (_, rol) ->
                if (rol == Rol.ORGANIZADOR) pushTokenManager.registrarSiOrganizador()
            }
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            // Primero se desvincula el celular (necesita el token de la sesión) y después se cierra.
            pushTokenManager.quitar()
            cerrarSesionUseCase()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FeriApp)
                SesionViewModel(app.container.observarSesionUseCase, app.container.cerrarSesionUseCase, app.container.pushTokenManager)
            }
        }
    }
}
