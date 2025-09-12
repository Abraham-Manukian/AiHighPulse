package com.example.aihighpulse.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import com.example.aihighpulse.ui.vm.SleepViewModel
import com.example.aihighpulse.R
import com.example.aihighpulse.core.designsystem.components.BarChart

@Composable
fun SleepScreen() {
    val vm: SleepViewModel = koinViewModel()
    val s by vm.state.collectAsState()
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text(stringResource(R.string.sleep_title), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold) }
        item {
            Card {
                androidx.compose.foundation.layout.Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Weekly sleep", style = MaterialTheme.typography.titleMedium)
                    BarChart(data = s.weeklyHours, modifier = Modifier.fillMaxSize())
                    androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
                        listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun").forEach { d -> Text(d, style = MaterialTheme.typography.bodySmall) }
                    }
                }
            }
        }
        item { Button(onClick = { vm.sync() }) { Text("Sync with Health") } }
        items(s.tips.size) { idx -> Card { Text(s.tips[idx], Modifier.padding(16.dp)) } }
    }
}
