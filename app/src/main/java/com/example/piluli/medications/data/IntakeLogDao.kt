package com.example.piluli.medications.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IntakeLogDao {
    @Query("SELECT * FROM intake_logs ORDER BY takenAtMillis DESC")
    fun observeAll(): Flow<List<IntakeLogEntity>>

    @Query("SELECT * FROM intake_logs WHERE medicationId = :medicationId ORDER BY takenAtMillis DESC")
    fun observeByMedication(medicationId: Long): Flow<List<IntakeLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: IntakeLogEntity): Long

    @Query("DELETE FROM intake_logs WHERE id = :id")
    suspend fun deleteById(id: Long)
}
