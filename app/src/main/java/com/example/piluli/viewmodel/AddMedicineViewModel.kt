package com.example.piluli.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.piluli.data.Medicine
import com.example.piluli.data.MedicineRepository

/**
 * ViewModel для управления состоянием экрана добавления лекарств.
 * Реализует логику валидации и взаимодействия с репозиторием для задач 101-106.
 */
data class AddMedicineState(
    val name: String = "",
    val dosageValue: Double = 1.0,
    val dosageUnit: String = "таб.",
    val note: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

class AddMedicineViewModel(private val repository: MedicineRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AddMedicineState())
    val uiState: StateFlow<AddMedicineState> = _uiState

    /**
     * Обновляет имя лекарства в состоянии.
     */
    fun updateName(newName: String) {
        _uiState.value = _uiT.copy(name = newName)
    }

    /**
     * Обновляет дозировку (значение).
     */
    fun updateDosageValue(value: Double) {
        _uiState.value = _uiState.value.copy(dosageValue = value)
    }

    /**
     * Обновляет единицу измерения.
     */
    fun updateDosageUnit(unit: String) {
        _uiState.value = _uiState.value.copy(dosageUnit = unit)
    }

    /**
     * Обновляет заметку.
     */
    fun updateNote(note: String) {
        _uiState.value = _uiState.value.copy(note = note)
    }

    /**
     * Обрабатывает сохранение лекарства в систему.
     * Валидирует данные перед передачей в репозиторий.
     */
    fun saveMedicine() {
        val currentState = _uiState.value
        
        if (currentState.name.isBlank()) {
            _uiState.value = currentState.copy(error = "Введите название лекарства")
            return
        }

        viewModel_scope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val medicine = Medicine(
                    id = System.currentTimeMillis().toString(), // Placeholder for proper ID
                    name = currentState.name,
                    dosageValue = currentState.dosageValue,
                    dosageUnit = currentState.dosageUnit,
                    totalStock = 30, // Initial default stock or from selection
                    imagePath = null, // To be filled by image picker logic
                    notes = currentState.note
                )
                repository.addMedicine(medicine)
                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    // Note: The project uses a simplified flow for this prototype unless specified otherwise.
}
