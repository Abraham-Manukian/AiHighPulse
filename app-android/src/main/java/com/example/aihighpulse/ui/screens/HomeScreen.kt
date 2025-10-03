package com.example.aihighpulse.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import com.example.aihighpulse.core.designsystem.components.StatChip
import com.example.aihighpulse.ui.navigation.Routes
import com.example.aihighpulse.ui.vm.HomeViewModel
import com.example.aihighpulse.R

@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    val vm: HomeViewModel = koinViewModel()
    val uiState = vm.state.collectAsState().value

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val columns = if (maxWidth < 700.dp) 1 else 2
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
        ) {
            item { OverviewCard(sets = uiState.todaySets, volume = uiState.totalVolume, sleepMinutes = uiState.sleepMinutes) }
            item { QuickActionCard(onNavigate) }
            item { TodayWorkoutCard(onNavigate) }
            item { NutritionSummaryCard(onNavigate) }
        }
    }
}

@Composable
private fun OverviewCard(sets: Int, volume: Int, sleepMinutes: Int) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation()) {
        androidx.compose.foundation.layout.Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.home_today), style = MaterialTheme.typography.titleMedium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatChip(stringResource(R.string.home_sets), stringResource(R.string.home_sets_value, sets))
                StatChip(stringResource(R.string.home_volume), stringResource(R.string.home_volume_value, volume))
                StatChip(stringResource(R.string.home_sleep_hours), stringResource(R.string.home_sleep_value, sleepMinutes / 60, sleepMinutes % 60))
            }
        }
    }
}

@Composable
private fun QuickActionCard(onNavigate: (String) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        androidx.compose.foundation.layout.Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(stringResource(R.string.home_quick_actions), style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { onNavigate(Routes.Workout) }) { Text(stringResource(R.string.nav_workout)) }
                OutlinedButton(onClick = { onNavigate(Routes.Nutrition) }) { Text(stringResource(R.string.nav_nutrition)) }
                OutlinedButton(onClick = { onNavigate(Routes.Sleep) }) { Text(stringResource(R.string.nav_sleep)) }
            }
        }
    }
}

@Composable
private fun TodayWorkoutCard(onNavigate: (String) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        androidx.compose.foundation.layout.Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.home_workout_today_title), style = MaterialTheme.typography.titleMedium)
            Text(stringResource(R.string.home_workout_today_sub), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { onNavigate(Routes.Workout) }) { Text(stringResource(R.string.home_start)) }
                OutlinedButton(onClick = { /* preview plan */ }) { Text(stringResource(R.string.home_preview)) }
            }
        }
    }
}

@Composable
private fun NutritionSummaryCard(onNavigate: (String) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        androidx.compose.foundation.layout.Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.home_nutrition_title), style = MaterialTheme.typography.titleMedium)
            Text(stringResource(R.string.home_macros_sample), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { onNavigate(Routes.Nutrition) }) { Text(stringResource(R.string.home_open_menu)) }
            }
        }
    }
}

