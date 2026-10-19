package com.feriapp.backend.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// El enum vive acá, junto a su componente
enum class PantallasFooter {
    FERIAS, RESERVAS, PERFIL
}

@Composable
fun BottomNavigationBar(
    seccionSeleccionada: PantallasFooter,
    onSeccionSeleccionada: (PantallasFooter) -> Unit
) {
    val colorActivo = Color(0xFF1B3B8B)
    val colorInactivo = Color(0xFF556080)
    val fondoActivo = Color(0xFFF2F5FA)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ),
        color = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ItemFooter(
                label = "Ferias",
                icon = Icons.Default.GridView,
                isSelected = seccionSeleccionada == PantallasFooter.FERIAS,
                colorActivo = colorActivo,
                colorInactivo = colorInactivo,
                fondoActivo = fondoActivo,
                onClick = { onSeccionSeleccionada(PantallasFooter.FERIAS) }
            )

            ItemFooter(
                label = "Mis reservas",
                icon = Icons.Default.Check,
                isSelected = seccionSeleccionada == PantallasFooter.RESERVAS,
                colorActivo = colorActivo,
                colorInactivo = colorInactivo,
                fondoActivo = fondoActivo,
                onClick = { onSeccionSeleccionada(PantallasFooter.RESERVAS) }
            )

            ItemFooter(
                label = "Perfil",
                icon = Icons.Outlined.Person,
                isSelected = seccionSeleccionada == PantallasFooter.PERFIL,
                colorActivo = colorActivo,
                colorInactivo = colorInactivo,
                fondoActivo = fondoActivo,
                onClick = { onSeccionSeleccionada(PantallasFooter.PERFIL) }
            )
        }
    }
}

@Composable
fun RowScope.ItemFooter(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    colorActivo: Color,
    colorInactivo: Color,
    fondoActivo: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(72.dp)
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) fondoActivo else Color.Transparent)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) colorActivo else colorInactivo,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                color = if (isSelected) colorActivo else colorInactivo,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}