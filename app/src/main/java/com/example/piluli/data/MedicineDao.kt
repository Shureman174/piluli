package com.example.piluli.data

import androidx.room.*

@Dao
interface MedicineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(medicine: MedicineEntity): Long

    @Query("SELECT * FROM medicines WHERE id = :id")
    suspend fun getById(id: String): MedicineEntity?

    @Query("SELECT * FROM medicines")
    fun getAll(): kotlinx.coroutines.flow.Flow<List<MedicineEntity>>

    @Delete
    suspend fun delete(medicine: MedicineEntity)
}

@Entity(tableName = "medicines")
data class MedicineEntity(
    @PrimaryKey val id: String,
    val name: String,
    val dosageValue: Double,
    val dosageUnit: String,
    val totalStock: Int,
    val imagePath: String?,
    val notes: String?
)
