package dev.mariinkys.openPillReminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

const val CHANNEL_ID = "pill_reminder_channel"

fun createNotificationChannel(context: Context) {
    val channel = NotificationChannel(
        CHANNEL_ID,
        "Pill Reminders",
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "Daily pill reminder notifications"
    }
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.createNotificationChannel(channel)
}

fun sendPillNotification(context: Context, userName: String, isBreakDay: Boolean) {
    val title = if (isBreakDay) "Placebo Reminder" else "Pill Reminder"

    val namePart = userName.trim().takeIf { it.isNotEmpty() }
        ?.let { ", $it" }
        ?: ""

    val message = if (isBreakDay)
        "Time to take your placebo pill$namePart!"
    else
        "Time to take your pill$namePart!"

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .build()

    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.notify(1, notification)
}
