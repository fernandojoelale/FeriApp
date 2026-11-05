package edu.uadeapps.feriapp.ui.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    onCerrarSesion: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        EncabezadoFeri(
            titulo = "Mi perfil",
            subtitulo = when (sesion?.rol) {
                Rol.FERIANTE -> "Feriante"
                Rol.ORGANIZADOR -> "Organizador"
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
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = sesion.nombre,
                    color = FeriColors.Primario,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(text = sesion.email, color = FeriColors.TextoSecundario, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(24.dp))
                DatoConEtiqueta(
                    etiqueta = "Tipo de cuenta",
                    valor = if (sesion.rol == Rol.FERIANTE) "Feriante" else "Organizador"
                )
                Spacer(modifier = Modifier.height(8.dp))
                BotonSecundario(texto = "Cerrar sesión", onClick = onCerrarSesion)
            }
        }
    }
}
