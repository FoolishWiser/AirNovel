package com.airnovel.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Brown40,
    onPrimary = Paper,
    primaryContainer = Brown80,
    onPrimaryContainer = Brown10,
    secondary = Warm40,
    onSecondary = Paper,
    secondaryContainer = Warm80,
    onSecondaryContainer = Color(0xFF2D1B12),
    tertiary = Red40,
    onTertiary = Paper,
    background = PaperLight,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = PaperDark,
    onSurfaceVariant = InkMedium,
    outline = InkLight,
    outlineVariant = Color(0xFFE8DDD0),
    error = Red40,
    onError = Paper,
    errorContainer = Red80,
    onErrorContainer = Red20,
)

private val DarkColorScheme = darkColorScheme(
    primary = Brown80,
    onPrimary = Brown10,
    primaryContainer = Brown40,
    onPrimaryContainer = Brown80,
    secondary = Warm80,
    onSecondary = Color(0xFF2D1B12),
    secondaryContainer = Warm20,
    onSecondaryContainer = Warm80,
    tertiary = Red80,
    onTertiary = Color(0xFF3A0A0A),
    background = DarkBg,
    onBackground = DarkOnBg,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = Color(0xFF3A3A3A),
    error = Red80,
    onError = Color(0xFF3A0A0A),
    errorContainer = Red20,
    onErrorContainer = Red80,
)

@Composable
fun AirNovelTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
