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
    private const val BUYING_ALARM_REQUEST_CODE = 1002
    const val RENOTIFY_REQUEST_CODE = 1003
    const val SNOOZE_1H_REQUEST_CODE = 1004
    const val SNOOZE_2H_REQUEST_CODE = 1005

    const val EXTRA_IS_RENOTIFY = "is_renotify"
    const val EXTRA_SNOOZE_MINUTES = "snooze_minutes"

    fun schedulePillReminder(context: Context, reminderTime: LocalTime) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val now = LocalDateTime.now()
        var next = now.toLocalDate().atTime(reminderTime)

        if (!next.isAfter(now)) {
            next = next.plusDays(1)
        }

        val triggerTimeMillis = next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val intent = Intent(context, PillAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTimeMillis,
                pendingIntent
            )
        } catch (e: SecurityException) {
            // TODO: handle Android 14+ case where SCHEDULE_EXACT_ALARM permission is revoked
        }
    }

    fun scheduleReNotify(context: Context, minutesFromNow: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerTimeMillis = System.currentTimeMillis() + minutesFromNow.coerceAtLeast(1) * 60_000L

        val intent = Intent(context, PillAlarmReceiver::class.java).apply {
            putExtra(EXTRA_IS_RENOTIFY, true)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            RENOTIFY_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTimeMillis,
                pendingIntent
            )
        } catch (e: SecurityException) {
            // TODO: handle Android 14+ case where SCHEDULE_EXACT_ALARM permission is revoked
        }
    }

    fun cancelReNotifyAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, PillAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            RENOTIFY_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun cancelRepeatingPillReminder(context: Context) {
        cancelReNotifyAlarm(context)

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        nm.cancel(1)
    }

    fun scheduleBuyingReminder(context: Context, time: LocalTime) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val now = LocalDateTime.now()
        var next = now.toLocalDate().atTime(time)

        if (!next.isAfter(now)) next = next.plusDays(1)

        val triggerTimeMillis = next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val intent = Intent(context, BuyingAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            BUYING_ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent)
        } catch (e: SecurityException) {
            // TODO: handle Android 14+ case where SCHEDULE_EXACT_ALARM permission is revoked
        }
    }

    fun cancelBuyingAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, BuyingAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            BUYING_ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}