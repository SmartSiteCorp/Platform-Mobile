package com.smartsite.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SmartSiteColorScheme = lightColorScheme(
    primary = Orange,
    onPrimary = Color.White,
    secondary = Sage,
    onSecondary = Color.White,
    background = Cream,
    onBackground = TextDark,
    surface = CardWhite,
    onSurface = TextDark,
    surfaceVariant = Cream,
    onSurfaceVariant = TextMuted,
    outline = BorderLight,
    error = AccentRed
)

@Composable
fun SmartSiteTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SmartSiteColorScheme,
        typography = SmartSiteTypography,
        content = content
    )
}
