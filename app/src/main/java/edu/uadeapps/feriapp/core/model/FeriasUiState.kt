package edu.uadeapps.feriapp.core.model

import edu.uadeapps.feriapp.ui.ferias.FeriaItem

// Representación de los estados inmutables de la vista
sealed class FeriasUiState {
    object Cargando : FeriasUiState()
    data class Contenido(val ferias: List<FeriaItem>) : FeriasUiState()
    object Vacio : FeriasUiState()
    data class Error(val mensaje: String) : FeriasUiState()
}