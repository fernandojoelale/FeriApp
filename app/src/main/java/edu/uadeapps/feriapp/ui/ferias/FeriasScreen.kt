package edu.uadeapps.feriapp.ui.ferias

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import edu.uadeapps.feriapp.ui.components.AvisoCard
import edu.uadeapps.feriapp.ui.components.TipoAviso
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.uadeapps.feriapp.core.model.FeriasUiState

data class FeriaItem(
    val id: Long = 0,
    val nombre: String,
    val fecha: String,
    val barrio: String,
    val rubro: String = "",
    val distancia: String,
    val precio: String,
    val precioNumerico: Double = 0.0,
    val puestosLibres: Int
)

enum class OrdenFerias { NINGUNO, FECHA, PRECIO }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeriasScreen(
    viewModel: FeriasViewModel = viewModel(factory = FeriasViewModel.Factory),
    onLoginClick: () -> Unit,
    onFeriaClick: (Long) -> Unit = {}
) {

    val uiState by viewModel.uiState.collectAsState()
    val refrescando by viewModel.refrescando.collectAsState()

    // Cada vez que se vuelve a esta pantalla (de un detalle, de otra pestaña, de publicar una feria)
    // se actualiza la lista en silencio, así aparecen las ferias nuevas sin reiniciar la app.
    LaunchedEffect(Unit) { viewModel.refrescar() }

    var searchQuery by remember { mutableStateOf("") }
    var rubroSeleccionado by remember { mutableStateOf("Todos los rubros") }
    var menuExpanded by remember { mutableStateOf(false) }
    var orden by remember { mutableStateOf(OrdenFerias.NINGUNO) }
    var soloConLugar by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // HEADER SUPERIOR
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1B3B8B))
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Ferias disponibles",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Encontrá un espacio para tus productos",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp
                    )
                }
                IconButton(onClick = onLoginClick, modifier = Modifier.padding(start = 16.dp)) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Iniciar sesión",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        // CONTROL DE ESTADOS DE LA PANTALLA
        when (val state = uiState) {
            is FeriasUiState.Cargando -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF1B3B8B))
                }
            }
            is FeriasUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = state.mensaje, color = Color.Red)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.cargarFerias() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            is FeriasUiState.Vacio -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No hay ferias disponibles", color = Color.Gray)
                }
            }
            is FeriasUiState.Contenido -> {
                // Lista de rubros únicos obtenida dinámicamente de las ferias del Backend
                val rubrosDisponibles = remember(state.ferias) {
                    listOf("Todos los rubros") + state.ferias.map { it.rubro }.filter { it.isNotBlank() }.distinct()
                }

                // Filtrado dinámico por búsqueda y rubro desplegable
                val feriasFiltradas = state.ferias.filter { feria ->
                    val coincideBusqueda = searchQuery.isBlank() ||
                            feria.nombre.contains(searchQuery, ignoreCase = true) ||
                            feria.barrio.contains(searchQuery, ignoreCase = true) ||
                            feria.rubro.contains(searchQuery, ignoreCase = true)

                    val coincideRubro = rubroSeleccionado == "Todos los rubros" ||
                            feria.rubro.equals(rubroSeleccionado, ignoreCase = true)

                    coincideBusqueda && coincideRubro
                }.let { lista ->
                    val conLugar = if (soloConLugar) lista.filter { it.puestosLibres > 0 } else lista
                    when (orden) {
                        OrdenFerias.FECHA -> conLugar.sortedBy { it.fecha } // AAAA-MM-DD ordena bien como texto
                        OrdenFerias.PRECIO -> conLugar.sortedBy { it.precioNumerico }
                        OrdenFerias.NINGUNO -> conLugar
                    }
                }

                PullToRefreshBox(
                    isRefreshing = refrescando,
                    onRefresh = { viewModel.refrescar() },
                    modifier = Modifier.fillMaxSize()
                ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (state.desdeCache) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            AvisoCard(
                                titulo = "Sin conexión",
                                mensaje = "Mostramos la última lista guardada: puede estar desactualizada. Para reservar necesitás internet.",
                                tipo = TipoAviso.ADVERTENCIA
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // BUSCADOR
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            placeholder = { Text("Buscar por nombre o zona") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Buscar",
                                    tint = Color(0xFF6B7280)
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Limpiar",
                                            tint = Color(0xFF6B7280)
                                        )
                                    }
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1B3B8B),
                                unfocusedBorderColor = Color(0xFFE5E7EB)
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // DESPLEGABLE DE RUBROS
                        ExposedDropdownMenuBox(
                            expanded = menuExpanded,
                            onExpandedChange = { menuExpanded = !menuExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = rubroSeleccionado,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Filtrar por rubro") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpanded)
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF1B3B8B),
                                    unfocusedBorderColor = Color(0xFFE5E7EB)
                                ),
                                modifier = Modifier
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false }
                            ) {
                                rubrosDisponibles.forEach { rubro ->
                                    DropdownMenuItem(
                                        text = { Text(rubro) },
                                        onClick = {
                                            rubroSeleccionado = rubro
                                            menuExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // ORDEN Y DISPONIBILIDAD (RF02)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = orden == OrdenFerias.FECHA,
                                onClick = { orden = if (orden == OrdenFerias.FECHA) OrdenFerias.NINGUNO else OrdenFerias.FECHA },
                                label = { Text("Próximas") }
                            )
                            FilterChip(
                                selected = orden == OrdenFerias.PRECIO,
                                onClick = { orden = if (orden == OrdenFerias.PRECIO) OrdenFerias.NINGUNO else OrdenFerias.PRECIO },
                                label = { Text("Más baratas") }
                            )
                            FilterChip(
                                selected = soloConLugar,
                                onClick = { soloConLugar = !soloConLugar },
                                label = { Text("Con lugar") }
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "${feriasFiltradas.size} ${if (feriasFiltradas.size == 1) "feria encontrada" else "ferias encontradas"}",
                            fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937)
                        )
                    }

                    if (feriasFiltradas.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No hay ferias que coincidan con la búsqueda o el rubro seleccionado.",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else {
                        items(feriasFiltradas) { feria ->
                            FeriaCard(feria, onClick = { onFeriaClick(feria.id) })
                        }
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
                }
            }
        }
    }
}

@Composable
fun FeriaCard(feria: FeriaItem, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).clickable(onClick = onClick).border(1.dp, Color(0xFFECEFF3), RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(70.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF2F5FA)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Storefront, contentDescription = "Puesto", tint = Color(0xFF1B3B8B), modifier = Modifier.size(36.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = feria.nombre, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = feria.fecha, fontSize = 12.sp, color = Color(0xFF6B7280))
                Text(
                    text = if (feria.rubro.isNotBlank()) "${feria.barrio} · ${feria.rubro}" else "${feria.barrio} · ${feria.distancia}",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${feria.precio} / puesto", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B3B8B))
                Spacer(modifier = Modifier.height(6.dp))

                val isLowOnStock = feria.puestosLibres <= 1
                val pillBg = if (isLowOnStock) Color(0xFFFEF3C7) else Color(0xFFE8F5E9)
                val pillTxt = if (isLowOnStock) Color(0xFFB45309) else Color(0xFF15803D)

                Box(
                    modifier = Modifier.clip(RoundedCornerShape(50)).background(pillBg).padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (feria.puestosLibres == 1) "1 puesto libre" else "${feria.puestosLibres} puestos libres",
                        fontSize = 11.sp, fontWeight = FontWeight.Bold, color = pillTxt
                    )
                }
            }
        }
    }
}