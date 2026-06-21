package com.example.piluli.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import com.example.piluli.model.Medicine

@Dao
interface MedicineDao {
    @Query("SELECT * FROM medicine_table")
    fun getAllMedicines(): Flow<List<Medicine>>

    @Query("SELECT * FROM medicine_table WHERE id = :id")
    fun getMedicineById(id: String): Flow<Medicine>

    @Insert
    suspend fun insertMedicine(medicine: Medicine)

    @Update
    suspend fun updateMedicine(medicine: Medicine)

    @Delete
    suspend fun deleteMedicine(medicine: Medicine)
}
