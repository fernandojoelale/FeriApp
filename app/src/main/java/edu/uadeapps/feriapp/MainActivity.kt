package edu.uadeapps.feriapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import edu.uadeapps.feriapp.domain.model.Rol
import edu.uadeapps.feriapp.notificaciones.Notificaciones
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import edu.uadeapps.feriapp.ui.MainScreen
import edu.uadeapps.feriapp.ui.PantallasFooter
import edu.uadeapps.feriapp.ui.auth.LoginScreen
import edu.uadeapps.feriapp.ui.auth.RegistroScreen
import edu.uadeapps.feriapp.ui.ferias.FeriaDetalleScreen
import edu.uadeapps.feriapp.ui.ferias.PuestosScreen
import edu.uadeapps.feriapp.ui.reservas.ReservaConfirmadaScreen
import edu.uadeapps.feriapp.ui.organizador.DetalleReservasScreen
import edu.uadeapps.feriapp.ui.organizador.PublicarFeriaScreen
import edu.uadeapps.feriapp.ui.sesion.SesionViewModel
import edu.uadeapps.feriapp.ui.theme.FeriAppTheme

class MainActivity : ComponentActivity() {

    // Feria a la que hay que ir porque el usuario tocó una notificación de reserva (RF04).
    private var feriaDeNotificacion by mutableStateOf<Long?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        feriaDeNotificacion = leerFeriaDe(intent)
        setContent {
            FeriAppTheme {
                AppNavigation(
                    feriaDeNotificacion = feriaDeNotificacion,
                    onNotificacionAtendida = { feriaDeNotificacion = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        feriaDeNotificacion = leerFeriaDe(intent)
    }

    private fun leerFeriaDe(intent: Intent?): Long? =
        intent?.getLongExtra(Notificaciones.EXTRA_FERIA_ID, -1L)?.takeIf { it > 0 }
}

@Composable
fun AppNavigation(
    feriaDeNotificacion: Long? = null,
    onNotificacionAtendida: () -> Unit = {}
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sesionViewModel: SesionViewModel = viewModel(factory = SesionViewModel.Factory)
    val sesion by sesionViewModel.sesion.collectAsState()
    var seccionInicial by remember { mutableStateOf(PantallasFooter.FERIAS) }

    // Android 13+: el organizador debe aceptar las notificaciones para recibir los avisos de reserva.
    val pedirNotificaciones = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    var permisoYaPedido by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(sesion?.rol) {
        if (sesion?.rol == Rol.ORGANIZADOR && !permisoYaPedido && Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            permisoYaPedido = true
            pedirNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    NavHost(navController = navController, startDestination = "main_flow") {

        composable("main_flow") {
            MainScreen(
                sesion = sesion,
                seccionInicial = seccionInicial,
                onLoginClick = { navController.navigate("login") },
                onRegistroClick = { navController.navigate("registro") },
                onFeriaClick = { id -> navController.navigate("feria/$id") },
                onFeriaOrganizadorClick = { id -> navController.navigate("reservas-feria/$id") },
                onReservaClick = { id -> navController.navigate("reserva/$id") },
                onPublicarFeriaClick = { navController.navigate("publicar") },
                onCerrarSesion = { sesionViewModel.cerrarSesion() }
            )
        }

        composable("login") {
            LoginScreen(
                onVolverClick = { navController.popBackStack() },
                onLoginExitoso = { navController.popBackStack() },
                onCrearCuentaClick = { navController.navigate("registro") }
            )
        }

        composable("publicar") {
            PublicarFeriaScreen(
                onVolverClick = { navController.popBackStack() },
                onPublicada = { navController.popBackStack() }
            )
        }

        composable("registro") {
            RegistroScreen(
                onVolverClick = { navController.popBackStack() },
                onRegistroExitoso = {
                    // Se vuelve a donde estaba el usuario antes de entrar a login/registro.
                    if (!navController.popBackStack("login", true)) navController.popBackStack()
                },
                onIniciarSesionClick = {
                    if (!navController.popBackStack("login", false)) {
                        navController.navigate("login") { popUpTo("registro") { inclusive = true } }
                    }
                }
            )
        }

        composable(
            route = "feria/{feriaId}",
            arguments = listOf(navArgument("feriaId") { type = NavType.LongType })
        ) {
            FeriaDetalleScreen(
                onVolverClick = { navController.popBackStack() },
                onElegirPuestoClick = { id -> navController.navigate("puestos/$id") }
            )
        }

        composable(
            route = "reservas-feria/{feriaId}",
            arguments = listOf(navArgument("feriaId") { type = NavType.LongType })
        ) {
            DetalleReservasScreen(onVolverClick = { navController.popBackStack() })
        }

        composable(
            route = "puestos/{feriaId}",
            arguments = listOf(navArgument("feriaId") { type = NavType.LongType })
        ) {
            PuestosScreen(
                onVolverClick = { navController.popBackStack() },
                onReservaCreada = { reservaId ->
                    navController.navigate("reserva/$reservaId") {
                        popUpTo("main_flow")
                    }
                },
                onLoginClick = { navController.navigate("login") }
            )
        }

        composable(
            route = "reserva/{reservaId}",
            arguments = listOf(navArgument("reservaId") { type = NavType.LongType })
        ) {
            ReservaConfirmadaScreen(
                onVerMisReservas = {
                    seccionInicial = PantallasFooter.RESERVAS
                    navController.popBackStack("main_flow", false)
                }
            )
        }
    }

    // Tocar la notificación de una reserva lleva al detalle con reservas de esa feria (organizador).
    LaunchedEffect(feriaDeNotificacion) {
        feriaDeNotificacion?.let { id ->
            navController.navigate("reservas-feria/$id")
            onNotificacionAtendida()
        }
    }
}
