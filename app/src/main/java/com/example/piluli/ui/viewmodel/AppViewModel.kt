package com.example.piluli.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.piluli.data.DataStoreManager
import com.example.piluli.data.MedicineRepository
import com.example.piluli.data.AppDatabasePiluli
import com.example.piluli.model.DaySchedule
import com.example.piluli.reminder.ReminderScheduler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MedicineRepository(AppDatabasePiluli.getDatabase(application).medicineDao())

    // Состояние настроек сеанса (остается как было)
    val settings = DataStoreManager.getSettingsFlow(application)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Новое состояние для отслеживания выбранной даты
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate>
        get() = _selectedDate.asStateFlow()

    // Поток расписания дня на основе выбранной даты
    val daySchedule: StateFlow<DaySchedule> = combine(
        settings,
        _selectedDate
    ) { settings, date ->
        DaySchedule(date, repository.getMedicineForDate(date), settings["pillCount"] as? Int ?: 0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DaySchedule(LocalDate.now(), emptyList(), 0))

    // Функции для работы с данными
    fun saveSettings(pillCount: Int, currentPill: Int, hour: Int, minute: Int, isLoop: Boolean) {
        viewModelScope.launch {
            DataStoreManager.saveSettings(getApplication(), pillCount, currentPill, hour, minute, isLoop)
            ReminderScheduler.scheduleReminder(getApplication(), hour, minute)
        }
    }

    fun manualIncrement() {
        viewModelScope.launch {
            DataStoreManager.incrementPill(getApplication())
        }
    }

    // Функции для работы с датой
    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }
}

