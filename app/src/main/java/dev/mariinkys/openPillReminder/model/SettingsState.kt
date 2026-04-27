package dev.mariinkys.openPillReminder.model

import java.time.LocalDate
import java.time.LocalTime

data class SettingsState(
    val userName: String = "",
    val activePills: Int = 21,
    val breakDays: Int = 7,
    val placebo: Boolean = false,
    val firstPillDate: LocalDate = LocalDate.now(),
    val reminderTime: LocalTime = LocalTime.of(8, 0),
    val active: Boolean = false
)