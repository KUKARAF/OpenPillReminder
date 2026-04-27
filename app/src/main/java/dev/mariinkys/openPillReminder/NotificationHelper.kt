package dev.mariinkys.openPillReminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

const val CHANNEL_ID = "pill_reminder_channel"

fun createNotificationChannel(context: Context) {
    val channel = NotificationChannel(
        CHANNEL_ID,
        "Pill Reminders",
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "Notifications for pill reminders"
    }
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.createNotificationChannel(channel)
}