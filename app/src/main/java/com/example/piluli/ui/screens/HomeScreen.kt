package com.example.piluli.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    currentPill: Int,
    pillCount: Int,
    dosesPerDay: Int,
    lastTakenAt: Long,
    onManualConfirm: () -> Unit,
    onPostpone: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val lastTakenText = remember(lastTakenAt) {
        if (lastTakenAt <= 0L) "Ещё не отмечено"
        else SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(lastTakenAt))
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Piluli") },
                actions = {
                    TextButton(onClick = onOpenSettings) { Text("⚙") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Сегодня нужно принять:", style = MaterialTheme.typography.titleMedium)
            Text(
                "Таблетка №$currentPill",
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center
            )
            Text("В упаковке: $pillCount шт.", style = MaterialTheme.typography.bodyMedium)
            Text("Интенсивность: $dosesPerDay раз(а) в день", style = MaterialTheme.typography.bodyMedium)
            Text("Последний приём: $lastTakenText", style = MaterialTheme.typography.bodyMedium)

            Button(
                onClick = onManualConfirm,
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
            ) {
                Text("Подтвердить приём")
            }
            OutlinedButton(
                onClick = onPostpone,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            ) {
                Text("Отложить на 15 минут")
            }
        }
    }
}
