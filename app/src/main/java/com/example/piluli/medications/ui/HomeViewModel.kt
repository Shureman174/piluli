package com.example.piluli.medications.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.piluli.medications.data.DateTimes
import com.example.piluli.medications.data.DoseSlotEntity
import com.example.piluli.medications.data.MedicationEntity
import com.example.piluli.medications.di.ServiceLocator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch

data class HomeSlotUi(
    val slot: DoseSlotEntity,
    val medication: MedicationEntity?,
    val status: SlotStatus,
)

enum class SlotStatus { PLANNED, SOON, OVERDUE, TAKEN }

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = ServiceLocator.medicationRepository(application)

    private val selectedDayStart = kotlinx.coroutines.flow.MutableStateFlow(DateTimes.startOfDayMillis(System.currentTimeMillis()))

    val selectedDayStartMillis: StateFlow<Long> = selectedDayStart.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        selectedDayStart.value
    )

    private fun dayEndExclusive(dayStartMillis: Long): Long = dayStartMillis + 24L * 60L * 60L * 1000L

    private val medicationsFlow = repo.observeMedications()

    val slots: StateFlow<List<HomeSlotUi>> =
        selectedDayStart.combine(medicationsFlow) { dayStart, meds -> dayStart to meds }
            .flatMapLatestCompat { (dayStart, meds) ->
                val end = dayEndExclusive(dayStart)
                repo.observeDoseSlotsForDay(dayStart, end).map { slots ->
                    val medMap = meds.associateBy { it.id }
                    val now = System.currentTimeMillis()
                    slots.sortedBy { it.scheduledAtMillis }.map { s ->
                        val status = when {
                            s.takenAtMillis != null -> SlotStatus.TAKEN
                            now > s.scheduledAtMillis + 30L * 60L * 1000L -> SlotStatus.OVERDUE
                            now >= s.scheduledAtMillis - 60L * 60L * 1000L -> SlotStatus.SOON
                            else -> SlotStatus.PLANNED
                        }
                        HomeSlotUi(slot = s, medication = medMap[s.medicationId], status = status)
                    }
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun selectDay(dayStartMillis: Long) {
        selectedDayStart.value = dayStartMillis
        viewModelScope.launch {
            repo.ensureDoseSlotsForDay(dayStartMillis, dayEndExclusive(dayStartMillis))
        }
    }

    fun postpone(slotId: Long, currentScheduledAtMillis: Long, minutes: Int) {
        val newTime = currentScheduledAtMillis + minutes * 60L * 1000L
        viewModelScope.launch { repo.postponeSlot(slotId, newTime) }
    }

    fun take(slotUi: HomeSlotUi) {
        viewModelScope.launch {
            // takeSlot вернёт false если уже принят (двойной клик/гонка)
            repo.takeSlot(slotUi.slot, amount = 1)
        }
    }
}

/**
 * Чтобы не тащить ExperimentalCoroutinesApi в каждый файл из-за flatMapLatest.
 */
private fun <T, R> kotlinx.coroutines.flow.Flow<T>.flatMapLatestCompat(
    transform: suspend (T) -> kotlinx.coroutines.flow.Flow<R>
): kotlinx.coroutines.flow.Flow<R> = transformLatest { value ->
    emitAll(transform(value))
}

