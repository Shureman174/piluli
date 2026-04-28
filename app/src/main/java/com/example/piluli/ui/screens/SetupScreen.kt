package com.example.piluli.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SetupScreen(onSave: (Int, Int, Int, Int, Boolean) -> Unit) {
    var pillCount by remember { mutableStateOf("21") }
    var currentPill by remember { mutableStateOf("1") }
    var hour by remember { mutableStateOf("9") }
    var minute by remember { mutableStateOf("0") }
    var isLoop by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Настройка", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        
        OutlinedTextField(
            value = pillCount,
            onValueChange = { pillCount = it },
            label = { Text("Таблеток в упаковке") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = currentPill,
            onValueChange = { currentPill = it },
            label = { Text("Номер таблетки сегодня") },
            modifier = Modifier.fillMaxWidth()
        )
        Row {
            OutlinedTextField(
                value = hour,
                onValueChange = { hour = it },
                label = { Text("Часы (0-23)") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = minute,
                onValueChange = { minute = it },
                label = { Text("Минуты (0-59)") },
                modifier = Modifier.weight(1f)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(checked = isLoop, onCheckedChange = { isLoop = it })
            Text("Повторять по кругу", modifier = Modifier.padding(start = 8.dp))
        }
        
        Button(
            onClick = {
                onSave(
                    pillCount.toIntOrNull() ?: 21,
                    currentPill.toIntOrNull() ?: 1,
                    hour.toIntOrNull() ?: 9,
                    minute.toIntOrNull() ?: 0,
                    isLoop
                )
            },
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
        ) {
            Text("Сохранить и начать")
        }
    }
}