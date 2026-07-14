package dev.mariinkys.openPillReminder.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.mariinkys.openPillReminder.data.PillLogRepository
import dev.mariinkys.openPillReminder.data.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                val settings = SettingsRepository(context).settingsFlow.first()

                ReminderScheduler.schedulePillReminder(context, settings.reminderTime)

                if (settings.buyingReminder) {
                    ReminderScheduler.scheduleBuyingReminder(context, settings.buyingReminderTime)
                }

                // Reboots clear all AlarmManager alarms, including a pending re-notify chain,
                // so re-arm it if today's pill is still un-taken and keep-reminding is enabled.
                val cycleLength = (settings.activePills + settings.breakDays).coerceAtLeast(1)
                val today = LocalDate.now()
                val daysSinceStart = ChronoUnit.DAYS.between(settings.firstPillDate, today)

                if (daysSinceStart >= 0) {
                    val positionInCycle = (daysSinceStart % cycleLength).toInt()
                    val isBreakDay = positionInCycle >= settings.activePills

                    val alreadyTaken = PillLogRepository(context).pillLogsFlow.first()[today]?.taken == true

                    if (!alreadyTaken && (!isBreakDay || settings.placebo) && settings.keepReminding) {
                        ReminderScheduler.scheduleReNotify(context, settings.reNotifyInterval)
                    }
                }

                pendingResult.finish()
            }
        }
    }
}