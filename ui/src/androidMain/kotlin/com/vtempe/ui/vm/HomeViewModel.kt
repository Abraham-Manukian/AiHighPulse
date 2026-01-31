package com.vtempe.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtempe.shared.domain.repository.TrainingRepository
import com.vtempe.shared.domain.usecase.EnsureCoachData
import com.vtempe.ui.screens.HomePresenter
import com.vtempe.ui.screens.HomeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(
    private val trainingRepository: TrainingRepository,
    private val ensureCoachData: EnsureCoachData,
) : ViewModel(), HomePresenter {
    private val _state = MutableStateFlow(HomeState(loading = true))
    override val state: StateFlow<HomeState> = _state.asStateFlow()

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

