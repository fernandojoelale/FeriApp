package edu.uadeapps.feriapp.ui.ferias

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.uadeapps.feriapp.ui.components.BotonPrimario
import edu.uadeapps.feriapp.ui.components.BotonSecundario
import edu.uadeapps.feriapp.ui.components.DatoConEtiqueta
import edu.uadeapps.feriapp.ui.components.EncabezadoFeri
import edu.uadeapps.feriapp.ui.components.EstadoCargando
import edu.uadeapps.feriapp.ui.components.EstadoError
import edu.uadeapps.feriapp.ui.components.abrirMapa
import edu.uadeapps.feriapp.ui.components.formatearFecha
import edu.uadeapps.feriapp.ui.components.formatearHorario
import edu.uadeapps.feriapp.ui.components.formatearPrecio
import edu.uadeapps.feriapp.ui.theme.FeriColors

@Composable
fun FeriaDetalleScreen(
    onVolverClick: () -> Unit,
    onElegirPuestoClick: (Long) -> Unit,
    viewModel: FeriaDetalleViewModel = viewModel(factory = FeriaDetalleViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        EncabezadoFeri(titulo = "Detalle de la feria", onVolver = onVolverClick)

        when (val e = estado) {
            is DetalleUiState.Cargando -> EstadoCargando()
            is DetalleUiState.Error -> EstadoError(mensaje = e.mensaje, onReintentar = viewModel::cargar)
            is DetalleUiState.Contenido -> {
                val feria = e.detalle.feria
                val libres = e.detalle.puestos.count { it.disponible }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                ) {
                    Text(feria.nombre, color = FeriColors.Primario, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    DatoConEtiqueta("Fecha", formatearFecha(feria.fecha))
                    DatoConEtiqueta("Horario", formatearHorario(feria.horaInicio, feria.horaFin))
                    DatoConEtiqueta("Zona", feria.zona)
                    DatoConEtiqueta("Dirección", feria.direccion)
                    DatoConEtiqueta("Rubro", feria.rubro)
                    DatoConEtiqueta("Precio puesto", formatearPrecio(feria.precioPuesto))
                    DatoConEtiqueta("Puestos libres", "$libres de ${e.detalle.totalPuestos}")
                    DatoConEtiqueta("Organiza", feria.organizadorNombre)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "El pago del puesto se acuerda con el organizador. La app solo reserva el lugar.",
                        color = FeriColors.TextoSecundario,
                        fontSize = 12.sp
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                        .navigationBarsPadding()
                ) {
                    if (feria.latitud != 0.0 || feria.longitud != 0.0) {
                        BotonSecundario(
                            texto = "Cómo llegar",
                            onClick = { abrirMapa(context, feria.latitud, feria.longitud, feria.nombre) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    BotonPrimario(
                        texto = if (libres > 0) "Elegir puesto" else "Sin puestos libres",
                        onClick = { onElegirPuestoClick(feria.id) },
                        enabled = libres > 0
                    )
                }
            }
        }
    }
}
