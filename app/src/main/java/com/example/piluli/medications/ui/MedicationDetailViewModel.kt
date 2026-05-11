package com.example.piluli.medications.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.piluli.medications.data.IntakeLogEntity
import com.example.piluli.medications.data.MedicationEntity
import com.example.piluli.medications.di.ServiceLocator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MedicationDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = ServiceLocator.medicationRepository(application)

    private val medicationIdFlow = MutableStateFlow<Long?>(null)

    val medication: StateFlow<MedicationEntity?> =
        medicationIdFlow.flatMapLatest { id -> if (id == null) flowOf(null) else repo.observeMedication(id) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val intakeLogs: StateFlow<List<IntakeLogEntity>> =
        medicationIdFlow.flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else repo.observeIntakeLogsForMedication(id)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun load(medicationId: Long) {
        medicationIdFlow.value = medicationId
    }

    fun takeNow(amount: Int = 1, comment: String? = null) {
        val id = medicationIdFlow.value ?: return
        viewModelScope.launch { repo.logIntake(medicationId = id, amount = amount, comment = comment, isScheduled = false) }
    }
}

