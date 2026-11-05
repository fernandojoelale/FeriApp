package edu.uadeapps.feriapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.uadeapps.feriapp.ui.theme.FeriColors

enum class TipoAviso { ERROR, ADVERTENCIA, INFO, REQUIERE_LOGIN }

data class Aviso(val mensaje: String, val tipo: TipoAviso)

/** Barra superior azul con título, subtítulo opcional y flecha para volver. */
@Composable
fun EncabezadoFeri(titulo: String, subtitulo: String? = null, onVolver: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(FeriColors.Primario)
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onVolver != null) {
            IconButton(onClick = onVolver) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
        } else {
            Spacer(modifier = Modifier.width(16.dp))
        }
        Column {
            Text(titulo, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            if (subtitulo != null) {
                Text(subtitulo, color = Color.White, fontSize = 13.sp)
            }
        }
    }
}

/** Mensaje de error o advertencia. Lleva un título de texto además del color. */
@Composable
fun AvisoCard(titulo: String, mensaje: String, tipo: TipoAviso, modifier: Modifier = Modifier) {
    val color = when (tipo) {
        TipoAviso.ERROR -> FeriColors.Error
        TipoAviso.ADVERTENCIA, TipoAviso.REQUIERE_LOGIN -> FeriColors.Advertencia
        TipoAviso.INFO -> FeriColors.Primario
    }
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, color)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(titulo, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(mensaje, color = FeriColors.Texto, fontSize = 14.sp)
        }
    }
}

@Composable
fun AvisoCard(aviso: Aviso, modifier: Modifier = Modifier) {
    val titulo = when (aviso.tipo) {
        TipoAviso.ERROR -> "Error"
        TipoAviso.ADVERTENCIA, TipoAviso.REQUIERE_LOGIN -> "Atención"
        TipoAviso.INFO -> "Info"
    }
    AvisoCard(titulo = titulo, mensaje = aviso.mensaje, tipo = aviso.tipo, modifier = modifier)
}

/** Estado vacío: título, mensaje y (opcional) acciones debajo. */
@Composable
fun EstadoVacio(titulo: String, mensaje: String, acciones: @Composable () -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(titulo, color = FeriColors.Primario, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(mensaje, color = FeriColors.TextoSecundario, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(20.dp))
            acciones()
        }
    }
}

@Composable
fun EstadoCargando(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = FeriColors.Primario)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Cargando…", color = FeriColors.Texto)
        }
    }
}

@Composable
fun EstadoError(mensaje: String, onReintentar: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(mensaje, color = FeriColors.Texto, fontSize = 16.sp)
            BotonPrimario(texto = "Reintentar", onClick = onReintentar)
        }
    }
}

@Composable
fun BotonPrimario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    cargando: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = enabled && !cargando,
        modifier = modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = FeriColors.Primario)
    ) {
        if (cargando) {
            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
        } else {
            Text(texto, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun BotonSecundario(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, FeriColors.Primario),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = FeriColors.Primario)
    ) {
        Text(texto, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

/** Campo de texto con la etiqueta arriba; con ojito para mostrar/ocultar si es contraseña. */
@Composable
fun CampoTexto(
    etiqueta: String,
    valor: String,
    onCambio: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    tipoTeclado: KeyboardType = KeyboardType.Text,
    esPassword: Boolean = false
) {
    var visible by remember { mutableStateOf(false) }
    val iconoFinal: (@Composable () -> Unit)? = if (esPassword) {
        {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    imageVector = if (visible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = if (visible) "Ocultar contraseña" else "Mostrar contraseña"
                )
            }
        }
    } else {
        null
    }

    Column(modifier = modifier) {
        Text(text = etiqueta, fontSize = 14.sp, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = valor,
            onValueChange = onCambio,
            placeholder = { Text(placeholder, color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            visualTransformation = if (esPassword && !visible) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = iconoFinal,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = FeriColors.Primario,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado)
        )
    }
}

/** Fila "etiqueta ... valor" para mostrar datos de una feria o reserva. */
@Composable
fun DatoConEtiqueta(etiqueta: String, valor: String) {
    if (valor.isBlank()) return
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(
            text = etiqueta,
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.width(120.dp)
        )
        Text(text = valor, color = FeriColors.Texto, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}
