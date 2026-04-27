package dev.mariinkys.openPillReminder.ui.settings

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.mariinkys.openPillReminder.model.SettingsState
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: SettingsState,
    onSettingsChange: (SettingsState) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        // Name
        OutlinedTextField(
            value = settings.userName,
            onValueChange = { onSettingsChange(settings.copy(userName = it)) },
            label = { Text("Your name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        // Active Pills
        OutlinedTextField(
            value = if (settings.activePills == 0) "" else settings.activePills.toString(),
            onValueChange = { onSettingsChange(settings.copy(activePills = it.toIntOrNull() ?: 0)) },
            label = { Text("Active pills") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // Break Days
        OutlinedTextField(
            value = if (settings.breakDays == 0) "" else settings.breakDays.toString(),
            onValueChange = { onSettingsChange(settings.copy(breakDays = it.toIntOrNull() ?: 0)) },
            label = { Text("Break days") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // Placebo
        SettingsSwitchRow(
            label = "Placebo pills on break days",
            checked = settings.placebo,
            onCheckedChange = { onSettingsChange(settings.copy(placebo = it)) }
        )

        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        // First Pill Date
        OutlinedTextField(
            value = settings.firstPillDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            onValueChange = {},
            label = { Text("First pill date") },
            readOnly = true,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                TextButton(onClick = { showDatePicker = true }) {
                    Text("Pick")
                }
            }
        )

        Spacer(Modifier.height(12.dp))

        // Reminder Time
        OutlinedTextField(
            value = settings.reminderTime.format(DateTimeFormatter.ofPattern("HH:mm")),
            onValueChange = {},
            label = { Text("Reminder time") },
            readOnly = true,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                TextButton(onClick = {
                    TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            onSettingsChange(settings.copy(reminderTime = LocalTime.of(hour, minute)))
                        },
                        settings.reminderTime.hour,
                        settings.reminderTime.minute,
                        true
                    ).show()
                }) {
                    Text("Pick")
                }
            }
        )

        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        // Active
        SettingsSwitchRow(
            label = "Active",
            checked = settings.active,
            onCheckedChange = { onSettingsChange(settings.copy(active = it)) }
        )
    }


    if (showDatePicker) {
        DatePickerDialog(

            onDismissRequest = {
                @Suppress("AssignedValueIsNeverRead")
                showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onSettingsChange(settings.copy(firstPillDate = date))
                    }
                    @Suppress("AssignedValueIsNeverRead")
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = {
                    @Suppress("AssignedValueIsNeverRead")
                    showDatePicker = false
                }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}