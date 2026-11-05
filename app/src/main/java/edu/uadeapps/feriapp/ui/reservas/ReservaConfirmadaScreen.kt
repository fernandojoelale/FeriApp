package edu.uadeapps.feriapp.ui.reservas

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
fun ReservaConfirmadaScreen(
    onVerMisReservas: () -> Unit,
    viewModel: ReservaConfirmadaViewModel = viewModel(factory = ReservaConfirmadaViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        EncabezadoFeri(titulo = "Reserva confirmada")

        when (val e = estado) {
            is ReservaUiState.Cargando -> EstadoCargando()
            is ReservaUiState.Error -> EstadoError(mensaje = e.mensaje, onReintentar = viewModel::cargar)
            is ReservaUiState.Contenido -> {
                val r = e.reserva
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                ) {
                    Text("¡Listo! Tu puesto está reservado", color = FeriColors.Disponible, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Comprobante ${r.comprobante}", color = FeriColors.Primario, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(16.dp))
                    DatoConEtiqueta("Feria", r.feriaNombre)
                    DatoConEtiqueta("Puesto", "N° ${r.numeroPuesto}")
                    DatoConEtiqueta("Fecha", formatearFecha(r.feriaFecha))
                    DatoConEtiqueta("Horario", formatearHorario(r.horaInicio, r.horaFin))
                    DatoConEtiqueta("Dirección", r.direccion)
                    DatoConEtiqueta("Zona", r.zona)
                    DatoConEtiqueta("Precio", formatearPrecio(r.precioPuesto))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "El pago no se hace en la app: coordinalo con el organizador.",
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
                    if (r.latitud != 0.0 || r.longitud != 0.0) {
                        BotonSecundario(
                            texto = "Cómo llegar",
                            onClick = { abrirMapa(context, r.latitud, r.longitud, r.feriaNombre) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    BotonPrimario(texto = "Ver mis reservas", onClick = onVerMisReservas)
                }
            }
        }
    }
}
