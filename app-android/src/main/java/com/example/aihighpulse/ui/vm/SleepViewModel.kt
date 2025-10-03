package com.example.aihighpulse.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aihighpulse.shared.domain.model.Goal
import com.example.aihighpulse.shared.domain.model.Profile
import com.example.aihighpulse.shared.domain.model.Sex
import com.example.aihighpulse.shared.domain.repository.AdviceRepository
import com.example.aihighpulse.shared.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


data class SleepState(
    val tips: List<String> = listOf("Sleep 7-9 hours", "Keep a consistent schedule", "Avoid caffeine after 16:00"),
    val weeklyHours: List<Int> = listOf(7, 7, 6, 8, 7, 9, 8),
    val syncing: Boolean = false,
    val disclaimer: String? = "Not medical advice"
)

class SleepViewModel(
    private val adviceRepository: AdviceRepository,
    private val profileRepository: ProfileRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(SleepState())
    val state: StateFlow<SleepState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            adviceRepository.observeAdvice("sleep").collect { advice ->
                _state.value = _state.value.copy(tips = advice.messages, disclaimer = advice.disclaimer)
            }
        }
        viewModelScope.launch {
            val profile = profileRepository.getProfile() ?: Profile("local", 28, Sex.MALE, 178, 75.0, Goal.MAINTAIN, 3)
            runCatching { adviceRepository.getAdvice(profile, mapOf("topic" to "sleep")) }
        }
    }

    fun sync() {
        viewModelScope.launch {
            _state.value = _state.value.copy(syncing = true)
            // TODO integrate Health Connect / HealthKit
            kotlinx.coroutines.delay(800)
            _state.value = _state.value.copy(syncing = false)
        }
    }
}
