package com.example.aihighpulse.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape

private val Green = Color(0xFF00B894)
private val Blue = Color(0xFF0984E3)
private val BgLight = Color(0xFFF9F9F9)
private val BgDark = Color(0xFF121212)
private val TextLight = Color(0xFF212121)
private val TextDark = Color(0xFFEDEDED)
private val Error = Color(0xFFD63031)

private val LightColors = lightColorScheme(
    primary = Green,
    secondary = Blue,
    background = BgLight,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextLight,
    onSurface = TextLight,
    error = Error,
    onError = Color.White,
)

private val DarkColors = darkColorScheme(
    primary = Green,
    secondary = Blue,
    background = BgDark,
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextDark,
    onSurface = TextDark,
    error = Error,
    onError = Color.White,
)

private val AppTypography = Typography(
    headlineLarge = TextStyle( // H1
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    titleLarge = TextStyle( // H2
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodySmall = TextStyle( // Caption
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp
    )
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),    // Buttons
    medium = RoundedCornerShape(16.dp),   // Cards
    large = RoundedCornerShape(24.dp)
)

@Composable
fun AiHighPulseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}

