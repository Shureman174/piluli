package com.example.piluli.medications.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.piluli.medications.data.AppDatabase
import com.example.piluli.medications.data.DoseSlotEntity
import com.example.piluli.medications.data.MedicationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DoseReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val goAsync = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ReminderNotificationHelper.ensureChannels(context)
                val db = AppDatabase.getInstance(context)
                val settings = ReminderSettingsStore.observe(context).first()

                when (action) {
                    DoseReminderScheduler.receiverActionSoonCheck() -> {
                        val now = System.currentTimeMillis()
                        val windowEnd = now + settings.soonMinutes * 60L * 1000L
                        val slots = db.doseSlotDao().observeForRange(now, windowEnd).first()
                            .filter { it.takenAtMillis == null }
                        if (slots.isNotEmpty()) {
                            val meds = db.medicationDao().getAllOnce().associateBy { it.id }
                            ReminderNotificationHelper.showSoonSummary(context, slots, meds, settings)
                        }
                        // перепланируем следующий soon-check на ближайший слот, если есть
                        val nextSoon = db.doseSlotDao().observeForRange(now, now + 7L * 24 * 60 * 60 * 1000).first()
                            .filter { it.takenAtMillis == null }
                            .minByOrNull { it.scheduledAtMillis }?.scheduledAtMillis
                        if (nextSoon != null) {
                            DoseReminderScheduler.scheduleSoonCheck(
                                context,
                                nextSoon - settings.soonMinutes * 60L * 1000L
                            )
                        }
                    }

                    DoseReminderScheduler.receiverActionDue(),
                    DoseReminderScheduler.receiverActionRepeat() -> {
                        val slotId = intent.getLongExtra(DoseReminderScheduler.extraSlotId(), -1L)
                        if (slotId <= 0) return@launch
                        val attempt = intent.getIntExtra(DoseReminderScheduler.extraAttempt(), 0)

                        val slot = db.doseSlotDao().getById(slotId)
                        if (slot == null || slot.takenAtMillis != null) return@launch

                        val med = db.medicationDao().getById(slot.medicationId) ?: return@launch
                        ReminderNotificationHelper.showDueForSlot(context, slot, med, settings)

                        // Повторы: показываем 1 минуту, потом пауза R и повтор до N
                        if (settings.repeatCount > 0 && attempt < settings.repeatCount) {
                            val repeatAt = System.currentTimeMillis() + 60_000L + settings.repeatIntervalMinutes * 60L * 1000L
                            DoseReminderScheduler.scheduleRepeat(context, slotId, repeatAt, attempt + 1)
                        } else if (attempt >= settings.repeatCount) {
                            ReminderNotificationHelper.showMissedForSlot(context, slot, med)
                        }
                    }
                }
            } finally {
                goAsync.finish()
            }
        }
    }
}

