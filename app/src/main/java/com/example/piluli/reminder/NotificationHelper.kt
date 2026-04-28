package com.example.piluli.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.piluli.MainActivity  // ← ПРАВИЛЬНЫЙ импорт: без .ui

object NotificationHelper {
    const val CHANNEL_ID = "pill_reminder_channel"
    const val NOTIFICATION_ID = 1

    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Напоминания о таблетках",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Напоминания о приёме противозачаточных таблеток"
        }
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)
    }

    fun showNotification(context: Context) {
        createChannel(context)
        
        val contentIntent = PendingIntent.getActivity(
            context, 0, Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_my_calendar)
            .setContentTitle("Время принять таблетку!")
            .setContentText("Откройте приложение, чтобы отметить приём.")
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .build()

        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify(NOTIFICATION_ID, notification)
    }
}