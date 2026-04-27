package dev.mariinkys.openPillReminder.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.mariinkys.openPillReminder.model.SettingsState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalTime

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object SettingsKeys {
    val USER_NAME = stringPreferencesKey("user_name")
    val ACTIVE_PILLS = intPreferencesKey("active_pills")
    val BREAK_DAYS = intPreferencesKey("break_days")
    val PLACEBO = booleanPreferencesKey("placebo")
    val FIRST_PILL_DATE = stringPreferencesKey("first_pill_date")
    val REMINDER_HOUR = intPreferencesKey("reminder_hour")
    val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
    val ACTIVE = booleanPreferencesKey("active")
}

class SettingsRepository(private val context: Context) {

    val settingsFlow: Flow<SettingsState> = context.dataStore.data.map { prefs ->
        SettingsState(
            userName = prefs[SettingsKeys.USER_NAME] ?: "",
            activePills = prefs[SettingsKeys.ACTIVE_PILLS] ?: 21,
            breakDays = prefs[SettingsKeys.BREAK_DAYS] ?: 7,
            placebo = prefs[SettingsKeys.PLACEBO] ?: false,
            firstPillDate = prefs[SettingsKeys.FIRST_PILL_DATE]
                ?.let { LocalDate.parse(it) }
                ?: LocalDate.now(),
            reminderTime = LocalTime.of(
                prefs[SettingsKeys.REMINDER_HOUR] ?: 8,
                prefs[SettingsKeys.REMINDER_MINUTE] ?: 0
            ),
            active = prefs[SettingsKeys.ACTIVE] ?: false
        )
    }

    suspend fun saveSettings(settings: SettingsState) {
        context.dataStore.edit { prefs ->
            prefs[SettingsKeys.USER_NAME] = settings.userName
            prefs[SettingsKeys.ACTIVE_PILLS] = settings.activePills
            prefs[SettingsKeys.BREAK_DAYS] = settings.breakDays
            prefs[SettingsKeys.PLACEBO] = settings.placebo
            prefs[SettingsKeys.FIRST_PILL_DATE] = settings.firstPillDate.toString()
            prefs[SettingsKeys.REMINDER_HOUR] = settings.reminderTime.hour
            prefs[SettingsKeys.REMINDER_MINUTE] = settings.reminderTime.minute
            prefs[SettingsKeys.ACTIVE] = settings.active
        }
    }
}