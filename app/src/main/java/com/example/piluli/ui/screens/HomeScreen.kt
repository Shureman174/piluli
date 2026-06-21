package com.example.piluli.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate

// --- МOCK DATA STRUCTURES (Для демонстрации структуры MVP) ---
data class Medication(
    val name: String,
    val dosage: String,
    val timeSlot: String // "Утро", "День", "Вечер"
)

data class DaySchedule(
    val date: LocalDate,
    val medications: List<Medication>,
    val pillCountAvailable: Int,
    val pillsTakenToday: Int
)

// --- UI COMPONENTS ---
    @Composable
    fun CalendarWidget(selectedDate: LocalDate, onDateSelected: (LocalDate) -> Unit) {
        Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Неделя", style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = { onDateSelected(selectedDate.plusDays(7L)) }) {
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next Week")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Добавьте сюда ваш код для отображения календаря (например, кнопки для дней недели)
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
            elevation = 2.dp
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                Text(text = medication.name, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "${medication.dosage} ${medication.timeSlot}")
            }
        }
    }

class HomeScreen : AppCompatActivity() {

    private val viewModel by viewModels<HomeScreenViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreenContent(viewModel, onDateSelected = { date ->
                viewModel.setSelectedDate(date)
            })
}
    }

    @Composable
    fun HomeScreenContent(viewModel: HomeScreenViewModel) {
        val selectedDate by viewModel.selectedDate.collectAsState(initial = LocalDate.now())
        val daySchedule by viewModel.daySchedule.collectAsState()

        Scaffold(
            topBar = { TopAppBar(title = { Text("Приложение для приема лекарств") }) },
            content = {
                Column(modifier = Modifier.padding(it)) {
                    CalendarWidget(selectedDate, onDateSelected = { date ->
                        viewModel.setSelectedDate(date)
                    })
                    MedicationListView(daySchedule?.medications ?: emptyList())
                }
            }
        )
    }

class HomeScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val appRepository by lazy { AppRepository(getApplication()) }

    val selectedDate = mutableStateOf(LocalDate.now())
    val daySchedule = derivedStateOf { appRepository.getDaySchedule(selectedDate.value) }

    fun setSelectedDate(date: LocalDate) {
        selectedDate.value = date
    }
}

class AppRepository(private val application: Application) {

    // Mock data access layer for demonstration purposes
    fun getDaySchedule(date: LocalDate): DaySchedule? {
        return null // Replace with actual implementation
    }
}
}
