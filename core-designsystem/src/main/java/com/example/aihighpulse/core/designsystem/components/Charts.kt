package com.example.aihighpulse.core.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BarChart(
    data: List<Int>,
    modifier: Modifier = Modifier,
    barColor: Color? = null,
    barWidth: Dp = 16.dp,
    spacing: Dp = 12.dp,
) {
    val resolvedBarColor = barColor ?: MaterialTheme.colorScheme.primary
    val maxVal = (data.maxOrNull() ?: 0).coerceAtLeast(1)
    Canvas(modifier = modifier.fillMaxWidth().height(120.dp)) {
        val widthPx = size.width
        val heightPx = size.height
        val barW = barWidth.toPx()
        val gap = spacing.toPx()
        val totalBarsWidth = data.size * barW + (data.size - 1) * gap
        var x = (widthPx - totalBarsWidth) / 2f
        data.forEach { value ->
            val h = (value.toFloat() / maxVal.toFloat()) * heightPx
            drawRect(
                color = resolvedBarColor,
                topLeft = Offset(x, heightPx - h),
                size = androidx.compose.ui.geometry.Size(barW, h)
            )
            x += barW + gap
        }
    }
}

@Composable
fun RingChart(
    values: List<Float>,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 16.dp,
) {
    val total = values.sum().coerceAtLeast(0.0001f)
    val sweepAngles = values.map { v -> (v / total) * 360f }
    val fallbackColor = MaterialTheme.colorScheme.primary
    Canvas(modifier = modifier.fillMaxWidth().height(160.dp)) {
        var start = -90f
        val strokePx = strokeWidth.toPx()
        sweepAngles.forEachIndexed { i, sweep ->
            drawArc(
                color = colors.getOrNull(i) ?: fallbackColor,
                startAngle = start,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(width = strokePx)
            )
            start += sweep
        }
    }
}

@Composable
fun LineChart(
    values: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color? = null,
    strokeWidth: Dp = 3.dp,
) {
    if (values.isEmpty()) return
    val color = lineColor ?: MaterialTheme.colorScheme.secondary
    Canvas(modifier = modifier.fillMaxWidth().height(160.dp)) {
        val w = size.width
        val h = size.height
        val min = values.minOrNull() ?: 0f
        val max = values.maxOrNull() ?: 1f
        val range = (max - min).takeIf { it > 0f } ?: 1f
        val stepX = w / (values.size - 1).coerceAtLeast(1)
        val path = Path()
        values.forEachIndexed { i, v ->
            val x = stepX * i
            val y = h - ((v - min) / range) * h
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = strokeWidth.toPx())
        )
    }
}
