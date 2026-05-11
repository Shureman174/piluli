package com.example.piluli.medications.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "intake_logs",
    foreignKeys = [
        ForeignKey(
            entity = MedicationEntity::class,
            parentColumns = ["id"],
            childColumns = ["medicationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("medicationId"), Index("takenAtMillis")]
)
data class IntakeLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val medicationId: Long,
    /** Время слота (после переноса), если приём был по расписанию */
    val scheduledAtMillis: Long?,
    val takenAtMillis: Long,
    val amount: Int,
    val comment: String?,
    val isScheduled: Boolean,
)
