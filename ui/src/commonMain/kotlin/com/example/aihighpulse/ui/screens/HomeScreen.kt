@file:OptIn(org.jetbrains.compose.resources.ExperimentalResourceApi::class)

package com.example.aihighpulse.ui.screens
import com.example.aihighpulse.ui.*

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aihighpulse.core.designsystem.components.BrandScreen
import com.example.aihighpulse.core.designsystem.components.StatChip
import com.example.aihighpulse.core.designsystem.theme.AiPalette
import com.example.aihighpulse.ui.navigation.Routes
import com.example.aihighpulse.ui.util.kmpFormat
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit = {},
    presenter: HomePresenter = rememberHomePresenter()
) {
    val uiState by presenter.state.collectAsState()

    BrandScreen(Modifier.fillMaxSize()) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val columns = if (maxWidth < 700.dp) 1 else 2
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp)
            ) {
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
                            initialOffsetY = { it / 4 },
                            animationSpec = tween(300)
                        )
                    ) {
                        OverviewCard(
                            sets = uiState.todaySets,
                            volume = uiState.totalVolume,
                            sleepMinutes = uiState.sleepMinutes
                        )
                    }
                }
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(350, delayMillis = 60)) + slideInVertically(
                            initialOffsetY = { it / 3 },
                            animationSpec = tween(350)
                        )
                    ) {
                        QuickActionCard(onNavigate)
                    }
                }
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(400, delayMillis = 100)) + slideInVertically(
                            initialOffsetY = { it / 3 },
                            animationSpec = tween(400)
                        )
                    ) {
                        TodayWorkoutCard(onNavigate)
                    }
                }
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(450, delayMillis = 140)) + slideInVertically(
                            initialOffsetY = { it / 3 },
                            animationSpec = tween(450)
                        )
                    ) {
                        NutritionSummaryCard(onNavigate)
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewCard(sets: Int, volume: Int, sleepMinutes: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = homeCardColors(),
        elevation = homeCardElevation(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                stringResource(Res.string.home_today),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatChip(
                    stringResource(Res.string.home_sets),
                    stringResource(Res.string.home_sets_value).kmpFormat(sets),
                    icon = Icons.Filled.FitnessCenter
                )
                StatChip(
                    stringResource(Res.string.home_volume),
                    stringResource(Res.string.home_volume_value).kmpFormat(volume),
                    icon = Icons.Filled.Whatshot
                )
                StatChip(
                    stringResource(Res.string.home_sleep_hours),
                    stringResource(Res.string.home_sleep_value).kmpFormat(
                        sleepMinutes / 60,
                        sleepMinutes % 60
                    ),
                    icon = Icons.Filled.Bedtime
                )
            }
        }
    }
}

@Composable
private fun QuickActionCard(onNavigate: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = homeCardColors(),
        elevation = homeCardElevation(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ActionIcon(
                    icon = Icons.Filled.FitnessCenter,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                ) { onNavigate(Routes.Workout) }
                ActionIcon(
                    icon = Icons.Filled.Whatshot,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                ) { onNavigate(Routes.Nutrition) }
                ActionIcon(
                    icon = Icons.Filled.Bedtime,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                ) { onNavigate(Routes.Sleep) }
            }
        }
    }
}

@Composable
private fun TodayWorkoutCard(onNavigate: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = homeCardColors(),
        elevation = homeCardElevation(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.FitnessCenter,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Column {
                    Text(
                        stringResource(Res.string.home_workout_today_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        stringResource(Res.string.home_workout_today_sub),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { onNavigate(Routes.Workout) },
                    colors = homeButtonColors(),
                    elevation = homeButtonElevation()
                ) { Text(stringResource(Res.string.home_start)) }
                OutlinedButton(onClick = { /* preview plan */ }) {
                    Text(stringResource(Res.string.home_preview))
                }
            }
        }
    }
}

@Composable
private fun NutritionSummaryCard(onNavigate: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = homeCardColors(),
        elevation = homeCardElevation(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.Whatshot,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                Column {
                    Text(
                        stringResource(Res.string.home_nutrition_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        stringResource(Res.string.home_macros_sample),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Button(
                onClick = { onNavigate(Routes.Nutrition) },
                colors = homeButtonColors(),
                elevation = homeButtonElevation(),
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(Res.string.home_open_menu)) }
        }
    }
}

@Composable
private fun ActionIcon(
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.2f))
        ) {
            IconButton(onClick = onClick) {
                Icon(icon, contentDescription = null, tint = tint)
            }
        }
    }
}

@Composable
private fun homeCardColors() =
    CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))

@Composable
private fun homeCardElevation() = CardDefaults.cardElevation(defaultElevation = 8.dp)

@Composable
private fun homeButtonColors() =
    ButtonDefaults.buttonColors(containerColor = AiPalette.DeepAccent, contentColor = Color.White)

@Composable
private fun homeButtonElevation() =
    ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 8.dp)
