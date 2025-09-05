package com.example.aihighpulse.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import com.example.aihighpulse.ui.vm.ProgressViewModel

@Composable
fun ProgressScreen() {
    val vm: ProgressViewModel = koinViewModel()
    val s by vm.state.collectAsState()
    Card(Modifier.padding(16.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Статистика", style = MaterialTheme.typography.titleLarge)
            Text("Тренировок: ${'$'}{s.totalWorkouts}")
            Text("Подходов: ${'$'}{s.totalSets}")
            Text("Объем: ${'$'}{s.totalVolume} кг·повт")
        }
    }
}
