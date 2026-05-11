package com.example.piluli.medications.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.piluli.medications.reminder.ReminderSettings
import com.example.piluli.medications.reminder.ReminderSettingsStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val ctx = application.applicationContext

    val settings: StateFlow<ReminderSettings> =
        ReminderSettingsStore.observe(ctx).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ReminderSettings())

    fun update(transform: (ReminderSettings) -> ReminderSettings) {
        viewModelScope.launch { ReminderSettingsStore.update(ctx, transform) }
    }
}

