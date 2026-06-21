package com.example.piluli.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {
    @Query("SELECT * FROM medications")
    fun getAllMedicines(): Flow<List<MedicineEntity>>

    @Query("SELECT * FROM medications WHERE id = :id")
    fun getMedicineById(id: String): Flow<MedicineEntity>

    @Insert
    suspend fun insertMedicine(medicine: MedicineEntity)

    @Update
    suspend fun updateMedicine(medicine: MedicineEntity)

    @Delete
    suspend fun deleteMedicine(medicine: MedicineEntity)
}
