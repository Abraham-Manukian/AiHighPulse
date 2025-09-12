package com.example.aihighpulse.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PlaceholderScreen(title: String, sections: List<String>) {
    androidx.compose.foundation.lazy.LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        }
        items(sections.size) { idx ->
            Card {
                androidx.compose.foundation.layout.Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                ) {
                    Text(sections[idx], style = MaterialTheme.typography.titleMedium)
                    Skeleton(height = 10.dp)
                    Skeleton(height = 10.dp)
                    Skeleton(height = 10.dp)
                }
            }
        }
    }
}

@Composable
fun Skeleton(
    height: Dp,
    color: Color = MaterialTheme.colorScheme.surfaceVariant,
    shimmer: Boolean = true
) {
    val modifier = Modifier
        .fillMaxWidth()
        .height(height)
        .clip(MaterialTheme.shapes.small)

    if (!shimmer) {
        Box(modifier = modifier.background(color.copy(alpha = 0.6f)))
        return
    }

    val transition = rememberInfiniteTransition(label = "skeleton")
    val xShimmer by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "x"
    )
    val brush = Brush.linearGradient(
        colors = listOf(
            color.copy(alpha = 0.4f),
            color.copy(alpha = 0.8f),
            color.copy(alpha = 0.4f),
        ),
        start = androidx.compose.ui.geometry.Offset(xShimmer - 200f, 0f),
        end = androidx.compose.ui.geometry.Offset(xShimmer, 100f)
    )
    Box(modifier = modifier.background(brush))
}

@Composable
fun StatChip(label: String, value: String) {
    androidx.compose.foundation.layout.Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
