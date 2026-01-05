package com.example.aihighpulse.ui.screens

import androidx.compose.runtime.Composable
import com.example.aihighpulse.shared.domain.model.Workout
import kotlinx.coroutines.flow.StateFlow

data class HomeState(
    val workouts: List<Workout> = emptyList(),
    val todaySets: Int = 0,
    val totalVolume: Int = 0,
    val sleepMinutes: Int = 7 * 60 + 20,
    val loading: Boolean = false,
)

interface HomePresenter {
    val state: StateFlow<HomeState>
}

@Composable
expect fun rememberHomePresenter(): HomePresenter
