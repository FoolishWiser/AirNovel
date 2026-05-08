package com.airnovel.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = DarkBrown,
    onPrimary = RicePaper,
    primaryContainer = LightBrownLight,
    onPrimaryContainer = DarkBrownDark,
    secondary = LightBrown,
    onSecondary = RicePaper,
    secondaryContainer = RicePaper,
    onSecondaryContainer = InkGray,
    tertiary = Vermilion,
    onTertiary = RicePaper,
    background = RicePaperLight,
    onBackground = InkGray,
    surface = RicePaper,
    onSurface = InkGray,
    surfaceVariant = RicePaperDark,
    onSurfaceVariant = InkGrayLight,
    outline = LightBrownLight,
    outlineVariant = RicePaperDark
)

private val DarkColorScheme = darkColorScheme(
    primary = LightBrownLight,
    onPrimary = InkGrayDark,
    primaryContainer = DarkBrownDark,
    onPrimaryContainer = RicePaper,
    secondary = LightBrown,
    onSecondary = RicePaper,
    secondaryContainer = NightSurface,
    onSecondaryContainer = NightText,
    tertiary = VermilionLight,
    onTertiary = RicePaper,
    background = NightBackground,
    onBackground = NightText,
    surface = NightSurface,
    onSurface = NightText,
    surfaceVariant = NightCard,
    onSurfaceVariant = NightTextSecondary,
    outline = NightTextSecondary,
    outlineVariant = NightCard
)

@Composable
fun AirNovelTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    followSystem: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
