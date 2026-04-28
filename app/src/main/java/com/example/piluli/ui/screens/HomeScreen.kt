package com.example.piluli.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(currentPill: Int, pillCount: Int, onManualConfirm: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
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
        
        Button(
            onClick = onManualConfirm,
            modifier = Modifier.fillMaxWidth().padding(top = 32.dp)
        ) {
            Text("Я принял(а) таблетку")
        }
    }
}