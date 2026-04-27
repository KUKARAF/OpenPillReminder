package dev.mariinkys.openPillReminder.worker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

object ReminderScheduler {

    private const val ALARM_REQUEST_CODE = 1001

    fun schedule(context: Context, reminderTime: LocalTime) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val now = LocalDateTime.now()
        var next = now.toLocalDate().atTime(reminderTime)

        if (!next.isAfter(now)) {
            next = next.plusDays(1)
        }

        val triggerTimeMillis = next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val pendingIntent = getPendingIntent(context)

        // Use exact alarm, bypassing battery optimizations
        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTimeMillis,
                pendingIntent
            )
        } catch (e: SecurityException) {
            // Handle Android 14+ case where SCHEDULE_EXACT_ALARM permission is revoked
        }
    }

    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(getPendingIntent(context))
    }

    private fun getPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, PillAlarmReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}