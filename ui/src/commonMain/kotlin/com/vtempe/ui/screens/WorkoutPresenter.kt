package com.vtempe.ui.screens

import androidx.compose.runtime.Composable
import com.vtempe.shared.domain.model.Workout
import kotlinx.coroutines.flow.StateFlow

data class WorkoutFeedback(
    val completedSets: Set<Int> = emptySet(),
    val notes: String = "",
    val submitted: Boolean = false
)

data class WorkoutState(
    val workouts: List<Workout> = emptyList(),
    val selectedWorkoutId: String? = null,
    val feedback: Map<String, WorkoutFeedback> = emptyMap()
)

interface WorkoutPresenter {
    val state: StateFlow<WorkoutState>
    fun select(workoutId: String)
    fun addSet(exerciseId: String, reps: Int, weight: Double?)
    fun toggleSetCompleted(workoutId: String, index: Int, completed: Boolean)
    fun updateNotes(workoutId: String, notes: String)
    fun submitFeedback(workoutId: String)
}

@Composable
expect fun rememberWorkoutPresenter(): WorkoutPresenter

