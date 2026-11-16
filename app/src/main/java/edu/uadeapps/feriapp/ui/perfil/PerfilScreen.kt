package edu.uadeapps.feriapp.ui.perfil

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import android.content.Intent
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import edu.uadeapps.feriapp.ui.components.AvisoCard
import edu.uadeapps.feriapp.ui.components.TipoAviso
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.uadeapps.feriapp.domain.model.Rol
import edu.uadeapps.feriapp.domain.model.Session
import edu.uadeapps.feriapp.ui.components.BotonPrimario
import edu.uadeapps.feriapp.ui.components.BotonSecundario
import edu.uadeapps.feriapp.ui.components.DatoConEtiqueta
import edu.uadeapps.feriapp.ui.components.EncabezadoFeri
import edu.uadeapps.feriapp.ui.components.EstadoVacio
import edu.uadeapps.feriapp.ui.theme.FeriColors

@Composable
fun PerfilScreen(
    sesion: Session?,
    onLoginClick: () -> Unit,
    onRegistroClick: () -> Unit,
    onCerrarSesion: () -> Unit,
    onPublicarFeriaClick: (() -> Unit)? = null,
    onIrAReservasClick: (() -> Unit)? = null
) {
    // Se vuelve a consultar al regresar de los ajustes del sistema.
    val context = LocalContext.current
    var notificacionesActivas by remember { mutableStateOf(NotificationManagerCompat.from(context).areNotificationsEnabled()) }
    var showConfirmLogoutDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        val ciclo = (context as? ComponentActivity)?.lifecycle
        val observador = LifecycleEventObserver { _, evento ->
            if (evento == Lifecycle.Event.ON_RESUME) {
                notificacionesActivas = NotificationManagerCompat.from(context).areNotificationsEnabled()
            }
        }
        ciclo?.addObserver(observador)
        onDispose { ciclo?.removeObserver(observador) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        EncabezadoFeri(
            titulo = "Mi perfil",
            subtitulo = when (sesion?.rol) {
                Rol.FERIANTE -> "Cuenta de Feriante"
                Rol.ORGANIZADOR -> "Cuenta de Organizador"
                null -> "Sin sesión iniciada"
            }
        )

        if (sesion == null) {
            EstadoVacio(
                titulo = "Todavía no iniciaste sesión",
                mensaje = "Podés ver las ferias sin cuenta. Para reservar un puesto o publicar una feria necesitás iniciar sesión."
            ) {
                BotonPrimario(texto = "Iniciar sesión", onClick = onLoginClick)
                Spacer(modifier = Modifier.height(12.dp))
                BotonSecundario(texto = "Crear cuenta", onClick = onRegistroClick)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar con iniciales
                val inicial = sesion.nombre.firstOrNull()?.uppercaseChar() ?: '?'
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(FeriColors.Primario),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = inicial.toString(),
                        color = Color.White,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = sesion.nombre,
                    color = FeriColors.Texto,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = sesion.email,
                    color = FeriColors.TextoSecundario,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Tarjeta de detalles de la cuenta
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF9FAFB),
                    border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Información de la cuenta",
                            color = FeriColors.Primario,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        DatoConEtiqueta(etiqueta = "ID de usuario", valor = "#${sesion.usuarioId}")
                        DatoConEtiqueta(
                            etiqueta = "Rol",
                            valor = if (sesion.rol == Rol.FERIANTE) "Feriante" else "Organizador"
                        )
                        DatoConEtiqueta(etiqueta = "Estado", valor = "Activo")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Acciones rápidas según el rol
                if (sesion.rol == Rol.ORGANIZADOR) {
                    if (onPublicarFeriaClick != null) {
                        Button(
                            onClick = onPublicarFeriaClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FeriColors.Primario)
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Publicar nueva feria", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Gestión de notificaciones para organizador
                    if (notificacionesActivas) {
                        AvisoCard(
                            titulo = "Notificaciones activas",
                            mensaje = "Recibís alertas en tiempo real cuando un feriante reserve un puesto en tus ferias.",
                            tipo = TipoAviso.INFO
                        )
                    } else {
                        AvisoCard(
                            titulo = "Notificaciones desactivadas",
                            mensaje = "No vas a recibir alertas cuando un feriante reserve un puesto de tus ferias.",
                            tipo = TipoAviso.ADVERTENCIA
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        BotonSecundario(
                            texto = "Activar en ajustes del sistema",
                            onClick = {
                                val ajustes = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                context.startActivity(ajustes)
                            }
                        )
                    }
                } else {
                    // Acciones rápidas para Feriante
                    if (onIrAReservasClick != null) {
                        OutlinedButton(
                            onClick = onIrAReservasClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, FeriColors.Primario),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = FeriColors.Primario)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ver mis reservas de puestos", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botón de ayuda / soporte
                OutlinedButton(
                    onClick = { showHelpDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.LightGray),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray)
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color.DarkGray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ayuda y soporte", fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón Cerrar sesión
                OutlinedButton(
                    onClick = { showConfirmLogoutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, FeriColors.Error),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FeriColors.Error)
                ) {
                    Icon(Icons.Default.PowerSettingsNew, contentDescription = null, tint = FeriColors.Error)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cerrar sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "FeriApp v1.0.0 · Plataforma de gestión de ferias",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }

    // Diálogo de confirmación para cerrar sesión
    if (showConfirmLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmLogoutDialog = false },
            title = { Text(text = "Cerrar sesión", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que querés cerrar sesión en FeriApp?") },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmLogoutDialog = false
                        onCerrarSesion()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FeriColors.Error)
                ) {
                    Text("Sí, cerrar sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmLogoutDialog = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }

    // Diálogo de Ayuda y Soporte
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text(text = "Ayuda y Soporte", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "FeriApp conecta organizadores de ferias y feriantes de forma directa.\n\n" +
                                "• Si sos Feriante: Buscá ferias disponibles y reservá tu puesto en el plano.\n" +
                                "• Si sos Organizador: Publicá tus ferias y gestioná los puestos de tu predio.\n\n" +
                                "Para consultas adicionales, contactanos en soporte@feriapp.com.",
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        lineHeight = 20.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showHelpDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = FeriColors.Primario)
                ) {
                    Text("Entendido")
                }
            }
        )
    }
}
