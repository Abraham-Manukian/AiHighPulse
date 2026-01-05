package com.example.aihighpulse.ui.screens

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.StateFlow

data class SleepState(
    val tips: List<String> = listOf("Sleep 7-9 hours", "Keep a consistent schedule", "Avoid caffeine after 16:00"),
    val weeklyHours: List<Int> = listOf(7, 7, 6, 8, 7, 9, 8),
    val syncing: Boolean = false,
    val disclaimer: String? = "Not medical advice"
)

interface SleepPresenter {
    val state: StateFlow<SleepState>
    fun sync()
}

@Composable
expect fun rememberSleepPresenter(): SleepPresenter
