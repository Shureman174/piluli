package com.example.piluli.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.piluli.model.Medicine
import com.example.piluli.data.MedicineRepository

class AddMedicineViewModel(private val repository: MedicineRepository) : ViewModel() {
    private val _medicine = MutableStateFlow(Medicine("", "", "", ""))
    val medicine: StateFlow<Medicine> = _medicine

    fun addMedicine() {
        viewModelScope.launch {
            repository.insert(_medicine.value)
        }
    }
}
