package edu.uadeapps.feriapp.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun LoginScreen(onVolverClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Pantalla de Iniciar Sesión", style = MaterialTheme.typography.headlineMedium)
            Button(onClick = onVolverClick) {
                Text("Volver al inicio")
            }
        }
    }
}