
package edu.uadeapps.feriapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FeriAppColors = lightColorScheme(
    primary = Color(0xFF1B3B8B),
    secondary = Color(0xFF556080),
    background = Color(0xFFF8F9FC),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF202124),
    onSurface = Color(0xFF202124)
)

@Composable
fun FeriAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FeriAppColors,
        content = content
    )
}
