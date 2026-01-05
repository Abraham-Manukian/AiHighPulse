package com.example.aihighpulse.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aihighpulse.core.designsystem.theme.AiGradients
import com.example.aihighpulse.core.designsystem.theme.AiThemeDefaults

@Composable
fun AuroraCard(
    modifier: Modifier = Modifier,
    gradient: Brush = AiGradients.aurora(),
    overlayColor: Color = Color.White.copy(alpha = 0.08f),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(gradient)
            .background(overlayColor)
            .padding(AiThemeDefaults.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(AiThemeDefaults.spacing.md),
        content = content
    )
}

@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
    containerColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
    borderColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
    blurred: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val surfaceModifier = if (blurred && isAdvancedBlurAvailable()) {
        modifier.blur(24.dp)
    } else {
        modifier
    }

    Surface(
        modifier = surfaceModifier,
        shape = shape,
        color = containerColor,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = AiThemeDefaults.spacing.xl,
                vertical = AiThemeDefaults.spacing.lg
            ),
            verticalArrangement = Arrangement.spacedBy(AiThemeDefaults.spacing.md),
            content = content
        )
    }
}

@Composable
fun MetricTile(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    trailing: (@Composable () -> Unit)? = null
) {
    GlassPanel(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.78f),
        borderColor = accentColor.copy(alpha = 0.18f)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AiThemeDefaults.spacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            trailing?.invoke()
        }
    }
}

@Composable
fun QuickActionChip(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    tonalColor: Color = MaterialTheme.colorScheme.primary
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(
                    horizontal = AiThemeDefaults.spacing.lg,
                    vertical = AiThemeDefaults.spacing.md
                ),
            horizontalArrangement = Arrangement.spacedBy(AiThemeDefaults.spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(tonalColor.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tonalColor
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                letterSpacing = 0.2.sp
            )
        }
    }
}
