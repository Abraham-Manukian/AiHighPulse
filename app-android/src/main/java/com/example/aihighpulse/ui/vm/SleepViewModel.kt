package com.example.aihighpulse.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aihighpulse.shared.domain.model.*
import com.example.aihighpulse.shared.domain.repository.AdviceRepository
import com.example.aihighpulse.shared.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SleepState(val tips: List<String> = listOf("Ложитесь в одно и то же время", "Темнота и прохлада в спальне"))

class SleepViewModel(
    private val adviceRepository: AdviceRepository,
    private val profileRepository: ProfileRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(SleepState())
    val state: StateFlow<SleepState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val profile = profileRepository.getProfile() ?: Profile("local", 28, Sex.MALE, 178, 75.0, Goal.MAINTAIN, 3)
            val advice = runCatching { adviceRepository.getAdvice(profile, mapOf("topic" to "sleep")) }.getOrNull()
            if (advice != null) _state.value = SleepState(advice.messages)
        }
    }
}
