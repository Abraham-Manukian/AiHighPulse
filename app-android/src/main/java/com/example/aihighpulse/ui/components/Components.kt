package com.example.aihighpulse.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun Skeleton(height: Dp, color: Color = MaterialTheme.colorScheme.surfaceVariant) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(MaterialTheme.shapes.small)
            .background(color.copy(alpha = 0.6f))
    )
}

@Composable
fun StatChip(label: String, value: String) {
    androidx.compose.foundation.layout.Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

