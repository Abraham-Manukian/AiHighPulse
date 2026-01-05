package com.example.aihighpulse.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object AiPalette {
    // Purple brand
    val Primary = Color(0xFF7C4DFF)        // Vibrant purple
    val PrimaryBright = Color(0xFFB47CFF)  // Light purple
    val PrimaryDark = Color(0xFF651FFF)    // Dark purple
    val PrimaryLighter = Color(0xFFB388FF) // Pastel purple
    val GradientHighlight = Color(0xFFEDE7F6)
    val DeepAccent = Color(0xFF4C1FD4)
    val OnGradient = Color(0xFF2D2D2D)
    val Secondary = Color(0xFF2FDFB1)
    val SecondaryDark = Color(0xFF1BB38E)
    val Tertiary = Color(0xFFFF7D87)
    val Neutral900 = Color(0xFF080B16)
    val Neutral800 = Color(0xFF111428)
    val Neutral100 = Color(0xFFF6F6FB)
    val Neutral50 = Color(0xFFFFFFFF)
    val SurfaceLight = Color(0xFFEFF1FA)
    val SurfaceDark = Color(0xFF181C31)
    val Outline = Color(0xFF414A66)
    val Success = Color(0xFF34D399)
    val Warning = Color(0xFFF8C04B)
    val Danger = Color(0xFFFF5A5F)
}

object AiGradients {
    fun lavenderMist(): Brush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF5100E6),
            AiPalette.Primary,
            AiPalette.PrimaryBright,
            Color(0xFFEFE1FF),
        ),
        startY = 0f,
        endY = 1600f
    )

    fun aurora(): Brush = Brush.linearGradient(
        colors = listOf(
            AiPalette.PrimaryDark,
            AiPalette.Primary,
            AiPalette.PrimaryBright,
        ),
        start = Offset.Zero,
        end = Offset(x = 600f, y = 600f)
    )

    fun purpleWave(): Brush = Brush.linearGradient(
        colors = listOf(
            AiPalette.PrimaryDark,
            AiPalette.Primary,
            AiPalette.PrimaryBright,
        ),
        start = Offset(x = 0f, y = 0f),
        end = Offset(x = 480f, y = 720f)
    )

    fun sunset(): Brush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFF6B8C),
            Color(0xFFFF8F6D),
            Color(0xFFFFC15A),
        ),
        start = Offset(x = 0f, y = 0f),
        end = Offset(x = 480f, y = 720f)
    )

    fun midnight(): Brush = Brush.radialGradient(
        colors = listOf(
            Color(0xFF1C2239),
            Color(0xFF0F1424),
        ),
        center = Offset.Zero,
        radius = 800f
    )
}

data class AiSpacing(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    val triple: Dp = 48.dp,
)

data class AiElevation(
    val level0: Dp = 0.dp,
    val level1: Dp = 1.dp,
    val level2: Dp = 3.dp,
    val level3: Dp = 6.dp,
    val level4: Dp = 12.dp,
    val level5: Dp = 24.dp,
)

val LocalAiSpacing = staticCompositionLocalOf { AiSpacing() }
val LocalAiElevation = staticCompositionLocalOf { AiElevation() }

private val Manrope = manropeFontFamily()

private val textShadow = Shadow(
    color = Color.Black.copy(alpha = 0.35f),
    offset = Offset(0f, 2f),
    blurRadius = 6f
)

val AiTypography: Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 36.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.5f).sp,
        shadow = textShadow
    ),
    headlineLarge = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp,
        lineHeight = 32.sp,
        shadow = textShadow
    ),
    titleLarge = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        shadow = textShadow
    ),
    titleMedium = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        shadow = textShadow
    ),
    bodyLarge = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        shadow = textShadow
    ),
    bodyMedium = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        shadow = textShadow
    ),
    labelLarge = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        shadow = textShadow
    ),
    labelMedium = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        shadow = textShadow
    ),
    labelSmall = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        shadow = textShadow
    )
)

val AiShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp)
)

fun aiLightColorScheme(): ColorScheme = lightColorScheme(
    primary = AiPalette.Primary,
    onPrimary = Color.White,
    primaryContainer = AiPalette.PrimaryBright,
    onPrimaryContainer = Color(0xFF1B134B),
    secondary = AiPalette.Secondary,
    onSecondary = Color(0xFF042720),
    secondaryContainer = Color(0xFFB0F2DE),
    onSecondaryContainer = Color(0xFF06483A),
    tertiary = AiPalette.Tertiary,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFD6DB),
    onTertiaryContainer = Color(0xFF4A0913),
    background = AiPalette.SurfaceLight,
    onBackground = Color(0xFF10142A),
    surface = AiPalette.Neutral50,
    onSurface = Color(0xFF131730),
    surfaceVariant = Color(0xFFE3E6F3),
    onSurfaceVariant = Color(0xFF434B62),
    outline = AiPalette.Outline,
    outlineVariant = Color(0xFFB4BDD2),
    scrim = Color(0xAA080B16),
    inverseSurface = Color(0xFF2A3049),
    inverseOnSurface = Color(0xFFE4E7F3),
    error = AiPalette.Danger,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

fun aiDarkColorScheme(): ColorScheme = darkColorScheme(
    primary = AiPalette.PrimaryBright,
    onPrimary = Color.Black,
    primaryContainer = AiPalette.PrimaryDark,
    onPrimaryContainer = Color.White,
    secondary = AiPalette.Secondary,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF085F4D),
    onSecondaryContainer = Color(0xFFCFFAE9),
    tertiary = AiPalette.Tertiary,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF7E2A37),
    onTertiaryContainer = Color(0xFFFFDADB),
    background = AiPalette.Neutral900,
    onBackground = Color(0xFFE4E7F3),
    surface = AiPalette.Neutral800,
    onSurface = Color(0xFFE0E3F4),
    surfaceVariant = Color(0xFF2C3450),
    onSurfaceVariant = Color(0xFFC3C9E5),
    outline = Color(0xFF565F7E),
    outlineVariant = Color(0xFF2E3651),
    scrim = Color(0xCC000000),
    inverseSurface = AiPalette.Neutral100,
    inverseOnSurface = Color(0xFF1A1F33),
    error = AiPalette.Danger,
    onError = Color.White,
    errorContainer = Color(0xFF932330),
    onErrorContainer = Color(0xFFFFDAD6)
)

object AiThemeDefaults {
    val spacing: AiSpacing
        @Composable get() = LocalAiSpacing.current

    val elevation: AiElevation
        @Composable get() = LocalAiElevation.current
}
