package com.example.piluli.medications.reminder

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.piluli.data.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class ReminderSettings(
    // Soon (summary)
    val soonMinutes: Int = 30,
    val soonSoundEnabled: Boolean = true,
    val soonVibrationEnabled: Boolean = true,

    // Due (per-slot, alarm-like)
    val dueSoundEnabled: Boolean = true,
    val dueVibrationEnabled: Boolean = true,
    val dueRingtoneUri: String? = null, // null => system default
    val repeatIntervalMinutes: Int = 5,
    val repeatCount: Int = 3,
)

object ReminderSettingsStore {
    private val SOON_MINUTES = intPreferencesKey("soon_minutes")
    private val SOON_SOUND = booleanPreferencesKey("soon_sound_enabled")
    private val SOON_VIBRATION = booleanPreferencesKey("soon_vibration_enabled")

    private val DUE_SOUND = booleanPreferencesKey("due_sound_enabled")
    private val DUE_VIBRATION = booleanPreferencesKey("due_vibration_enabled")
    private val DUE_RINGTONE = stringPreferencesKey("due_ringtone_uri")

    private val REPEAT_INTERVAL = intPreferencesKey("due_repeat_interval_minutes")
    private val REPEAT_COUNT = intPreferencesKey("due_repeat_count")

    fun observe(context: Context): Flow<ReminderSettings> {
        return context.dataStore.data.map { prefs ->
            ReminderSettings(
                soonMinutes = prefs[SOON_MINUTES] ?: 30,
                soonSoundEnabled = prefs[SOON_SOUND] ?: true,
                soonVibrationEnabled = prefs[SOON_VIBRATION] ?: true,
                dueSoundEnabled = prefs[DUE_SOUND] ?: true,
                dueVibrationEnabled = prefs[DUE_VIBRATION] ?: true,
                dueRingtoneUri = prefs[DUE_RINGTONE],
                repeatIntervalMinutes = prefs[REPEAT_INTERVAL] ?: 5,
                repeatCount = prefs[REPEAT_COUNT] ?: 3,
            )
        }
    }

    suspend fun update(context: Context, transform: (ReminderSettings) -> ReminderSettings) {
        context.dataStore.edit { prefs ->
            val current = ReminderSettings(
                soonMinutes = prefs[SOON_MINUTES] ?: 30,
                soonSoundEnabled = prefs[SOON_SOUND] ?: true,
                soonVibrationEnabled = prefs[SOON_VIBRATION] ?: true,
                dueSoundEnabled = prefs[DUE_SOUND] ?: true,
                dueVibrationEnabled = prefs[DUE_VIBRATION] ?: true,
                dueRingtoneUri = prefs[DUE_RINGTONE],
                repeatIntervalMinutes = prefs[REPEAT_INTERVAL] ?: 5,
                repeatCount = prefs[REPEAT_COUNT] ?: 3,
            )
            val next = transform(current)

            prefs[SOON_MINUTES] = next.soonMinutes.coerceIn(1, 240)
            prefs[SOON_SOUND] = next.soonSoundEnabled
            prefs[SOON_VIBRATION] = next.soonVibrationEnabled

            prefs[DUE_SOUND] = next.dueSoundEnabled
            prefs[DUE_VIBRATION] = next.dueVibrationEnabled
            if (next.dueRingtoneUri.isNullOrBlank()) prefs.remove(DUE_RINGTONE) else prefs[DUE_RINGTONE] = next.dueRingtoneUri

            prefs[REPEAT_INTERVAL] = next.repeatIntervalMinutes.coerceIn(1, 240)
            prefs[REPEAT_COUNT] = next.repeatCount.coerceIn(0, 20)
        }
    }
}

