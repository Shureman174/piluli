package com.example.piluli.medications.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.piluli.medications.data.IntakeLogEntity
import com.example.piluli.medications.di.ServiceLocator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class JournalViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = ServiceLocator.medicationRepository(application)

    val intakeLogs: StateFlow<List<IntakeLogEntity>> =
        repo.observeIntakeLogs().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

