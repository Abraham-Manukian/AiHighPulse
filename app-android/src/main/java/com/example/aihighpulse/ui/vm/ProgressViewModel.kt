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
    // Monday..Sunday volumes
    val weeklyVolumes: List<Int> = listOf(0,0,0,0,0,0,0),
    val weightSeries: List<Float> = emptyList(),
    val caloriesSeries: List<Int> = emptyList(),
    val sleepHoursWeek: List<Int> = listOf(7,7,6,8,7,9,8),
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
                val daily = IntArray(7) { 0 }
                list.forEach { w ->
                    val v = w.sets.sumOf { s -> ((s.weightKg ?: 0.0) * s.reps).toInt() }
                    // kotlinx.datetime DayOfWeek has Monday=1..Sunday=7
                    val index = (w.date.dayOfWeek.ordinal) // Monday=0 .. Sunday=6
                    daily[index] = daily[index] + v
                }
                val vols = daily.toList()
                val baseWeight = 78f
                val weight = (0 until 14).map { i -> baseWeight + kotlin.random.Random(i).nextInt(-2, 3) * 0.5f }
                val calories = (0 until 7).map { 2300 + (vols.getOrNull(it) ?: 0) * 2 }
                _state.value = ProgressState(list.size, sets, volume, vols, weight, calories, sleepHoursWeek = listOf(7,7,6,8,7,9,8))
            }
        }
    }
}
