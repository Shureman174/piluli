package com.example.piluli.medications.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.piluli.medications.data.DoseSlotEntity
import kotlin.math.absoluteValue

object DoseReminderScheduler {
    private const val ACTION_SOON_CHECK = "com.example.piluli.ACTION_DOSE_SOON_CHECK"
    private const val ACTION_DUE = "com.example.piluli.ACTION_DOSE_DUE"
    private const val ACTION_REPEAT = "com.example.piluli.ACTION_DOSE_REPEAT"

    private const val EXTRA_SLOT_ID = "slot_id"
    private const val EXTRA_ATTEMPT = "attempt"

    fun scheduleSoonCheck(context: Context, triggerAtMillis: Long) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi = PendingIntent.getBroadcast(
            context,
            10_001,
            Intent(context, DoseReminderReceiver::class.java).setAction(ACTION_SOON_CHECK),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi)
    }

    fun scheduleDue(context: Context, slotId: Long, triggerAtMillis: Long) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi = PendingIntent.getBroadcast(
            context,
            requestCode(slotId, 1),
            Intent(context, DoseReminderReceiver::class.java)
                .setAction(ACTION_DUE)
                .putExtra(EXTRA_SLOT_ID, slotId)
                .putExtra(EXTRA_ATTEMPT, 0),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi)
    }

    fun scheduleRepeat(context: Context, slotId: Long, triggerAtMillis: Long, attempt: Int) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi = PendingIntent.getBroadcast(
            context,
            requestCode(slotId, 2),
            Intent(context, DoseReminderReceiver::class.java)
                .setAction(ACTION_REPEAT)
                .putExtra(EXTRA_SLOT_ID, slotId)
                .putExtra(EXTRA_ATTEMPT, attempt),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi)
    }

    fun cancelAllForSlot(context: Context, slotId: Long) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        listOf(1, 2).forEach { kind ->
            val rc = requestCode(slotId, kind)
            val pi = PendingIntent.getBroadcast(
                context,
                rc,
                Intent(context, DoseReminderReceiver::class.java),
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pi != null) am.cancel(pi)
        }
    }

    fun receiverActionSoonCheck() = ACTION_SOON_CHECK
    fun receiverActionDue() = ACTION_DUE
    fun receiverActionRepeat() = ACTION_REPEAT
    fun extraSlotId() = EXTRA_SLOT_ID
    fun extraAttempt() = EXTRA_ATTEMPT

    private fun requestCode(slotId: Long, kind: Int): Int {
        // kind: 1 due, 2 repeat
        val base = (slotId xor (slotId ushr 32)).toInt().absoluteValue
        return base + kind * 100_000
    }
}

