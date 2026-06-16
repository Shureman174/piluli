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
    val DOSES_PER_DAY = intPreferencesKey("doses_per_day")
    val IS_LOOP = booleanPreferencesKey("is_loop")
    val IS_FIRST_RUN = booleanPreferencesKey("is_first_run")
    val LAST_TAKEN_AT = longPreferencesKey("last_taken_at")

    fun getSettingsFlow(context: Context): Flow<Map<String, Any>> =
        context.dataStore.data.map { prefs ->
            mapOf(
                "pillCount" to (prefs[PILL_COUNT] ?: 21),
                "currentPill" to (prefs[CURRENT_PILL] ?: 1),
                "reminderHour" to (prefs[REMINDER_HOUR] ?: 9),
                "reminderMinute" to (prefs[REMINDER_MINUTE] ?: 0),
                "dosesPerDay" to (prefs[DOSES_PER_DAY] ?: 1),
                "isLoop" to (prefs[IS_LOOP] ?: true),
                "isFirstRun" to (prefs[IS_FIRST_RUN] ?: true),
                "lastTakenAt" to (prefs[LAST_TAKEN_AT] ?: 0L)
            )
        }

    suspend fun saveSettings(
        context: Context,
        pillCount: Int,
        currentPill: Int,
        hour: Int,
        minute: Int,
        dosesPerDay: Int,
        isLoop: Boolean
    ) {
        context.dataStore.edit { prefs ->
            prefs[PILL_COUNT] = pillCount
            prefs[CURRENT_PILL] = currentPill
            prefs[REMINDER_HOUR] = hour
            prefs[REMINDER_MINUTE] = minute
            prefs[DOSES_PER_DAY] = dosesPerDay
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
            prefs[LAST_TAKEN_AT] = System.currentTimeMillis()
        }
    }
}
