package com.example.piluli.medications.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medications")
data class MedicationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val photoUri: String?,
    val createdAtMillis: Long,
    val startAtMillis: Long?,
    val intakesPerDay: Int,
    val totalCourseAmount: Int?,
    val intakeTimes: List<String>,
)
