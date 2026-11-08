package edu.uadeapps.feriapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.uadeapps.feriapp.domain.model.Puesto
import edu.uadeapps.feriapp.ui.theme.FeriColors
import androidx.compose.foundation.layout.Spacer

/**
 * Plano del predio: cada puesto se dibuja en su columna (posX) y fila (posY).
 * El estado nunca depende solo del color: cada puesto tiene su número y una marca de texto
 * (✕ = ocupado, ✓ = elegido) y TalkBack lo lee completo ("Puesto 3, Libre").
 */
@Composable
fun PlanoPuestos(
    puestos: List<Puesto>,
    modifier: Modifier = Modifier,
    seleccionadoId: Long? = null,
    textoOcupado: String = "Ocupado",
    onPuestoClick: ((Puesto) -> Unit)? = null
) {
    if (puestos.isEmpty()) return
    val columnas = puestos.maxOf { it.posX } + 1
    val filas = puestos.maxOf { it.posY } + 1
    val porPosicion = puestos.associateBy { it.posX to it.posY }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        for (y in 0 until filas) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (x in 0 until columnas) {
                    Box(modifier = Modifier.weight(1f).aspectRatio(1f)) {
                        val puesto = porPosicion[x to y]
                        if (puesto != null) {
                            CeldaPuesto(
                                puesto = puesto,
                                elegido = puesto.id == seleccionadoId,
                                textoOcupado = textoOcupado,
                                onClick = onPuestoClick?.let { accion -> { accion(puesto) } }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CeldaPuesto(puesto: Puesto, elegido: Boolean, textoOcupado: String, onClick: (() -> Unit)?) {
    val fondo = when {
        elegido -> FeriColors.Primario
        puesto.disponible -> FeriColors.Disponible.copy(alpha = 0.12f)
        else -> FeriColors.Ocupado
    }
    val colorTexto = if (elegido || !puesto.disponible) Color.White else FeriColors.Disponible
    val borde = if (puesto.disponible || elegido) FeriColors.Disponible else FeriColors.Ocupado
    val estadoTexto = when {
        elegido -> "Elegido"
        puesto.disponible -> "Libre"
        else -> textoOcupado
    }
    val marca = when {
        elegido -> "✓"
        puesto.disponible -> ""
        else -> "✕"
    }
    var modificador = Modifier
        .fillMaxSize()
        .clip(RoundedCornerShape(10.dp))
        .background(fondo)
        .border(1.dp, borde, RoundedCornerShape(10.dp))
    if (onClick != null && puesto.disponible) modificador = modificador.clickable(onClick = onClick)
    Column(
        modifier = modificador.semantics { contentDescription = "Puesto ${puesto.numero}, $estadoTexto" },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("${puesto.numero}", color = colorTexto, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        if (marca.isNotEmpty()) Text(marca, color = colorTexto, fontSize = 14.sp)
    }
}

/** Referencias del plano, con el mismo tamaño de texto que el resto del contenido (14 sp). */
@Composable
fun LeyendaPlano(textoOcupado: String = "Ocupado", mostrarElegido: Boolean = true) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
        ItemLeyenda("Libre", FeriColors.Disponible)
        ItemLeyenda("✕ $textoOcupado", FeriColors.Ocupado)
        if (mostrarElegido) ItemLeyenda("✓ Elegido", FeriColors.Primario)
    }
}

@Composable
private fun ItemLeyenda(texto: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(14.dp).clip(RoundedCornerShape(4.dp)).background(color))
        Spacer(modifier = Modifier.size(4.dp))
        Text(texto, fontSize = 14.sp, color = FeriColors.Texto)
    }
}
