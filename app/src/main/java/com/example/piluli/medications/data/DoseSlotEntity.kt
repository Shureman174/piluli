package com.example.piluli.medications.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Запланированный приём (слот) на конкретную дату/время.
 * Нужен для:
 * - переноса (отложить) с сохранением нового времени
 * - жёсткой защиты от повторного "Принять" для того же слота
 * - статусов на главном экране
 */
@Entity(
    tableName = "dose_slots",
    foreignKeys = [
        ForeignKey(
            entity = MedicationEntity::class,
            parentColumns = ["id"],
            childColumns = ["medicationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("medicationId"),
        Index(value = ["medicationId", "originalScheduledAtMillis"], unique = true),
        Index("scheduledAtMillis"),
    ]
)
data class DoseSlotEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val medicationId: Long,
    /** Исходное расписание (не меняется) */
    val originalScheduledAtMillis: Long,
    /** Текущее время приёма (с учётом "отложить") */
    val scheduledAtMillis: Long,
    /** Когда пользователь отметил приём (null если не принят) */
    val takenAtMillis: Long?,
)

