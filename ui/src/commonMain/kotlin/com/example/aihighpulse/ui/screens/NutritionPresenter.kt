package com.example.aihighpulse.ui.screens

import androidx.compose.runtime.Composable
import com.example.aihighpulse.shared.domain.model.NutritionPlan
import com.example.aihighpulse.ui.state.UiState
import kotlinx.coroutines.flow.StateFlow

data class NutritionState(
    val ui: UiState<NutritionPlan> = UiState.Loading,
    val selectedDay: String = "Mon"
)

interface NutritionPresenter {
    val state: StateFlow<NutritionState>
    fun refresh(weekIndex: Int = 0, force: Boolean = true)
    fun selectDay(day: String)
}

@Composable
expect fun rememberNutritionPresenter(): NutritionPresenter
