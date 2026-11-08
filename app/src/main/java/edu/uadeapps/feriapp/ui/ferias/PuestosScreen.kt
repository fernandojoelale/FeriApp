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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import edu.uadeapps.feriapp.ui.components.LeyendaPlano
import edu.uadeapps.feriapp.ui.components.PlanoPuestos
import edu.uadeapps.feriapp.ui.components.TipoAviso
import edu.uadeapps.feriapp.ui.components.formatearActualizacion
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
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    LeyendaPlano()
                    Spacer(modifier = Modifier.height(12.dp))
                    PlanoPuestos(
                        puestos = detalle.puestos,
                        seleccionadoId = estado.seleccionadoId,
                        onPuestoClick = { viewModel.seleccionar(it) }
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .navigationBarsPadding()
                ) {
                    if (estado.sinConexion) {
                        AvisoCard(
                            titulo = "Sin conexión · plano guardado",
                            mensaje = "Este plano es de ${formatearActualizacion(detalle.actualizadoEn)} y puede estar desactualizado. " +
                                "Reservá cuando tengas internet.",
                            tipo = TipoAviso.ADVERTENCIA
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        BotonSecundario(texto = "Reintentar conexión", onClick = { viewModel.cargar() })
                        Spacer(modifier = Modifier.height(8.dp))
                    }
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
                        enabled = elegido != null && !estado.sinConexion,
                        cargando = estado.reservando
                    )
                }
            }
        }
    }
}
