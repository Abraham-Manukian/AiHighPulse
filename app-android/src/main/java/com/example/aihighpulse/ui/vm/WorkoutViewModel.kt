package com.example.aihighpulse.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aihighpulse.shared.domain.model.Workout
import com.example.aihighpulse.shared.domain.model.WorkoutSet
import com.example.aihighpulse.shared.domain.usecase.GenerateTrainingPlan
import com.example.aihighpulse.shared.domain.usecase.LogWorkoutSet
import com.example.aihighpulse.shared.domain.repository.TrainingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class WorkoutState(
    val workouts: List<Workout> = emptyList(),
    val selectedWorkoutId: String? = null,
)

class WorkoutViewModel(
    private val trainingRepository: TrainingRepository,
    private val logWorkoutSet: LogWorkoutSet,
    private val generateTrainingPlan: GenerateTrainingPlan,
) : ViewModel() {
    private val _state = MutableStateFlow(WorkoutState())
    val state: StateFlow<WorkoutState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            trainingRepository.observeWorkouts().collectLatest { list ->
                val selected = _state.value.selectedWorkoutId ?: list.firstOrNull()?.id
                _state.value = _state.value.copy(workouts = list, selectedWorkoutId = selected)
            }
        }
        viewModelScope.launch { runCatching { generateTrainingPlan(0) } }
    }

    fun select(workoutId: String) { _state.value = _state.value.copy(selectedWorkoutId = workoutId) }

    fun addSet(exerciseId: String, reps: Int, weight: Double?, rpe: Double?) {
        val id = _state.value.selectedWorkoutId ?: return
        viewModelScope.launch { logWorkoutSet(id, WorkoutSet(exerciseId, reps, weight, rpe)) }
    }
}
