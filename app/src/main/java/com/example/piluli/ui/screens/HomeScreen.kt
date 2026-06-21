package com.example.piluli.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import java.time.LocalDate

// --- DATA STRUCTURES ---
data class Medication(
    val name: String,
    val dosage: String,
    val timeSlot: String
)

data class DaySchedule(
    val date: LocalDate,
    val medications: List<Medication>,
    val pillCountAvailable: Int,
    val pillsTakenToday: Int
)

// --- VIEWMODEL & REPOSITORY ---
class HomeScreenViewModel(application: Application) : AndroidViewModel(application) {
    private val appRepository = AppRepository(application)

    private val _selectedDate = mutableStateOf(LocalDate.now())
    val selectedDate: State<LocalDate> = _selectedDate

    val daySchedule = derivedStateOf { 
        appRepository.getDaySchedule(_selectedDate.value) ?: DaySchedule(_selectedDate.value, emptyList(), 0, 0)
    }

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }
}

class AppRepository(private val application: Application) {
    fun getDaySchedule(date: LocalDate): DaySchedule? {
        // Mock data
        return DaySchedule(date, listOf(Medication("Аспирин", "1 таб", "Утро")), 10, 1)
    }
}

// --- UI COMPONENTS ---
@Composable
fun HomeScreen(viewModel: HomeScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val selectedDate by viewModel.selectedDate
    val daySchedule by viewModel.daySchedule

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(title = { Text("Приложение для приема лекарств") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            CalendarWidget(selectedDate, onDateSelected = { viewModel.setSelectedDate(it) })
            Spacer(modifier = Modifier.height(16.dp))
            MedicationListView(daySchedule.medications)
        }
    }
}

@Composable
fun CalendarWidget(selectedDate: LocalDate, onDateSelected: (LocalDate) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Дата: $selectedDate", style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = { onDateSelected(selectedDate.plusDays(1)) }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Next Day")
                }
            }
        }
    }
}

@Composable
fun MedicationListView(medications: List<Medication>) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(medications) { medication ->
            MedicationItem(medication = medication)
        }
    }
}

@Composable
fun MedicationItem(medication: Medication) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Text(text = medication.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "${medication.dosage} ${medication.timeSlot}")
        }
    }
}
