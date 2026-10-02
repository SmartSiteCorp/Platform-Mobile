package com.smartsite.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val SmartSiteTypography = Typography(
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp, color = TextDark),
    titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextDark),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = TextDark),
    bodyLarge = TextStyle(fontSize = 16.sp, color = TextDark),
    bodyMedium = TextStyle(fontSize = 14.sp, color = TextDark),
    bodySmall = TextStyle(fontSize = 12.sp, color = TextMuted),
    labelSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp, color = TextMuted)
)
