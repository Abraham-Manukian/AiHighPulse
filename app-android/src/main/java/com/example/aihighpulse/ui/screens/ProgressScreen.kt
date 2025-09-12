package com.example.aihighpulse.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aihighpulse.R
import com.example.aihighpulse.core.designsystem.components.StatChip
import com.example.aihighpulse.core.designsystem.components.BarChart
import com.example.aihighpulse.core.designsystem.components.LineChart
import com.example.aihighpulse.core.designsystem.components.Skeleton
import com.example.aihighpulse.ui.vm.ProgressViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProgressScreen() {
    val vm: ProgressViewModel = koinViewModel()
    val s by vm.state.collectAsState()
    var tab by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(1) }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.progress_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
        item {
            TabRow(selectedTabIndex = tab) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Weight") })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Training") })
                Tab(selected = tab == 2, onClick = { tab = 2 }, text = { Text("Sleep") })
                Tab(selected = tab == 3, onClick = { tab = 3 }, text = { Text("Calories") })
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = stringResource(R.string.nav_progress),
                        style = MaterialTheme.typography.titleMedium
                    )
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatChip(label = stringResource(R.string.nav_progress), value = s.totalWorkouts.toString())
                        StatChip(label = stringResource(R.string.home_sets), value = s.totalSets.toString())
                        StatChip(label = stringResource(R.string.home_volume), value = "${s.totalVolume} kg")
                    }
                }
            }
        }
        when (tab) {
            0 -> item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Weight", style = MaterialTheme.typography.titleMedium)
                        if (s.weightSeries.isEmpty()) { Skeleton(10.dp) } else {
                            LineChart(values = s.weightSeries, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }
            1 -> item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(text = stringResource(R.string.progress_weekly_volume), style = MaterialTheme.typography.titleMedium)
                        BarChart(data = s.weeklyVolumes, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
            2 -> item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Sleep", style = MaterialTheme.typography.titleMedium)
                        BarChart(data = s.sleepHoursWeek, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
            3 -> item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Calories", style = MaterialTheme.typography.titleMedium)
                        if (s.caloriesSeries.isEmpty()) { Skeleton(10.dp) } else {
                            LineChart(values = s.caloriesSeries.map { it.toFloat() }, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        }
    }
}
