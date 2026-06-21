package com.example.piluli.ui.screens

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import com.example.piluli.model.Medicine
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddMedicineScreen() {
    var name by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = name, 
            onValueChange = { name = it }, 
            label = { Text("Medicine Name") }
        )
        Button(onClick = { /* Logic */ }) {
            Text("Add Medicine")
        }
    }
}
