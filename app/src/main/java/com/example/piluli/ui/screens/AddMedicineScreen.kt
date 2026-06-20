package com.example.piluli.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun AddMedicineScreen() {
    var name by remember { mutableStateOf("") }
    
    Column {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Введите название") }
        )
        Button(onClick = { /* TODO */ }) {
            Text("Сохранить")
        }
    }
}
