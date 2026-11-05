package edu.uadeapps.feriapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import edu.uadeapps.feriapp.domain.model.Session
import edu.uadeapps.feriapp.ui.ferias.FeriasScreen
import edu.uadeapps.feriapp.ui.perfil.PerfilScreen
import edu.uadeapps.feriapp.ui.reservas.MisReservasScreen

@Composable
fun MainScreen(
    sesion: Session?,
    seccionInicial: PantallasFooter,
    onLoginClick: () -> Unit,
    onRegistroClick: () -> Unit,
    onFeriaClick: (Long) -> Unit,
    onReservaClick: (Long) -> Unit,
    onCerrarSesion: () -> Unit
) {
    var seccionActual by rememberSaveable(seccionInicial) { mutableStateOf(seccionInicial) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                seccionSeleccionada = seccionActual,
                onSeccionSeleccionada = { seccionActual = it }
            )
        }
    ) { paddingValores ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValores)
        ) {
            when (seccionActual) {
                PantallasFooter.FERIAS -> FeriasScreen(
                    // El ícono de perfil del encabezado: con sesión lleva al perfil, sin sesión al login.
                    onLoginClick = {
                        if (sesion != null) seccionActual = PantallasFooter.PERFIL else onLoginClick()
                    },
                    onFeriaClick = onFeriaClick
                )
                PantallasFooter.RESERVAS -> MisReservasScreen(
                    sesion = sesion,
                    onLoginClick = onLoginClick,
                    onReservaClick = onReservaClick
                )
                PantallasFooter.PERFIL -> PerfilScreen(
                    sesion = sesion,
                    onLoginClick = onLoginClick,
                    onRegistroClick = onRegistroClick,
                    onCerrarSesion = onCerrarSesion
                )
            }
        }
    }
}
