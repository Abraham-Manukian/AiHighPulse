package com.vtempe.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.vtempe.core.designsystem.theme.AiGradients

@Composable
fun BrandScreen(
    modifier: Modifier = Modifier,
    gradient: Brush = AiGradients.lavenderMist(),
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.background(gradient)) {
        Surface(
            color = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            content = content
        )
    }
}







