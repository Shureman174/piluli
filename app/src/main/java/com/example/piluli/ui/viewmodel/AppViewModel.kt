package com.example.piluli.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.piluli.data.DataStoreManager
import com.example.piluli.reminder.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {
    
    val settings = DataStoreManager.getSettingsFlow(application)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun saveSettings(
        pillCount: Int,
        currentPill: Int,
        hour: Int,
        minute: Int,
        dosesPerDay: Int,
        isLoop: Boolean
    ) {
        viewModelScope.launch {
            DataStoreManager.saveSettings(
                getApplication(),
                pillCount,
                currentPill,
                hour,
                minute,
                dosesPerDay,
                isLoop
            )
            ReminderScheduler.scheduleReminder(getApplication(), hour, minute)
        }
    }

    fun manualIncrement() {
        viewModelScope.launch {
            DataStoreManager.incrementPill(getApplication())
        }
    }

    fun postponeBy15Minutes() {
        ReminderScheduler.scheduleReminderInMinutes(getApplication(), 15)
    }
}
