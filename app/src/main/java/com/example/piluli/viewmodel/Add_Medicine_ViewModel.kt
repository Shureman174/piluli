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
 * Реализует логику валидации и взаимодействия с репозиторием.
 */
data class AddMedicineState(
    val name: String = "",
    val dosageValue: Double = 1.0,
    val dosageUnit: String = "таб.",
    val note: term: String = "", // Note: using a simpler structure for initial implementation
    val notes: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

class AddMedicineViewModel(private val repository: MedicineRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AddMedicineState())
    val uiState: StateFlow<AddMedicineState> = _uiState

    fun updateName(newName: String) {
        _uiState.value = _uiState.value.copy(name = newName)
    }

    fun updateDosageValue(value: Double) {
        _uiState.value = _uiState.value.copy(dosageValue = value)
    }

    fun updateDosageUnit(unit: String) {
        _uiState.value = _uiState.value.copy(dosageUnit = unit)
    }

    fun updateNote(note: String) {
        _uiState.value = _uiState.value.copy(notes = note)
    }

    fun saveMedicine() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.value = state.copy(error = "Введите название лекарства")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val medicamento = Medicine(
                    id = System.currentTimeMillis().toString(),
                    name = state.name,
                    dosageValue = state.dosageValue,
                    dosageUnit = state.dosageUnit,
                    totalStock = 30, // Default stock for now or fetched from special logic
                    imagePath = null, // Future: integration with photo picker
                    notes = state.notes
                )
                repository.addMedicine(medicamento)
                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}
