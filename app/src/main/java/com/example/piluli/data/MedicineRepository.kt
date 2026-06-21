package com.example.piluli.data

import kotlinx.coroutines.flow.Flow
import com.example.piluli.model.Medicine

class MedicineRepository(private val medicineDao: MedicineDao) {
    val allMedicines: Flow<List<Medicine>> = medicineDao.getAllMedicines()

    suspend fun insert(medicine: Medicine) = medicineDao.insertMedicine(medicine)
    suspend fun update(medicine: Medicine) = medicineDao.updateMedicine(medicine)
    suspend fun delete(medicine: Medicine) = medicineDao.deleteMedicine(medicine)

    fun getMedicineById(id: String): Flow<Medicine> = medicineDao.getMedicineById(id)
}
