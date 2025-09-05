package com.example.aihighpulse.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aihighpulse.shared.domain.repository.TrainingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class ProgressState(
    val totalWorkouts: Int = 0,
    val totalSets: Int = 0,
    val totalVolume: Int = 0,
)

class ProgressViewModel(
    private val trainingRepository: TrainingRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ProgressState())
    val state: StateFlow<ProgressState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            trainingRepository.observeWorkouts().collectLatest { list ->
                val sets = list.sumOf { it.sets.size }
                val volume = list.sumOf { it.sets.sumOf { s -> ((s.weightKg ?: 0.0) * s.reps).toInt() } }
                _state.value = ProgressState(list.size, sets, volume)
            }
        }
    }
}
