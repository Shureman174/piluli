package com.example.piluli

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.piluli.ui.screens.HomeScreen
import com.example.piluli.ui.screens.SetupScreen
import com.example.piluli.ui.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable  // ← ОБЯЗАТЕЛЬНО: эта аннотация была пропущена!
fun MainScreen(viewModel: AppViewModel = viewModel()) {
    val settings by viewModel.settings.collectAsState()
    val isFirstRun = settings["isFirstRun"] as? Boolean ?: true

    if (isFirstRun) {
        SetupScreen(
            onSave = { pillCount, currentPill, hour, minute, isLoop ->
                viewModel.saveSettings(pillCount, currentPill, hour, minute, isLoop)
            }
        )
    } else {
        HomeScreen(
            currentPill = (settings["currentPill"] as? Int) ?: 1,
            pillCount = (settings["pillCount"] as? Int) ?: 21,
            onManualConfirm = { viewModel.manualIncrement() }
        )
    }
}