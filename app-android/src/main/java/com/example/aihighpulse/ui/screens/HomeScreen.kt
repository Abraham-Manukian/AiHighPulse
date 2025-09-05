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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import com.example.aihighpulse.ui.components.StatChip
import com.example.aihighpulse.ui.navigation.Routes
import com.example.aihighpulse.ui.vm.HomeViewModel

@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    val vm: HomeViewModel = koinViewModel()
    val s by vm.state.collectAsState()

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val columns = if (maxWidth < 700.dp) 1 else 2
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
        ) {
            item { OverviewCard(sets = s.todaySets, volume = s.totalVolume) }
            item { QuickActionCard(onNavigate) }
            item { TodayWorkoutCard(onNavigate) }
            item { NutritionSummaryCard(onNavigate) }
        }
    }
}

@Composable
private fun OverviewCard(sets: Int, volume: Int) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation()) {
        androidx.compose.foundation.layout.Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Сегодня", style = MaterialTheme.typography.titleMedium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatChip("Подходы", sets.toString())
                StatChip("Объем", "${'$'}volume кг·повт")
                StatChip("Сон", "7ч 20м")
            }
        }
    }
}

@Composable
private fun QuickActionCard(onNavigate: (String) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        androidx.compose.foundation.layout.Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Быстрые действия", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { onNavigate(Routes.Workout) }) { Text("Тренировка") }
                OutlinedButton(onClick = { onNavigate(Routes.Nutrition) }) { Text("Питание") }
                OutlinedButton(onClick = { onNavigate(Routes.Sleep) }) { Text("Сон") }
            }
        }
    }
}

@Composable
private fun TodayWorkoutCard(onNavigate: (String) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        androidx.compose.foundation.layout.Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Тренировка на сегодня", style = MaterialTheme.typography.titleMedium)
            Text("Полное тело · 45 мин · 8 упражнений", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { onNavigate(Routes.Workout) }) { Text("Начать") }
                OutlinedButton(onClick = { /* preview plan */ }) { Text("Просмотр") }
            }
        }
    }
}

@Composable
private fun NutritionSummaryCard(onNavigate: (String) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        androidx.compose.foundation.layout.Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Питание", style = MaterialTheme.typography.titleMedium)
            Text("Белки 120г · Жиры 60г · Углеводы 250г", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { onNavigate(Routes.Nutrition) }) { Text("Открыть меню") }
            }
        }
    }
}
