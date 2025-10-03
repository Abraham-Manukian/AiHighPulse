package com.example.aihighpulse.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aihighpulse.shared.domain.model.NutritionPlan
import com.example.aihighpulse.shared.domain.repository.NutritionRepository
import com.example.aihighpulse.shared.domain.usecase.EnsureCoachData
import com.example.aihighpulse.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class NutritionState(
    val ui: UiState<NutritionPlan> = UiState.Loading,
    val selectedDay: String = "Mon",
)

class NutritionViewModel(
    private val ensureCoachData: EnsureCoachData,
    private val nutritionRepository: NutritionRepository
) : ViewModel() {
    private val _state = MutableStateFlow(NutritionState())
    val state: StateFlow<NutritionState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            nutritionRepository.observePlan().collect { plan ->
                if (plan != null) {
                    _state.value = _state.value.copy(ui = UiState.Data(plan))
                } else if (_state.value.ui !is UiState.Data) {
                    _state.value = _state.value.copy(ui = UiState.Loading)
                }
            }
        }
        refresh(force = false)
    }

    fun refresh(weekIndex: Int = 0, force: Boolean = true) {
        viewModelScope.launch {
            if (_state.value.ui !is UiState.Data) {
                _state.value = _state.value.copy(ui = UiState.Loading)
            }
            val result = runCatching { ensureCoachData(weekIndex, force = force) }
            val success = result.getOrDefault(false)
            if (result.isFailure || (!success && _state.value.ui !is UiState.Data)) {
                val message = result.exceptionOrNull()?.message ?: "Unable to load plan"
                _state.value = _state.value.copy(ui = UiState.Error(message))
            }
        }
    }

    fun selectDay(day: String) { _state.value = _state.value.copy(selectedDay = day) }
}


