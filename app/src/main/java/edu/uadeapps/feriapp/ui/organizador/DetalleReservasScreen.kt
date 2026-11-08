package edu.uadeapps.feriapp.ui.organizador

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.uadeapps.feriapp.domain.model.ReservaDeFeria
import edu.uadeapps.feriapp.ui.components.EncabezadoFeri
import edu.uadeapps.feriapp.ui.components.EstadoCargando
import edu.uadeapps.feriapp.ui.components.EstadoError
import edu.uadeapps.feriapp.ui.components.LeyendaPlano
import edu.uadeapps.feriapp.ui.components.PlanoPuestos
import edu.uadeapps.feriapp.ui.components.formatearFechaHorario
import edu.uadeapps.feriapp.ui.theme.FeriColors

@Composable
fun DetalleReservasScreen(
    onVolverClick: () -> Unit,
    viewModel: DetalleReservasViewModel = viewModel(factory = DetalleReservasViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        val titulo = (estado as? DetalleReservasUiState.Contenido)?.detalle?.feria?.nombre
        EncabezadoFeri(titulo = "Detalle con reservas", subtitulo = titulo, onVolver = onVolverClick)

        when (val e = estado) {
            is DetalleReservasUiState.Cargando -> EstadoCargando()
            is DetalleReservasUiState.Error -> EstadoError(mensaje = e.mensaje, onReintentar = viewModel::cargar)
            is DetalleReservasUiState.Contenido -> {
                val feria = e.detalle.feria
                val reservados = e.reservas.size
                val total = e.detalle.puestos.size
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        formatearFechaHorario(feria.fecha, feria.horaInicio, feria.horaFin) + " · " + feria.zona,
                        color = FeriColors.TextoSecundario, fontSize = 14.sp
                    )
                    Text(
                        "$reservados reservados · ${total - reservados} libres (de $total)",
                        color = FeriColors.Texto, fontSize = 16.sp, fontWeight = FontWeight.Bold
                    )
                    LeyendaPlano(textoOcupado = "Reservado", mostrarElegido = false)
                    PlanoPuestos(puestos = e.detalle.puestos, textoOcupado = "Reservado")

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Quién reservó", color = FeriColors.Texto, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    if (e.reservas.isEmpty()) {
                        Text("Todavía nadie reservó un puesto en esta feria.", color = FeriColors.TextoSecundario, fontSize = 14.sp)
                    } else {
                        e.reservas.forEach { TarjetaReserva(it) }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaReserva(r: ReservaDeFeria) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, FeriColors.Borde, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Text("Puesto ${r.numeroPuesto} · ${r.ferianteNombre}", color = FeriColors.Texto, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(r.ferianteEmail, color = FeriColors.Texto, fontSize = 14.sp)
            r.ferianteRubro?.takeIf { it.isNotBlank() }?.let {
                Text("Rubro: $it", color = FeriColors.TextoSecundario, fontSize = 14.sp)
            }
            Text("Comprobante ${r.comprobante}", color = FeriColors.TextoSecundario, fontSize = 14.sp)
        }
    }
}
