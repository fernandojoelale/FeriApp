package edu.uadeapps.feriapp.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.uadeapps.feriapp.domain.model.Rol
import edu.uadeapps.feriapp.ui.components.AvisoCard
import edu.uadeapps.feriapp.ui.components.BotonPrimario
import edu.uadeapps.feriapp.ui.components.EncabezadoFeri
import edu.uadeapps.feriapp.ui.components.TipoAviso
import edu.uadeapps.feriapp.ui.theme.FeriColors

@Composable
fun RegistroScreen(
    onVolverClick: () -> Unit,
    onRegistroExitoso: () -> Unit,
    onIniciarSesionClick: () -> Unit,
    viewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory)
) {
    var rol by remember { mutableStateOf(Rol.FERIANTE) }
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rubro by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(uiState.exito) {
        if (uiState.exito) onRegistroExitoso()
    }

    val colores = OutlinedTextFieldDefaults.colors(
        unfocusedBorderColor = FeriColors.Borde,
        focusedBorderColor = FeriColors.Primario
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .imePadding()
    ) {
        EncabezadoFeri(
            titulo = "Crear cuenta",
            subtitulo = "Elegí el perfil que vas a usar",
            onVolver = onVolverClick
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilterChip(
                    selected = rol == Rol.FERIANTE,
                    onClick = { rol = Rol.FERIANTE },
                    label = { Text("Feriante") }
                )
                FilterChip(
                    selected = rol == Rol.ORGANIZADOR,
                    onClick = { rol = Rol.ORGANIZADOR },
                    label = { Text("Organizador") }
                )
            }
            Text(
                text = if (rol == Rol.FERIANTE) "Buscás puestos para vender tus productos."
                else "Publicás ferias y gestionás sus puestos.",
                fontSize = 13.sp,
                color = FeriColors.TextoSecundario,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = colores
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = colores
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña (mínimo 6 caracteres)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                colors = colores
            )

            if (rol == Rol.FERIANTE) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = rubro,
                    onValueChange = { rubro = it },
                    label = { Text("Rubro (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = colores
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Pedimos solo los datos necesarios. Tu ubicación nunca se guarda ni se envía al servidor.",
                fontSize = 12.sp,
                color = FeriColors.TextoSecundario
            )

            uiState.error?.let { mensaje ->
                Spacer(modifier = Modifier.height(16.dp))
                AvisoCard(titulo = "No pudimos crear tu cuenta", mensaje = mensaje, tipo = TipoAviso.ERROR)
            }

            Spacer(modifier = Modifier.height(24.dp))
            BotonPrimario(
                texto = if (rol == Rol.FERIANTE) "Crear cuenta de feriante" else "Crear cuenta de organizador",
                onClick = { viewModel.registrar(nombre, email, password, rol, rubro) },
                cargando = uiState.cargando
            )
            TextButton(
                onClick = onIniciarSesionClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Text("Ya tengo cuenta", color = FeriColors.Primario, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
