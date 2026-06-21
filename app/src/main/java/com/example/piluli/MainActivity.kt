package com.example.piluli

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.piluli.ui.theme.PiluliTheme
import com.example.piluli.ui.screens.HomeScreen

/**
 * Main entry point for the Piluli app.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PiluliTheme {
                HomeScreen()
            }
        }
    }
}
