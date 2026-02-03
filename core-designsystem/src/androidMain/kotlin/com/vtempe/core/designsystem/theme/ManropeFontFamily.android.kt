package com.vtempe.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.vtempe.core.designsystem.Res
import com.vtempe.core.designsystem.manrope_variable
import org.jetbrains.compose.resources.Font

internal actual @Composable fun manropeFontFamily(): FontFamily = FontFamily(
    Font(Res.font.manrope_variable, FontWeight.Light),
    Font(Res.font.manrope_variable, FontWeight.Normal),
    Font(Res.font.manrope_variable, FontWeight.Medium),
    Font(Res.font.manrope_variable, FontWeight.SemiBold),
    Font(Res.font.manrope_variable, FontWeight.Bold),
    Font(Res.font.manrope_variable, FontWeight.ExtraBold)
)