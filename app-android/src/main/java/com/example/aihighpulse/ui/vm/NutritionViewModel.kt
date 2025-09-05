package com.example.aihighpulse.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aihighpulse.shared.domain.model.NutritionPlan
import com.example.aihighpulse.shared.domain.usecase.GenerateNutritionPlan
import com.example.aihighpulse.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NutritionState(
    val ui: UiState<NutritionPlan> = UiState.Loading,
    val selectedDay: String = "Mon",
)

class NutritionViewModel(
    private val generateNutritionPlan: GenerateNutritionPlan
) : ViewModel() {
    private val _state = MutableStateFlow(NutritionState())
    val state: StateFlow<NutritionState> = _state.asStateFlow()

    init { refresh() }

    fun refresh(weekIndex: Int = 0) {
        viewModelScope.launch {
            _state.value = _state.value.copy(ui = UiState.Loading)
            val result = runCatching { generateNutritionPlan(weekIndex) }
            _state.value = result.fold(
                onSuccess = { _state.value.copy(ui = UiState.Data(it)) },
                onFailure = { _state.value.copy(ui = UiState.Error(it.message)) }
            )
        }
    }

    fun selectDay(day: String) { _state.value = _state.value.copy(selectedDay = day) }
}
