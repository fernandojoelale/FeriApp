package edu.uadeapps.feriapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
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
import edu.uadeapps.feriapp.ui.sesion.SesionViewModel
import edu.uadeapps.feriapp.ui.theme.FeriAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FeriAppTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val sesionViewModel: SesionViewModel = viewModel(factory = SesionViewModel.Factory)
    val sesion by sesionViewModel.sesion.collectAsState()
    var seccionInicial by remember { mutableStateOf(PantallasFooter.FERIAS) }

    NavHost(navController = navController, startDestination = "main_flow") {

        composable("main_flow") {
            MainScreen(
                sesion = sesion,
                seccionInicial = seccionInicial,
                onLoginClick = { navController.navigate("login") },
                onRegistroClick = { navController.navigate("registro") },
                onFeriaClick = { id -> navController.navigate("feria/$id") },
                onReservaClick = { id -> navController.navigate("reserva/$id") },
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
}
