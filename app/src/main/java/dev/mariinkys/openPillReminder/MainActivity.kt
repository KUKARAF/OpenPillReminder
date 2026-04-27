package dev.mariinkys.openPillReminder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.mariinkys.openPillReminder.ui.AppLayout
import dev.mariinkys.openPillReminder.ui.theme.OpenPillReminderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel(this)
        enableEdgeToEdge()
        setContent {
            OpenPillReminderTheme {
                AppLayout()
            }
        }
    }
}