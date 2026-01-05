package com.example.aihighpulse.ui.screens

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.StateFlow

data class ProgressState(
    val totalWorkouts: Int = 0,
    val totalSets: Int = 0,
    val totalVolume: Int = 0,
    val weeklyVolumes: List<Int> = List(7) { 0 },
    val weightSeries: List<Float> = emptyList(),
    val caloriesSeries: List<Int> = emptyList(),
    val sleepHoursWeek: List<Int> = List(7) { 0 },
)

interface ProgressPresenter {
    val state: StateFlow<ProgressState>
}

@Composable
expect fun rememberProgressPresenter(): ProgressPresenter
