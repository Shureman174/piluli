package com.example.piluli.medications.data

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import com.example.piluli.medications.reminder.DoseReminderScheduler
import com.example.piluli.medications.reminder.ReminderSettingsStore

class MedicationRepository(
    private val context: Context,
    private val medicationDao: MedicationDao,
    private val intakeLogDao: IntakeLogDao,
    private val doseSlotDao: DoseSlotDao,
) {
    fun observeMedications(): Flow<List<MedicationEntity>> = medicationDao.observeAll()

    fun observeMedication(id: Long): Flow<MedicationEntity?> = medicationDao.observeById(id)

    suspend fun getMedication(id: Long): MedicationEntity? = medicationDao.getById(id)

    fun observeIntakeLogs(): Flow<List<IntakeLogEntity>> = intakeLogDao.observeAll()

    fun observeIntakeLogsForMedication(medicationId: Long): Flow<List<IntakeLogEntity>> =
        intakeLogDao.observeByMedication(medicationId)

    suspend fun upsertMedication(entity: MedicationEntity): Long = medicationDao.upsert(entity)

    suspend fun deleteMedication(id: Long) = medicationDao.deleteById(id)

    suspend fun logIntake(
        medicationId: Long,
        amount: Int,
        comment: String?,
        isScheduled: Boolean,
        scheduledAtMillis: Long? = null,
        takenAtMillis: Long = System.currentTimeMillis(),
    ): Long {
        return intakeLogDao.insert(
            IntakeLogEntity(
                medicationId = medicationId,
                scheduledAtMillis = scheduledAtMillis,
                takenAtMillis = takenAtMillis,
                amount = amount,
                comment = comment?.takeIf { it.isNotBlank() },
                isScheduled = isScheduled
            )
        )
    }

    fun observeDoseSlotsForDay(dayStartMillis: Long, dayEndExclusiveMillis: Long): Flow<List<DoseSlotEntity>> {
        return doseSlotDao.observeForRange(dayStartMillis, dayEndExclusiveMillis)
    }

    suspend fun ensureDoseSlotsForDay(dayStartMillis: Long, dayEndExclusiveMillis: Long) {
        val meds = medicationDao.getAllOnce()
        for (med in meds) {
            val times = med.intakeTimes
            for (t in times) {
                val scheduled = DateTimes.combineDayWithTime(dayStartMillis, t) ?: continue
                if (scheduled < dayStartMillis || scheduled >= dayEndExclusiveMillis) continue
                doseSlotDao.insertIgnore(
                    DoseSlotEntity(
                        medicationId = med.id,
                        originalScheduledAtMillis = scheduled,
                        scheduledAtMillis = scheduled,
                        takenAtMillis = null
                    )
                )
            }
        }

        // Планируем будильники на все слоты дня + ближайшее "soon"
        val settings = ReminderSettingsStore.observe(context).first()
        val daySlots = doseSlotDao.getForRange(dayStartMillis, dayEndExclusiveMillis).filter { it.takenAtMillis == null }
        daySlots.forEach { slot ->
            DoseReminderScheduler.scheduleDue(context, slot.id, slot.scheduledAtMillis)
        }
        val earliest = daySlots.minByOrNull { it.scheduledAtMillis }?.scheduledAtMillis
        if (earliest != null) {
            DoseReminderScheduler.scheduleSoonCheck(context, earliest - settings.soonMinutes * 60L * 1000L)
        }
    }

    suspend fun postponeSlot(slotId: Long, newScheduledAtMillis: Long): Boolean {
        val ok = doseSlotDao.postpone(slotId, newScheduledAtMillis) > 0
        if (ok) {
            DoseReminderScheduler.cancelAllForSlot(context, slotId)
            DoseReminderScheduler.scheduleDue(context, slotId, newScheduledAtMillis)
            val settings = ReminderSettingsStore.observe(context).first()
            DoseReminderScheduler.scheduleSoonCheck(context, newScheduledAtMillis - settings.soonMinutes * 60L * 1000L)
        }
        return ok
    }

    suspend fun takeSlot(slot: DoseSlotEntity, amount: Int = 1, comment: String? = null): Boolean {
        val takenAt = System.currentTimeMillis()
        val updated = doseSlotDao.markTaken(slot.id, takenAt) > 0
        if (!updated) return false
        DoseReminderScheduler.cancelAllForSlot(context, slot.id)
        logIntake(
            medicationId = slot.medicationId,
            amount = amount,
            comment = comment,
            isScheduled = true,
            scheduledAtMillis = slot.scheduledAtMillis,
            takenAtMillis = takenAt
        )
        return true
    }

    /**
     * Копируем выбранное изображение в app-specific storage,
     * чтобы URI не "протух" при доступе к чужому DocumentProvider.
     */
    suspend fun importPhotoToAppStorage(sourceUri: Uri): Uri = withContext(Dispatchers.IO) {
        val photosDir = File(context.filesDir, "med_photos").apply { mkdirs() }
        val target = File(photosDir, "${UUID.randomUUID()}.jpg")

        context.contentResolver.openInputStream(sourceUri).use { input ->
            requireNotNull(input) { "Can't open input stream for $sourceUri" }
            target.outputStream().use { output -> input.copyTo(output) }
        }

        Uri.fromFile(target)
    }
}

