package edu.uadeapps.feriapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import edu.uadeapps.feriapp.FeriApp
import edu.uadeapps.feriapp.domain.model.Rol
import edu.uadeapps.feriapp.domain.usecase.IniciarSesionUseCase
import edu.uadeapps.feriapp.domain.usecase.RegistrarUsuarioUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val cargando: Boolean = false,
    val error: String? = null,
    val exito: Boolean = false
)

class AuthViewModel(
    private val iniciarSesionUseCase: IniciarSesionUseCase,
    private val registrarUsuarioUseCase: RegistrarUsuarioUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun iniciarSesion(email: String, password: String) {
        if (_uiState.value.cargando) return
        viewModelScope.launch {
            _uiState.value = AuthUiState(cargando = true)
            iniciarSesionUseCase(email, password).fold(
                onSuccess = { _uiState.value = AuthUiState(exito = true) },
                onFailure = { _uiState.value = AuthUiState(error = it.message ?: "No pudimos iniciar sesión.") }
            )
        }
    }

    fun registrar(nombre: String, email: String, password: String, rol: Rol, rubro: String) {
        if (_uiState.value.cargando) return
        viewModelScope.launch {
            _uiState.value = AuthUiState(cargando = true)
            registrarUsuarioUseCase(nombre, email, password, rol, rubro).fold(
                onSuccess = { _uiState.value = AuthUiState(exito = true) },
                onFailure = { _uiState.value = AuthUiState(error = it.message ?: "No pudimos crear la cuenta.") }
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FeriApp)
                AuthViewModel(app.container.iniciarSesionUseCase, app.container.registrarUsuarioUseCase)
            }
        }
    }
}
