package com.example.aihighpulse.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aihighpulse.shared.domain.model.Workout
import com.example.aihighpulse.shared.domain.repository.TrainingRepository
import com.example.aihighpulse.shared.domain.usecase.EnsureCoachData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class HomeState(
    val workouts: List<Workout> = emptyList(),
    val todaySets: Int = 0,
    val totalVolume: Int = 0,
    val sleepMinutes: Int = 7 * 60 + 20,
    val loading: Boolean = false,
)

class HomeViewModel(
    private val trainingRepository: TrainingRepository,
    private val ensureCoachData: EnsureCoachData,
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState(loading = true))
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            runCatching { ensureCoachData() }
        }
        viewModelScope.launch {
            trainingRepository.observeWorkouts().collectLatest { list ->
                val sets = list.firstOrNull()?.sets?.size ?: 0
                val volume = list.firstOrNull()?.sets?.sumOf { ((it.weightKg ?: 0.0) * it.reps).toInt() } ?: 0
                val sleepMinutes = _state.value.sleepMinutes
                _state.value = HomeState(
                    workouts = list,
                    todaySets = sets,
                    totalVolume = volume,
                    sleepMinutes = sleepMinutes,
                    loading = false
                )
            }
        }
    }
}
