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
import edu.uadeapps.feriapp.ui.components.AvisoCard
import edu.uadeapps.feriapp.ui.components.BotonPrimario
import edu.uadeapps.feriapp.ui.components.CampoTexto
import edu.uadeapps.feriapp.ui.components.EncabezadoFeri
import edu.uadeapps.feriapp.ui.components.TipoAviso
import edu.uadeapps.feriapp.ui.theme.FeriColors

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
    var cantidad by remember { mutableStateOf("") }

    val estado by viewModel.uiState.collectAsState()
    LaunchedEffect(estado.publicada) {
        if (estado.publicada) onPublicada()
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).imePadding()) {
        EncabezadoFeri(titulo = "Publicar feria", subtitulo = "Los puestos se crean automáticamente", onVolver = onVolverClick)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CampoTexto("Nombre de la feria", nombre, { nombre = it }, placeholder = "Feria de las Artes")
            CampoTexto("Fecha (AAAA-MM-DD)", fecha, { fecha = it }, placeholder = "2026-12-20", tipoTeclado = KeyboardType.Number)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CampoTexto("Desde (HH:mm)", horaInicio, { horaInicio = it }, modifier = Modifier.weight(1f))
                CampoTexto("Hasta (HH:mm)", horaFin, { horaFin = it }, modifier = Modifier.weight(1f))
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
                fontSize = 12.sp
            )
            CampoTexto("Rubro", rubro, { rubro = it }, placeholder = "Artesanías")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CampoTexto("Precio por puesto ($)", precio, { precio = it }, modifier = Modifier.weight(1f), placeholder = "18000", tipoTeclado = KeyboardType.Decimal)
                CampoTexto("Cantidad de puestos", cantidad, { cantidad = it }, modifier = Modifier.weight(1f), placeholder = "20", tipoTeclado = KeyboardType.Number)
            }

            estado.error?.let {
                AvisoCard(titulo = "No pudimos publicar la feria", mensaje = it, tipo = TipoAviso.ERROR)
            }
            Spacer(modifier = Modifier.height(4.dp))
            BotonPrimario(
                texto = "Publicar feria",
                onClick = {
                    viewModel.publicar(nombre, fecha, horaInicio, horaFin, direccion, zona, latitud, longitud, rubro, precio, cantidad)
                },
                cargando = estado.publicando,
                modifier = Modifier.fillMaxWidth().navigationBarsPadding()
            )
        }
    }
}
