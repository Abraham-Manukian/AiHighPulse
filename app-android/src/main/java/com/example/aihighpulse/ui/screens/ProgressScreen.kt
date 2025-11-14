package com.example.aihighpulse.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import com.example.aihighpulse.ui.vm.ProgressViewModel
import com.example.aihighpulse.R
import com.example.aihighpulse.core.designsystem.components.BarChart
import com.example.aihighpulse.core.designsystem.components.BrandScreen
import com.example.aihighpulse.core.designsystem.theme.AiPalette

@Composable
fun ProgressScreen() {
    val vm: ProgressViewModel = koinViewModel()
    val s by vm.state.collectAsState()
    val dayLabels = listOf(
        stringResource(R.string.day_mon_short),
        stringResource(R.string.day_tue_short),
        stringResource(R.string.day_wed_short),
        stringResource(R.string.day_thu_short),
        stringResource(R.string.day_fri_short),
        stringResource(R.string.day_sat_short),
        stringResource(R.string.day_sun_short)
    )
    val contentColor = AiPalette.OnGradient

    BrandScreen(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Text(
                    stringResource(R.string.progress_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(300)) + slideInVertically(initialOffsetY = { it / 5 }, animationSpec = tween(300))
                ) {
                    Card(
                        colors = progressCardColors(),
                        elevation = progressCardElevation(),
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(stringResource(R.string.progress_total_workouts, s.totalWorkouts), color = contentColor)
                            Text(stringResource(R.string.progress_total_sets, s.totalSets), color = contentColor)
                            Text(stringResource(R.string.progress_total_volume, s.totalVolume), color = contentColor)
                        }
                    }
                }
            }
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(350)) + slideInVertically(initialOffsetY = { it / 6 }, animationSpec = tween(350))
                ) {
                    Card(
                        colors = progressCardColors(),
                        elevation = progressCardElevation(),
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(stringResource(R.string.progress_weekly_volume), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = contentColor)
                            BarChart(data = s.weeklyVolumes.map { it.coerceAtLeast(0) }, modifier = Modifier.fillMaxWidth())
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                dayLabels.forEachIndexed { index, label ->
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(label, style = MaterialTheme.typography.labelMedium, color = contentColor.copy(alpha = 0.7f))
                                        Text("${s.weeklyVolumes.getOrNull(index) ?: 0}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = contentColor)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(400)) + slideInVertically(initialOffsetY = { it / 6 }, animationSpec = tween(400))
                ) {
                    Card(
                        colors = progressCardColors(),
                        elevation = progressCardElevation(),
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(stringResource(R.string.sleep_weekly_chart_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = contentColor)
                            BarChart(data = s.sleepHoursWeek, modifier = Modifier.fillMaxWidth())
                            Text(
                                stringResource(R.string.sleep_title),
                                style = MaterialTheme.typography.bodyMedium,
                                color = contentColor.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun progressCardColors() = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.97f))

@Composable
private fun progressCardElevation() = CardDefaults.cardElevation(defaultElevation = 8.dp)
