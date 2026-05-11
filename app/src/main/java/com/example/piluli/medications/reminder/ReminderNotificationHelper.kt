package com.example.piluli.medications.reminder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.piluli.R
import com.example.piluli.medications.data.DoseSlotEntity
import com.example.piluli.medications.data.MedicationEntity

object ReminderNotificationHelper {
    private const val CHANNEL_SOON = "dose_soon"
    private const val CHANNEL_DUE = "dose_due"
    private const val CHANNEL_MISSED = "dose_missed"

    const val ACTION_TAKE = "com.example.piluli.ACTION_DOSE_TAKE"
    const val ACTION_POSTPONE = "com.example.piluli.ACTION_DOSE_POSTPONE"
    const val EXTRA_SLOT_ID = "slot_id"
    const val EXTRA_MINUTES = "minutes"

    fun ensureChannels(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (nm.getNotificationChannel(CHANNEL_SOON) == null) {
            nm.createNotificationChannel(
                NotificationChannel(CHANNEL_SOON, "Скоро приём", NotificationManager.IMPORTANCE_HIGH).apply {
                    description = "Уведомления за некоторое время до приёма"
                }
            )
        }
        if (nm.getNotificationChannel(CHANNEL_DUE) == null) {
            nm.createNotificationChannel(
                NotificationChannel(CHANNEL_DUE, "Время приёма", NotificationManager.IMPORTANCE_HIGH).apply {
                    description = "Уведомления в момент приёма (как будильник)"
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }
            )
        }
        if (nm.getNotificationChannel(CHANNEL_MISSED) == null) {
            nm.createNotificationChannel(
                NotificationChannel(CHANNEL_MISSED, "Пропущенные приёмы", NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = "Уведомления о пропущенном приёме"
                }
            )
        }
    }

    fun showSoonSummary(
        context: Context,
        slots: List<DoseSlotEntity>,
        meds: Map<Long, MedicationEntity>,
        settings: ReminderSettings,
    ) {
        val names = slots.mapNotNull { meds[it.medicationId]?.name }.distinct()
        val title = "Скоро приём"
        val text = if (names.isEmpty()) "Скоро приём препаратов" else "Скоро: " + names.joinToString(limit = 4) + if (names.size > 4) "…" else ""

        val builder = NotificationCompat.Builder(context, CHANNEL_SOON)
            .setSmallIcon(R.drawable.ic_pill)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText("Скоро приём: " + names.joinToString()))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        applySoundVibration(context, builder, isDue = false, settings = settings)

        NotificationManagerCompat.from(context).notify(2001, builder.build())
    }

    fun showDueForSlot(context: Context, slot: DoseSlotEntity, med: MedicationEntity, settings: ReminderSettings) {
        val fullScreenIntent = Intent(context, FullScreenDoseActivity::class.java)
            .putExtra(EXTRA_SLOT_ID, slot.id)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val fullScreenPi = PendingIntent.getActivity(
            context,
            (slot.id.toInt() + 3_000_000),
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val takePi = PendingIntent.getBroadcast(
            context,
            (slot.id.toInt() + 4_000_000),
            Intent(context, DoseUserActionReceiver::class.java).setAction(ACTION_TAKE).putExtra(EXTRA_SLOT_ID, slot.id),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val postpone15Pi = postponePi(context, slot.id, 15)
        val postpone30Pi = postponePi(context, slot.id, 30)
        val postpone60Pi = postponePi(context, slot.id, 60)

        val builder = NotificationCompat.Builder(context, CHANNEL_DUE)
            .setSmallIcon(R.drawable.ic_pill)
            .setContentTitle("Время приёма")
            .setContentText(med.name)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(false)
            .setAutoCancel(true)
            .setFullScreenIntent(fullScreenPi, true)
            .addAction(0, "Принять", takePi)
            .addAction(0, "+15", postpone15Pi)
            .addAction(0, "+30", postpone30Pi)
            .addAction(0, "+60", postpone60Pi)

        applySoundVibration(context, builder, isDue = true, settings = settings)

        NotificationManagerCompat.from(context).notify(dueNotificationId(slot.id), builder.build())
    }

    fun showMissedForSlot(context: Context, slot: DoseSlotEntity, med: MedicationEntity) {
        val builder = NotificationCompat.Builder(context, CHANNEL_MISSED)
            .setSmallIcon(R.drawable.ic_pill)
            .setContentTitle("Пропущен приём")
            .setContentText(med.name)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)
        NotificationManagerCompat.from(context).notify(missedNotificationId(slot.id), builder.build())
    }

    fun cancelForSlot(context: Context, slotId: Long) {
        NotificationManagerCompat.from(context).cancel(dueNotificationId(slotId))
        NotificationManagerCompat.from(context).cancel(missedNotificationId(slotId))
    }

    private fun postponePi(context: Context, slotId: Long, minutes: Int): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            (slotId.toInt() + 5_000_000 + minutes),
            Intent(context, DoseUserActionReceiver::class.java)
                .setAction(ACTION_POSTPONE)
                .putExtra(EXTRA_SLOT_ID, slotId)
                .putExtra(EXTRA_MINUTES, minutes),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun applySoundVibration(
        context: Context,
        builder: NotificationCompat.Builder,
        isDue: Boolean,
        settings: ReminderSettings
    ) {
        val soundEnabled = if (isDue) settings.dueSoundEnabled else settings.soonSoundEnabled
        val vibEnabled = if (isDue) settings.dueVibrationEnabled else settings.soonVibrationEnabled

        if (vibEnabled) {
            builder.setVibrate(longArrayOf(0, 250, 250, 250))
        } else {
            builder.setVibrate(null)
        }

        if (soundEnabled) {
            val uri = if (isDue) settings.dueRingtoneUri?.let(Uri::parse) else null
            builder.setSound(uri)
        } else {
            builder.setSound(null)
        }
    }

    private fun dueNotificationId(slotId: Long): Int = 30_000 + (slotId % 1_000_000).toInt()
    private fun missedNotificationId(slotId: Long): Int = 40_000 + (slotId % 1_000_000).toInt()
}

