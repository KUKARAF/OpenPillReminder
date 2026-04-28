package dev.mariinkys.openPillReminder.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import dev.mariinkys.openPillReminder.model.SettingsState
import dev.mariinkys.openPillReminder.model.ThemeMode

@Composable
fun OpenPillReminderTheme(
    settings: SettingsState,
    content: @Composable () -> Unit
) {
    val darkTheme = when (settings.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        settings.useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> generateColorSchemeFromSeed(settings.seedColor, darkTheme)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

private fun generateColorSchemeFromSeed(seedColor: Int, isDark: Boolean): ColorScheme {
    val base = Color(seedColor)


    return if (isDark) {
        val darkSurface = Color(0xFF121212)

        darkColorScheme(
            primary = base,
            onPrimary = Color.White,
            primaryContainer = base.copy(alpha = 0.7f),
            onPrimaryContainer = Color.White,
            secondary = base.copy(alpha = 0.85f),
            onSecondary = Color.White,
            secondaryContainer = base.copy(alpha = 0.6f),
            onSecondaryContainer = Color.White,
            tertiary = base.copy(alpha = 0.75f),
            onTertiary = Color.White,
            surface = Color(0xFF121212),
            onSurface = Color.White,
            background = darkSurface,
            surfaceVariant = darkSurface,
        )
    } else {
        val lightSurface = Color.White

        lightColorScheme(
            primary = base,
            onPrimary = Color.White,
            primaryContainer = base.copy(alpha = 0.2f),
            onPrimaryContainer = Color.Black,
            secondary = base.copy(alpha = 0.3f),
            onSecondary = Color.Black,
            secondaryContainer = base.copy(alpha = 0.15f),
            onSecondaryContainer = Color.Black,
            tertiary = base.copy(alpha = 0.25f),
            onTertiary = Color.Black,
            surface = Color.White,
            onSurface = Color.Black,
            background = lightSurface,
            surfaceVariant = lightSurface,
        )
    }
}