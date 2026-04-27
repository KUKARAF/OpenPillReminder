package dev.mariinkys.openPillReminder.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.mariinkys.openPillReminder.model.SettingsState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(settings: SettingsState, modifier: Modifier = Modifier) {

    if (!settings.active) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Tracking is not active")
        }
        return
    }

    val totalDays = settings.activePills + settings.breakDays

    val dates = List(totalDays) { index ->
        settings.firstPillDate.plusDays(index.toLong())
    }

    val takenPills = remember { mutableStateOf(setOf<LocalDate>()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val chunked = dates.chunked(7) // 7 rows per column

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            chunked.forEachIndexed { columnIndex, columnDates ->

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    columnDates.forEachIndexed { rowIndex, date ->

                        val index = columnIndex * 7 + rowIndex
                        val isBreakDay = index >= settings.activePills
                        val isToday = date == LocalDate.now()
                        val isTaken = takenPills.value.contains(date)

                        PillBubble(
                            date = date,
                            isBreakDay = isBreakDay,
                            isToday = isToday,
                            isTaken = isTaken,
                            onClick = {
                                takenPills.value =
                                    if (isTaken) takenPills.value - date
                                    else takenPills.value + date
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PillBubble(
    date: LocalDate,
    isBreakDay: Boolean,
    isToday: Boolean,
    isTaken: Boolean,
    onClick: () -> Unit
) {
    val formatterDay = DateTimeFormatter.ofPattern("EEE")
    val formatterDate = DateTimeFormatter.ofPattern("d")

    val targetColor = when {
        isTaken -> Color(0xFF4CAF50)        // green = taken
        isToday -> Color(0xFFFFC107)        // yellow = today
        isBreakDay -> Color(0xFFFFC1CC)     // light pink
        else -> Color(0xFFFF6F91)           // strong pink
    }

    // Animated color
    val animatedColor by animateColorAsState(targetValue = targetColor, label = "")

    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(animatedColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(date.format(formatterDay))
            Text(date.format(formatterDate))
        }
    }
}