package edu.uadeapps.feriapp.ui.ferias

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.uadeapps.feriapp.domain.model.Puesto
import edu.uadeapps.feriapp.ui.components.AvisoCard
import edu.uadeapps.feriapp.ui.components.BotonPrimario
import edu.uadeapps.feriapp.ui.components.BotonSecundario
import edu.uadeapps.feriapp.ui.components.EncabezadoFeri
import edu.uadeapps.feriapp.ui.components.EstadoCargando
import edu.uadeapps.feriapp.ui.components.EstadoError
import edu.uadeapps.feriapp.ui.components.TipoAviso
import edu.uadeapps.feriapp.ui.components.formatearPrecio
import edu.uadeapps.feriapp.ui.theme.FeriColors

@Composable
fun PuestosScreen(
    onVolverClick: () -> Unit,
    onReservaCreada: (Long) -> Unit,
    onLoginClick: () -> Unit,
    viewModel: PuestosViewModel = viewModel(factory = PuestosViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsState()

    LaunchedEffect(estado.reservaCreadaId) {
        estado.reservaCreadaId?.let(onReservaCreada)
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        EncabezadoFeri(
            titulo = "Elegí tu puesto",
            subtitulo = estado.detalle?.feria?.nombre,
            onVolver = onVolverClick
        )

        val detalle = estado.detalle
        when {
            estado.cargando -> EstadoCargando()
            estado.errorCarga != null -> EstadoError(
                mensaje = estado.errorCarga ?: "",
                onReintentar = { viewModel.cargar() }
            )
            detalle != null -> {
                Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Leyenda()
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(detalle.puestos.sortedBy { it.numero }, key = { it.id }) { puesto ->
                            CeldaPuesto(
                                puesto = puesto,
                                elegido = puesto.id == estado.seleccionadoId,
                                onClick = { viewModel.seleccionar(puesto) }
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .navigationBarsPadding()
                ) {
                    estado.aviso?.let { aviso ->
                        AvisoCard(aviso)
                        if (aviso.tipo == TipoAviso.REQUIERE_LOGIN) {
                            Spacer(modifier = Modifier.height(8.dp))
                            BotonSecundario(texto = "Iniciar sesión", onClick = onLoginClick)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    val elegido = estado.seleccionado
                    Text(
                        text = if (elegido != null)
                            "Puesto ${elegido.numero} · ${formatearPrecio(detalle.feria.precioPuesto)}"
                        else "Tocá un puesto libre para elegirlo",
                        color = FeriColors.Texto,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    BotonPrimario(
                        texto = "Confirmar reserva",
                        onClick = { viewModel.reservar() },
                        enabled = elegido != null,
                        cargando = estado.reservando
                    )
                }
            }
        }
    }
}

@Composable
private fun Leyenda() {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
        ItemLeyenda("Libre", FeriColors.Disponible)
        ItemLeyenda("Ocupado", FeriColors.Ocupado)
        ItemLeyenda("Elegido", FeriColors.Primario)
    }
}

@Composable
private fun ItemLeyenda(texto: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(14.dp).clip(RoundedCornerShape(4.dp)).background(color))
        Text(" $texto", fontSize = 12.sp, color = FeriColors.Texto)
    }
}

@Composable
private fun CeldaPuesto(puesto: Puesto, elegido: Boolean, onClick: () -> Unit) {
    val fondo = when {
        elegido -> FeriColors.Primario
        puesto.disponible -> FeriColors.Disponible.copy(alpha = 0.12f)
        else -> FeriColors.Ocupado
    }
    val colorTexto = when {
        elegido || !puesto.disponible -> Color.White
        else -> FeriColors.Disponible
    }
    val estadoTexto = when {
        elegido -> "Elegido"
        puesto.disponible -> "Libre"
        else -> "Ocupado"
    }
    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(fondo)
            .border(1.dp, if (puesto.disponible || elegido) FeriColors.Disponible else FeriColors.Ocupado, RoundedCornerShape(10.dp))
            .clickable(enabled = puesto.disponible, onClick = onClick)
            .semantics { contentDescription = "Puesto ${puesto.numero}, $estadoTexto" },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("${puesto.numero}", color = colorTexto, fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text(estadoTexto, color = colorTexto, fontSize = 9.sp, textAlign = TextAlign.Center)
    }
}
