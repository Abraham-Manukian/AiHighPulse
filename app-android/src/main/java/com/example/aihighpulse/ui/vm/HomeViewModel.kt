package com.example.aihighpulse.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aihighpulse.shared.domain.usecase.GenerateTrainingPlan
import com.example.aihighpulse.shared.domain.repository.TrainingRepository
import com.example.aihighpulse.shared.domain.model.Workout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class HomeState(
    val workouts: List<Workout> = emptyList(),
    val todaySets: Int = 0,
    val totalVolume: Int = 0,
    val loading: Boolean = false,
)

class HomeViewModel(
    private val trainingRepository: TrainingRepository,
    private val generateTrainingPlan: GenerateTrainingPlan,
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState(loading = true))
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            trainingRepository.observeWorkouts().collectLatest { list ->
                val sets = list.firstOrNull()?.sets?.size ?: 0
                val volume = list.firstOrNull()?.sets?.sumOf { ((it.weightKg ?: 0.0) * it.reps).toInt() } ?: 0
                _state.value = HomeState(workouts = list, todaySets = sets, totalVolume = volume, loading = false)
            }
        }
        viewModelScope.launch {
            if (_state.value.workouts.isEmpty()) {
                runCatching { generateTrainingPlan(0) }
            }
        }
    }
}
