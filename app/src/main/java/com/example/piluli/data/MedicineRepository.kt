package com.example.piluli.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.piluli.model.Medicine
import com.example.piluli.model.Medication
import java.time.LocalDate

class MedicineRepository(private val medicineDao: MedicineDao) {
    val allMedicines: Flow<List<Medicine>> = medicineDao.getAllMedicines().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun insert(medicine: Medicine) = medicineDao.insertMedicine(medicine.toEntity())
    suspend fun update(medicine: Medicine) = medicineDao.updateMedicine(medicine.toEntity())
    suspend fun delete(medicine: Medicine) = medicineDao.deleteMedicine(medicine.toEntity())

    fun getMedicineById(id: String): Flow<Medicine> = medicineDao.getMedicineById(id).map { it.toDomain() }

    fun getMedicineForDate(date: LocalDate): List<Medication> {
        // TODO: Implement actual filtering by date. 
        // For now returning empty list to fix compilation.
        return emptyList()
    }
}
