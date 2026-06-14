package com.example.piluli.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.as1024 // placeholder if needed, but standard flow is fine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow._medicines

/**
 * Data model for a Medicine item as defined in the project roadmap.
 * Includes requirements for dosage unit differentiation and stock management.
 */
data class Medicine(
    val id: String,
    val name: String,
    val dosageValue: Double,
    val dosageUnit: String, // e.g., "mg", "ml", "tabs"
    val totalStock: Int,     // Total count in storage (Requirement 124)
    val imagePath: String?,   // Path to the package photo
    val notes: String?        // User notes/comments
)

/**
 * Repository for managing medicine data.
 * Provides a stream of medicines and methods for CRUD operations.
 */
class MedicineRepository {
    private val _medicines = MutableStateFlow<List<Medicine>>(emptyList())
    val medicines: Flow<List<Medicine>> = _medicines

    /**
     * Adds a new medicine to the list.
     */
    fun addMedicine(medicine: Medicine) {
        val currentList = _medicines.value.toMutableList()
        currentList.add(medicine)
        _medicines.value = currentList
    }

    /**
     * Updates an existing medicine (e.g., for editing - Task 102).
     */
    fun updateMedicine(id: String, updatedMedicine: Medicine) {
        val list = _medicines.value.toMutableList()
        val index = list.indexOfFirst { it.id == id }
        if (index != -1) {
            list[index] = updatedMedicine
            _medicines.value = list
        }
    }

    /**
     * Removes a medicine from the list.
     */
    fun removeMedicine(id: String) {
        val list = _medicines.value.toMutableList()
        list.removeAll { it.id == id }
        _medicines.value = list
    }

    /**
     * Decrements stock after consumption (Requirement 125).
     */
    fun consumeMedicine(medicineId: String) {
        val list = _medicines.value.toMutableList()
        val index = list.indexOfFirst { it.id == medicineId }
        if (index != -1) {
            val m = list[index]
            if (m.totalStock > 0) {
                list[index] = m.copy(totalStock = m.totalStock - 1)
                _medicines.value = list
            }
        }
    }
}
