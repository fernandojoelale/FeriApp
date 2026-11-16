package edu.uadeapps.feriapp.ui.organizador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.uadeapps.feriapp.domain.model.PosicionPuesto
import edu.uadeapps.feriapp.domain.usecase.MAX_COLUMNAS_PLANO
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import edu.uadeapps.feriapp.ui.components.AvisoCard
import edu.uadeapps.feriapp.ui.components.BotonPrimario
import edu.uadeapps.feriapp.ui.components.CampoTexto
import edu.uadeapps.feriapp.ui.components.EncabezadoFeri
import edu.uadeapps.feriapp.ui.components.TipoAviso
import edu.uadeapps.feriapp.ui.theme.FeriColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicarFeriaScreen(
    onVolverClick: () -> Unit,
    onPublicada: () -> Unit,
    viewModel: PublicarFeriaViewModel = viewModel(factory = PublicarFeriaViewModel.Factory)
) {
    var nombre by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var horaInicio by remember { mutableStateOf("10:00") }
    var horaFin by remember { mutableStateOf("18:00") }
    var direccion by remember { mutableStateOf("") }
    var zona by remember { mutableStateOf("") }
    var latitud by remember { mutableStateOf("") }
    var longitud by remember { mutableStateOf("") }
    var rubro by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var columnas by remember { mutableStateOf(4) }
    var filas by remember { mutableStateOf(3) }
    var inactivos by remember { mutableStateOf(setOf<Pair<Int, Int>>()) }

    val estado by viewModel.uiState.collectAsState()
    LaunchedEffect(estado.publicada) {
        if (estado.publicada) onPublicada()
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).imePadding()) {
        EncabezadoFeri(titulo = "Publicar feria", subtitulo = "Ubicá los puestos en el predio", onVolver = onVolverClick)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CampoTexto("Nombre de la feria", nombre, { nombre = it }, placeholder = "Feria de las Artes")
            
            // Selector de fecha con calendario precargado
            FechaPickerField(fecha = fecha, onFechaSelected = { fecha = it })

            // Selectores desplegables para Hora de Inicio y Hora de Fin
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                HoraDropdownField(
                    label = "Desde (HH:mm)",
                    selectedTime = horaInicio,
                    onTimeSelected = { horaInicio = it },
                    modifier = Modifier.weight(1f)
                )
                HoraDropdownField(
                    label = "Hasta (HH:mm)",
                    selectedTime = horaFin,
                    onTimeSelected = { horaFin = it },
                    modifier = Modifier.weight(1f)
                )
            }

            CampoTexto("Dirección", direccion, { direccion = it }, placeholder = "Av. Santa Fe 1234")
            CampoTexto("Zona / barrio", zona, { zona = it }, placeholder = "Palermo")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CampoTexto("Latitud", latitud, { latitud = it }, modifier = Modifier.weight(1f), placeholder = "-34.5875", tipoTeclado = KeyboardType.Decimal)
                CampoTexto("Longitud", longitud, { longitud = it }, modifier = Modifier.weight(1f), placeholder = "-58.4200", tipoTeclado = KeyboardType.Decimal)
            }
            Text(
                "Tip: en Google Maps mantené apretado el punto de la feria y copiá las coordenadas.",
                color = FeriColors.TextoSecundario,
                fontSize = 14.sp
            )
            CampoTexto("Rubro", rubro, { rubro = it }, placeholder = "Artesanías")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CampoTexto("Precio por puesto ($)", precio, { precio = it }, modifier = Modifier.weight(1f), placeholder = "18000", tipoTeclado = KeyboardType.Decimal)
            }

            Text("Plano del predio", color = FeriColors.Texto, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(
                "Elegí cuántas columnas y filas tiene el predio y tocá un lugar para quitar o volver a poner un puesto (por ejemplo, para dejar pasillos).",
                color = FeriColors.TextoSecundario, fontSize = 14.sp
            )
            Contador("Columnas", columnas, 3, MAX_COLUMNAS_PLANO) { columnas = it; inactivos = inactivos.filter { (x, _) -> x < it }.toSet() }
            Contador("Filas", filas, 1, 10) { filas = it; inactivos = inactivos.filter { (_, y) -> y < it }.toSet() }
            val activos = (0 until filas).flatMap { y -> (0 until columnas).map { x -> x to y } }.filter { it !in inactivos }
            EditorPlano(columnas, filas, inactivos) { pos ->
                inactivos = if (pos in inactivos) inactivos - pos else inactivos + pos
            }
            Text("${activos.size} puestos", color = FeriColors.Texto, fontSize = 14.sp, fontWeight = FontWeight.Bold)

            estado.error?.let {
                AvisoCard(titulo = "No pudimos publicar la feria", mensaje = it, tipo = TipoAviso.ERROR)
            }
            Spacer(modifier = Modifier.height(4.dp))
            BotonPrimario(
                texto = "Publicar feria",
                onClick = {
                    val activos = (0 until filas).flatMap { y -> (0 until columnas).map { x -> x to y } }.filter { it !in inactivos }
                    viewModel.publicar(nombre, fecha, horaInicio, horaFin, direccion, zona, latitud, longitud, rubro, precio,
                        activos.map { PosicionPuesto(it.first, it.second) })
                },
                cargando = estado.publicando,
                modifier = Modifier.fillMaxWidth().navigationBarsPadding()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FechaPickerField(
    fecha: String,
    onFechaSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    Column(modifier = modifier) {
        Text("Fecha de la feria", fontSize = 14.sp, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = fecha,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text("2026-12-20", color = Color.Gray) },
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Seleccionar fecha",
                        tint = FeriColors.Primario
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true },
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = Color.LightGray,
                disabledLabelColor = Color.Gray,
                disabledTextColor = Color.Black,
                disabledTrailingIconColor = FeriColors.Primario,
                disabledPlaceholderColor = Color.Gray
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
                                timeZone = TimeZone.getTimeZone("UTC")
                            }
                            onFechaSelected(formatter.format(Date(millis)))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Aceptar", color = FeriColors.Primario)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HoraDropdownField(
    label: String,
    selectedTime: String,
    onTimeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val horas = listOf(
        "08:00", "09:00", "10:00", "11:00", "12:00",
        "13:00", "14:00", "15:00", "16:00", "17:00",
        "18:00", "19:00", "20:00", "21:00", "22:00"
    )
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(label, fontSize = 14.sp, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(6.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedTime,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = FeriColors.Primario,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                horas.forEach { hora ->
                    DropdownMenuItem(
                        text = { Text(hora) },
                        onClick = {
                            onTimeSelected(hora)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun Contador(titulo: String, valor: Int, min: Int, max: Int, onCambio: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("$titulo: $valor", color = FeriColors.Texto, fontSize = 14.sp, modifier = Modifier.weight(1f))
        OutlinedButton(
            onClick = { if (valor > min) onCambio(valor - 1) },
            enabled = valor > min,
            modifier = Modifier.size(48.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
        ) { Text("−", fontSize = 20.sp) }
        OutlinedButton(
            onClick = { if (valor < max) onCambio(valor + 1) },
            enabled = valor < max,
            modifier = Modifier.size(48.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
        ) { Text("+", fontSize = 20.sp) }
    }
}

/** Cuadrícula editable: cada lugar es un puesto (con su número) o un espacio vacío marcado con "·". */
@Composable
private fun EditorPlano(columnas: Int, filas: Int, inactivos: Set<Pair<Int, Int>>, onToggle: (Pair<Int, Int>) -> Unit) {
    var numero = 0
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        for (y in 0 until filas) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (x in 0 until columnas) {
                    val pos = x to y
                    val activo = pos !in inactivos
                    if (activo) numero++
                    val texto = if (activo) "$numero" else "Vacío"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (activo) FeriColors.Primario else Color.White)
                            .border(1.dp, if (activo) FeriColors.Primario else FeriColors.Ocupado, RoundedCornerShape(10.dp))
                            .clickable { onToggle(pos) }
                            .semantics {
                                contentDescription = if (activo) "Puesto $numero. Tocá para quitarlo" else "Lugar vacío. Tocá para poner un puesto"
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            texto,
                            color = if (activo) Color.White else FeriColors.Ocupado,
                            fontSize = if (activo) 16.sp else 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
