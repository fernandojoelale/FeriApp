package edu.uadeapps.feriapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import edu.uadeapps.feriapp.ui.MainScreen
import edu.uadeapps.feriapp.ui.theme.FeriAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FeriAppTheme {
                MainScreen()
            }
        }
    }
}