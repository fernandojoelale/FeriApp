package edu.uadeapps.feriapp.ui.reservas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.uadeapps.feriapp.domain.model.Reserva
import edu.uadeapps.feriapp.domain.model.Rol
import edu.uadeapps.feriapp.domain.model.Session
import edu.uadeapps.feriapp.ui.components.BotonPrimario
import edu.uadeapps.feriapp.ui.components.EncabezadoFeri
import edu.uadeapps.feriapp.ui.components.EstadoCargando
import edu.uadeapps.feriapp.ui.components.EstadoError
import edu.uadeapps.feriapp.ui.components.EstadoVacio
import edu.uadeapps.feriapp.ui.components.esFechaPasada
import edu.uadeapps.feriapp.ui.components.formatearFecha
import edu.uadeapps.feriapp.ui.components.formatearHorario
import edu.uadeapps.feriapp.ui.components.formatearPrecio
import edu.uadeapps.feriapp.ui.theme.FeriColors

@Composable
fun MisReservasScreen(
    sesion: Session?,
    onLoginClick: () -> Unit,
    onReservaClick: (Long) -> Unit,
    viewModel: MisReservasViewModel = viewModel(factory = MisReservasViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsState()
    val esFeriante = sesion?.rol == Rol.FERIANTE

    // Recarga cada vez que se entra a la sección o cambia la sesión.
    LaunchedEffect(sesion?.usuarioId) {
        if (esFeriante) viewModel.cargar()
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        EncabezadoFeri(titulo = "Mis reservas", subtitulo = "Tus puestos reservados")

        when {
            sesion == null -> EstadoVacio(
                titulo = "Iniciá sesión",
                mensaje = "Para ver tus reservas necesitás ingresar con tu cuenta de feriante."
            ) { BotonPrimario(texto = "Iniciar sesión", onClick = onLoginClick) }

            !esFeriante -> EstadoVacio(
                titulo = "Solo para feriantes",
                mensaje = "Las reservas de puestos las hacen las cuentas de feriante."
            )

            else -> when (val e = estado) {
                is MisReservasUiState.Cargando -> EstadoCargando()
                is MisReservasUiState.Error -> EstadoError(mensaje = e.mensaje, onReintentar = viewModel::cargar)
                is MisReservasUiState.Contenido -> {
                    if (e.reservas.isEmpty()) {
                        EstadoVacio(
                            titulo = "Todavía no reservaste puestos",
                            mensaje = "Buscá una feria y elegí tu lugar."
                        )
                    } else {
                        val (pasadas, proximas) = e.reservas.partition { esFechaPasada(it.feriaFecha) }
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (proximas.isNotEmpty()) {
                                item { Text("Próximas", color = FeriColors.Primario, fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                                items(proximas, key = { it.id }) { TarjetaReserva(it, onReservaClick) }
                            }
                            if (pasadas.isNotEmpty()) {
                                item { Text("Pasadas", color = FeriColors.TextoSecundario, fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                                items(pasadas, key = { it.id }) { TarjetaReserva(it, onReservaClick) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaReserva(reserva: Reserva, onClick: (Long) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, FeriColors.Borde, RoundedCornerShape(16.dp))
            .clickable { onClick(reserva.id) }
            .padding(16.dp)
    ) {
        Text(reserva.feriaNombre, color = FeriColors.Texto, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(
            "${formatearFecha(reserva.feriaFecha)} · ${formatearHorario(reserva.horaInicio, reserva.horaFin)}",
            color = FeriColors.TextoSecundario,
            fontSize = 13.sp
        )
        Text(
            "Puesto N° ${reserva.numeroPuesto} · ${formatearPrecio(reserva.precioPuesto)}",
            color = FeriColors.Texto,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(reserva.comprobante, color = FeriColors.Primario, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
    }
}
