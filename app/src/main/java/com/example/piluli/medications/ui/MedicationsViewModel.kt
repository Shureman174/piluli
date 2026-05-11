package com.example.piluli.medications.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.piluli.medications.data.MedicationEntity
import com.example.piluli.medications.di.ServiceLocator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MedicationsViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = ServiceLocator.medicationRepository(application)

    val medications: StateFlow<List<MedicationEntity>> =
        repo.observeMedications().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun deleteMedication(id: Long) {
        viewModelScope.launch { repo.deleteMedication(id) }
    }
}

