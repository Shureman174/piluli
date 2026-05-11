package com.example.piluli.medications.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.piluli.medications.data.IntakeLogEntity
import com.example.piluli.medications.ui.MedicationDetailViewModel
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationDetailScreen(
    medicationId: Long,
    viewModel: MedicationDetailViewModel,
    onBack: () -> Unit,
) {
    LaunchedEffect(medicationId) { viewModel.load(medicationId) }
    val medication by viewModel.medication.collectAsState()
    val logs by viewModel.intakeLogs.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(medication?.name ?: "Препарат") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AsyncImage(
                    model = medication?.photoUri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                )
                Text(medication?.description.orEmpty(), style = MaterialTheme.typography.bodyMedium)

                Text(
                    "Интенсивность: ${medication?.intakesPerDay ?: 1} раз/день",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (!medication?.intakeTimes.isNullOrEmpty()) {
                    Text(
                        "Времена: ${medication?.intakeTimes?.joinToString().orEmpty()}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (medication?.totalCourseAmount != null) {
                    Text("На курс: ${medication?.totalCourseAmount} шт.", style = MaterialTheme.typography.bodyMedium)
                }

                Button(onClick = { viewModel.takeNow(amount = 1) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Принять сейчас")
                }
            }

            Text(
                "История приёмов",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.titleMedium
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(logs, key = { it.id }) { log ->
                    IntakeRow(log)
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun IntakeRow(log: IntakeLogEntity) {
    val df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(df.format(Date(log.takenAtMillis)), style = MaterialTheme.typography.bodyLarge)
        Text("x${log.amount}", style = MaterialTheme.typography.bodyLarge)
    }
}

