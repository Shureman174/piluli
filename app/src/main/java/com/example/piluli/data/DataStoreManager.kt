package com.example.piluli.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Расширение для Context — должно быть ОДНО в проекте
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "pill_settings")

object DataStoreManager {
    // ✅ public свойства, чтобы использовать из других файлов
    val PILL_COUNT = intPreferencesKey("pill_count")
    val CURRENT_PILL = intPreferencesKey("current_pill")
    val REMINDER_HOUR = intPreferencesKey("reminder_hour")
    val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
    val IS_LOOP = booleanPreferencesKey("is_loop")
    val IS_FIRST_RUN = booleanPreferencesKey("is_first_run")

    fun getSettingsFlow(context: Context): Flow<Map<String, Any>> =
        context.dataStore.data.map { prefs ->
            mapOf(
                "pillCount" to (prefs[PILL_COUNT] ?: 21),
                "currentPill" to (prefs[CURRENT_PILL] ?: 1),
                "reminderHour" to (prefs[REMINDER_HOUR] ?: 9),
                "reminderMinute" to (prefs[REMINDER_MINUTE] ?: 0),
                "isLoop" to (prefs[IS_LOOP] ?: true),
                "isFirstRun" to (prefs[IS_FIRST_RUN] ?: true)
            )
        }

    suspend fun saveSettings(
        context: Context,
        pillCount: Int,
        currentPill: Int,
        hour: Int,
        minute: Int,
        isLoop: Boolean
    ) {
        context.dataStore.edit { prefs ->
            prefs[PILL_COUNT] = pillCount
            prefs[CURRENT_PILL] = currentPill
            prefs[REMINDER_HOUR] = hour
            prefs[REMINDER_MINUTE] = minute
            prefs[IS_LOOP] = isLoop
            prefs[IS_FIRST_RUN] = false
        }
    }

    suspend fun incrementPill(context: Context) {
        context.dataStore.edit { prefs ->
            val current = prefs[CURRENT_PILL] ?: 1
            val count = prefs[PILL_COUNT] ?: 21
            val loop = prefs[IS_LOOP] ?: true
            prefs[CURRENT_PILL] = if (current >= count) {
                if (loop) 1 else count
            } else {
                current + 1
            }
        }
    }
}