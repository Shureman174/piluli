package com.example.piluli.medications.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.piluli.medications.data.AppDatabase
import com.example.piluli.medications.data.IntakeLogEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DoseUserActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val slotId = intent.getLongExtra(ReminderNotificationHelper.EXTRA_SLOT_ID, -1L)
        if (slotId <= 0) return

        val goAsync = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ReminderNotificationHelper.ensureChannels(context)
                val db = AppDatabase.getInstance(context)
                val slot = db.doseSlotDao().getById(slotId) ?: return@launch
                if (slot.takenAtMillis != null) {
                    ReminderNotificationHelper.cancelForSlot(context, slotId)
                    return@launch
                }

                when (action) {
                    ReminderNotificationHelper.ACTION_TAKE -> {
                        val takenAt = System.currentTimeMillis()
                        val updated = db.doseSlotDao().markTaken(slotId, takenAt) > 0
                        if (updated) {
                            db.intakeLogDao().insert(
                                IntakeLogEntity(
                                    medicationId = slot.medicationId,
                                    scheduledAtMillis = slot.scheduledAtMillis,
                                    takenAtMillis = takenAt,
                                    amount = 1,
                                    comment = null,
                                    isScheduled = true
                                )
                            )
                        }
                        DoseReminderScheduler.cancelAllForSlot(context, slotId)
                        ReminderNotificationHelper.cancelForSlot(context, slotId)
                    }

                    ReminderNotificationHelper.ACTION_POSTPONE -> {
                        val minutes = intent.getIntExtra(ReminderNotificationHelper.EXTRA_MINUTES, 15)
                        val newTime = slot.scheduledAtMillis + minutes * 60L * 1000L
                        val ok = db.doseSlotDao().postpone(slotId, newTime) > 0
                        if (ok) {
                            DoseReminderScheduler.cancelAllForSlot(context, slotId)
                            DoseReminderScheduler.scheduleDue(context, slotId, newTime)
                            val settings = ReminderSettingsStore.observe(context).first()
                            DoseReminderScheduler.scheduleSoonCheck(context, newTime - settings.soonMinutes * 60L * 1000L)
                            ReminderNotificationHelper.cancelForSlot(context, slotId)
                        }
                    }
                }
            } finally {
                goAsync.finish()
            }
        }
    }
}

