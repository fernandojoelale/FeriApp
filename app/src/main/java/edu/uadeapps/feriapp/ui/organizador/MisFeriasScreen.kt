package edu.uadeapps.feriapp.ui.organizador

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.uadeapps.feriapp.domain.Feria
import edu.uadeapps.feriapp.ui.components.BotonPrimario
import edu.uadeapps.feriapp.ui.components.EncabezadoFeri
import edu.uadeapps.feriapp.ui.components.EstadoCargando
import edu.uadeapps.feriapp.ui.components.EstadoError
import edu.uadeapps.feriapp.ui.components.EstadoVacio
import edu.uadeapps.feriapp.ui.components.formatearFecha
import edu.uadeapps.feriapp.ui.components.formatearHorario
import edu.uadeapps.feriapp.ui.components.formatearPrecio
import edu.uadeapps.feriapp.ui.theme.FeriColors

/** Pestaña "Reservas" para cuentas de organizador: sus ferias y el botón para publicar otra. */
@Composable
fun MisFeriasScreen(
    onPublicarClick: () -> Unit,
    onFeriaClick: (Long) -> Unit,
    viewModel: MisFeriasViewModel = viewModel(factory = MisFeriasViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) { viewModel.cargar() }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        EncabezadoFeri(titulo = "Mis ferias", subtitulo = "Ferias que publicaste")

        when (val e = estado) {
            is MisFeriasUiState.Cargando -> EstadoCargando()
            is MisFeriasUiState.Error -> EstadoError(mensaje = e.mensaje, onReintentar = viewModel::cargar)
            is MisFeriasUiState.Contenido -> {
                if (e.ferias.isEmpty()) {
                    EstadoVacio(
                        titulo = "Todavía no publicaste ferias",
                        mensaje = "Publicá tu primera feria y los feriantes podrán reservar puestos."
                    ) { BotonPrimario(texto = "Publicar feria", onClick = onPublicarClick) }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item { BotonPrimario(texto = "Publicar nueva feria", onClick = onPublicarClick) }
                        items(e.ferias, key = { it.id }) { TarjetaMiFeria(it) { onFeriaClick(it.id) } }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaMiFeria(feria: Feria, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, FeriColors.Borde, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column {
            Text(feria.nombre, color = FeriColors.Texto, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(
                "${formatearFecha(feria.fecha)} · ${formatearHorario(feria.horaInicio, feria.horaFin)}",
                color = FeriColors.TextoSecundario,
                fontSize = 13.sp
            )
            Text("${feria.zona} · ${formatearPrecio(feria.precioPuesto)} por puesto", color = FeriColors.Texto, fontSize = 14.sp)
            Text(
                "Puestos libres: ${feria.puestosLibres}",
                color = if (feria.puestosLibres > 0) FeriColors.Disponible else FeriColors.Ocupado,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
