package com.example.piluli.medications.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DoseSlotDao {
    @Query(
        "SELECT * FROM dose_slots " +
            "WHERE scheduledAtMillis BETWEEN :fromInclusive AND :toExclusive " +
            "ORDER BY scheduledAtMillis ASC"
    )
    fun observeForRange(fromInclusive: Long, toExclusive: Long): Flow<List<DoseSlotEntity>>

    @Query(
        "SELECT * FROM dose_slots " +
            "WHERE scheduledAtMillis BETWEEN :fromInclusive AND :toExclusive " +
            "ORDER BY scheduledAtMillis ASC"
    )
    suspend fun getForRange(fromInclusive: Long, toExclusive: Long): List<DoseSlotEntity>

    @Query(
        "SELECT * FROM dose_slots " +
            "WHERE originalScheduledAtMillis BETWEEN :fromInclusive AND :toExclusive " +
            "ORDER BY scheduledAtMillis ASC"
    )
    suspend fun getForDayByOriginal(fromInclusive: Long, toExclusive: Long): List<DoseSlotEntity>

    @Query("SELECT * FROM dose_slots WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): DoseSlotEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(entity: DoseSlotEntity): Long

    @Update
    suspend fun update(entity: DoseSlotEntity)

    @Query("UPDATE dose_slots SET scheduledAtMillis = :newTimeMillis WHERE id = :slotId AND takenAtMillis IS NULL")
    suspend fun postpone(slotId: Long, newTimeMillis: Long): Int

    @Query("UPDATE dose_slots SET takenAtMillis = :takenAtMillis WHERE id = :slotId AND takenAtMillis IS NULL")
    suspend fun markTaken(slotId: Long, takenAtMillis: Long): Int
}

