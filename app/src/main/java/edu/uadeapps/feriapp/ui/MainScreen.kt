package edu.uadeapps.feriapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


@Composable
fun MainScreen(onLoginClick: () -> Unit) {
    var seccionActual by remember { mutableStateOf(PantallasFooter.FERIAS) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                seccionSeleccionada = seccionActual,
                onSeccionSeleccionada = { nuevaSeccion ->
                    seccionActual = nuevaSeccion
                }
            )
        }
    )  { paddingValores ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValores)
        ) {
            when (seccionActual) {
                PantallasFooter.FERIAS -> {
                    FeriasScreen(onLoginClick = onLoginClick)
                }
                PantallasFooter.RESERVAS -> {
                    Text(
                        text = "Aquí se mostrarán tus Reservas",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                PantallasFooter.PERFIL -> {
                    Text(
                        text = "Aquí verás tu Perfil",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}