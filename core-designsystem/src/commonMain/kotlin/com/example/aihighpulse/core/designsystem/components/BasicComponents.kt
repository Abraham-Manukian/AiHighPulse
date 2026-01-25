package com.example.aihighpulse.core.designsystem.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aihighpulse.core.designsystem.theme.AiThemeDefaults

@Composable
fun PlaceholderScreen(title: String, sections: List<String>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = AiThemeDefaults.spacing.xl, vertical = AiThemeDefaults.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(AiThemeDefaults.spacing.md)
    ) {
        item {
            Text(
                title,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold
            )
        }
        items(sections) { section ->
            Card {
                Column(
                    modifier = Modifier.padding(AiThemeDefaults.spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(AiThemeDefaults.spacing.sm)
                ) {
                    Text(section, style = MaterialTheme.typography.titleMedium)
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
            color.copy(alpha = 0.35f),
            color.copy(alpha = 0.85f),
            color.copy(alpha = 0.35f),
        ),
        start = androidx.compose.ui.geometry.Offset(xShimmer - 200f, 0f),
        end = androidx.compose.ui.geometry.Offset(xShimmer, 100f)
    )
    Box(modifier = modifier.background(brush))
}

@Composable
fun StatChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    accentColor: Color = MaterialTheme.colorScheme.primary
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            AiIcon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                containerColor = accentColor.copy(alpha = 0.12f),
                modifier = Modifier.size(48.dp),
                padding = 12.dp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        Text(
            value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                lineHeight = 20.sp
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
