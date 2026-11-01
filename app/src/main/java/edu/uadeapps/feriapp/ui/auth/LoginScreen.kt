package edu.uadeapps.feriapp.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(onVolverClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Color principal extraído de la imagen
    val primaryBlue = Color(0xFF1C4C96)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Encabezado superior
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(primaryBlue)
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Text(
                text = "FeriApp",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Encontrá tu lugar en la próxima feria",
                color = Color.White,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Contenido principal del formulario
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // Título principal
            Text(
                text = "Tu próxima oportunidad\nempieza acá.",
                color = primaryBlue,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo Email
            Text(text = "Email", fontSize = 14.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("lucia@ejemplo.com", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = primaryBlue,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Contraseña
            Text(text = "Contraseña", fontSize = 14.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("********", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else
                        Icons.Filled.VisibilityOff

                    val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = primaryBlue,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón Iniciar sesión
            Button(
                onClick = { /* Lógica de login hacia el backend */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
            ) {
                Text("Iniciar sesión", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Crear cuenta
            OutlinedButton(
                onClick = { /* Navegación a registro */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, primaryBlue),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryBlue)
            ) {
                Text("Crear cuenta", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Texto de pie de página
            Text(
                text = "El rol se recupera de tu cuenta al iniciar sesión.\nLa reserva no incluye el pago.",
                fontSize = 12.sp,
                color = Color.Gray,
                lineHeight = 16.sp
            )
        }
    }
}

