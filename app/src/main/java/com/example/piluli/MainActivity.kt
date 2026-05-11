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
import com.example.piluli.medications.ui.MedApp
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

    // Старый сценарий (1 таблетка в день) пока оставляем, но новым главным экраном делаем справочник.
    // Вернёмся и аккуратно интегрируем/удалим после стабилизации "Препараты + журнал".
    MedApp(modifier = Modifier.fillMaxSize())
}