package com.example.piluli.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
class MedicineRepository(private val medicineDao: MedicineDao) {
    val medicines: Flow<List<Medicine>> = medicineDao.getAll().map { entities ->
        entities.map { it.toDomain() }
    }

    /**
     * Adds a new medicine to the list.
     */
    suspend fun addMedicine(medicine: Medicine) {
        medicineDao.insert(medicine.toEntity())
    }

    /**
     * Updates an existing medicine (e.g., for editing - Task 102).
     */
    suspend fun updateMedicine(id: String, updatedMedicine: Medicine) {
        medicineDao.insert(updatedMedicine.toEntity())
    }

    /**
     * Removes a medicine from the list.
     */
    suspend fun removeMedicine(id: String) {
        val entity = medicineDao.getById(id)
        if (entity != null) {
            medicineDao.delete(entity)
        }
    }

    /**
     * Decrements stock after consumption (Requirement 125).
     */
    suspend fun consumeMedicine(medicineId: String) {
        val entity = medicineDao.getById(medicineId)
        if (entity != null && entity.totalStock > 0) {
            medicineDao.insert(entity.copy(totalStock = entity.totalStock - 1))
        }
    }

    suspend fun getMedicineById(id: String): Medicine? {
        return medicineDao.getById(id)?.toDomain()
    }
}

fun MedicineEntity.toDomain() = Medicine(
    id = id,
    name = name,
    dosageValue = dosageValue,
    dosageUnit = dosageUnit,
    totalStock = totalStock,
    imagePath = imagePath,
    notes = notes
)

fun Medicine.toEntity() = MedicineEntity(
    id = id,
    name = name,
    dosageValue = dosageValue,
    dosageUnit = dosageUnit,
    totalStock = totalStock,
    imagePath = imagePath,
    notes = notes
)
