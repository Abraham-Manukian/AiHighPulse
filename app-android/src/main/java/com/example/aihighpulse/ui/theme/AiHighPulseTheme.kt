package com.example.aihighpulse.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import com.example.aihighpulse.core.designsystem.theme.AiElevation
import com.example.aihighpulse.core.designsystem.theme.AiShapes
import com.example.aihighpulse.core.designsystem.theme.AiSpacing
import com.example.aihighpulse.core.designsystem.theme.AiThemeDefaults
import com.example.aihighpulse.core.designsystem.theme.AiTypography
import com.example.aihighpulse.core.designsystem.theme.LocalAiElevation
import com.example.aihighpulse.core.designsystem.theme.LocalAiSpacing
import com.example.aihighpulse.core.designsystem.theme.aiDarkColorScheme
import com.example.aihighpulse.core.designsystem.theme.aiLightColorScheme

@Composable
fun AiHighPulseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    spacing: AiSpacing = AiSpacing(),
    elevation: AiElevation = AiElevation(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> aiDarkColorScheme()
        else -> aiLightColorScheme()
    }

    CompositionLocalProvider(
        LocalAiSpacing provides spacing,
        LocalAiElevation provides elevation,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AiTypography,
            shapes = AiShapes,
            content = content
        )
    }
}

object AiTheme {
    val spacing: AiSpacing
        @Composable get() = AiThemeDefaults.spacing

    val elevation: AiElevation
        @Composable get() = AiThemeDefaults.elevation
}
