package com.example.aihighpulse.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.aihighpulse.core.designsystem.theme.AiShapes
import com.example.aihighpulse.core.designsystem.theme.aiDarkColorScheme
import com.example.aihighpulse.core.designsystem.theme.aiLightColorScheme
import com.example.aihighpulse.ui.Res
import com.example.aihighpulse.ui.inter_medium
import com.example.aihighpulse.ui.inter_regular
import org.jetbrains.compose.resources.Font

@Composable
fun AiHighPulseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = platformColorScheme(darkTheme, dynamicColor)
        ?: if (darkTheme) aiDarkColorScheme() else aiLightColorScheme()
    val typography = aiTypography()

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = AiShapes,
        content = content
    )
}

@Composable
internal expect fun platformColorScheme(
    darkTheme: Boolean,
    dynamicColor: Boolean
): ColorScheme?

@Composable
private fun aiTypography(): Typography {
    val interFamily = FontFamily(
        Font(Res.font.inter_regular, FontWeight.Normal),
        Font(Res.font.inter_medium, FontWeight.Medium)
    )
    val title = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 24.sp
    )
    val body = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp
    )
    val caption = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )

    return Typography(
        displayLarge = title,
        displayMedium = title,
        displaySmall = title,
        headlineLarge = title,
        headlineMedium = title,
        headlineSmall = title,
        titleLarge = title,
        titleMedium = title,
        titleSmall = title,
        bodyLarge = body,
        bodyMedium = body,
        bodySmall = body,
        labelLarge = caption,
        labelMedium = caption,
        labelSmall = caption
    )
}
