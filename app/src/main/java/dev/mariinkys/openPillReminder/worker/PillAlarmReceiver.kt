package dev.mariinkys.openPillReminder.worker

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.mariinkys.openPillReminder.data.PillLogRepository
import dev.mariinkys.openPillReminder.data.SettingsRepository
import dev.mariinkys.openPillReminder.sendPillNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

class PillAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        val isRenotify = intent.getBooleanExtra(ReminderScheduler.EXTRA_IS_RENOTIFY, false)
        val snoozeMinutes = intent.getIntExtra(ReminderScheduler.EXTRA_SNOOZE_MINUTES, 0)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Snooze action: dismiss current notification and reschedule the re-notify chain
                // to fire once at now + snoozeMinutes. Does not touch tomorrow's daily.
                if (snoozeMinutes > 0) {
                    val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    nm.cancel(1)
                    ReminderScheduler.scheduleReNotify(context, snoozeMinutes)
                    return@launch
                }

                val settings = SettingsRepository(context).settingsFlow.first()

                val firstDate = settings.firstPillDate
                val cycleLength = (settings.activePills + settings.breakDays).coerceAtLeast(1)
                val today = LocalDate.now()
                val daysSinceStart = java.time.temporal.ChronoUnit.DAYS.between(firstDate, today)

                if (daysSinceStart >= 0) {
                    val positionInCycle = (daysSinceStart % cycleLength).toInt()
                    val isBreakDay = positionInCycle >= settings.activePills

                    val todayLog = PillLogRepository(context).pillLogsFlow.first()[today]
                    val alreadyTaken = todayLog?.taken == true

                    if (!alreadyTaken && (!isBreakDay || settings.placebo)) {
                        sendPillNotification(context, settings.userName, isBreakDay, today)
                        // Chain the next re-notification until the pill is marked taken.
                        ReminderScheduler.scheduleReNotify(context, settings.reNotifyInterval)
                    }
                }

                // Only the daily fire reschedules tomorrow's alarm; re-notify fires do not.
                if (!isRenotify) {
                    ReminderScheduler.schedulePillReminder(context, settings.reminderTime)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
