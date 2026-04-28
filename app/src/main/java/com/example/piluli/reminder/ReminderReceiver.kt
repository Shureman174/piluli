package com.example.piluli.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.piluli.data.DataStoreManager
import com.example.piluli.data.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {
    
    companion object {
        const val ACTION_TAKE = "com.example.piluli.ACTION_TAKE"
        const val ACTION_POSTPONE = "com.example.piluli.ACTION_POSTPONE"
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_TAKE -> {
                CoroutineScope(Dispatchers.IO).launch {
                    DataStoreManager.incrementPill(context)
                    // Перепланировать на завтра
                    val prefs = context.dataStore.data.first()
                    val hour = prefs[DataStoreManager.REMINDER_HOUR] ?: 9
                    val minute = prefs[DataStoreManager.REMINDER_MINUTE] ?: 0
                    ReminderScheduler.scheduleReminder(context, hour, minute)
                }
            }
            ACTION_POSTPONE -> {
                // Упрощённо: просто покажем уведомление снова
                NotificationHelper.showNotification(context)
            }
            else -> {
                NotificationHelper.showNotification(context)
            }
        }
    }
}