package com.vtempe.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtempe.shared.domain.model.Goal
import com.vtempe.shared.domain.model.Profile
import com.vtempe.shared.domain.model.Sex
import com.vtempe.shared.domain.repository.AdviceRepository
import com.vtempe.shared.domain.repository.ProfileRepository
import com.vtempe.ui.screens.SleepPresenter
import com.vtempe.ui.screens.SleepState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SleepViewModel(
    private val adviceRepository: AdviceRepository,
    private val profileRepository: ProfileRepository,
) : ViewModel(), SleepPresenter {
    private val _state = MutableStateFlow(SleepState())
    override val state: StateFlow<SleepState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            adviceRepository.observeAdvice("sleep").collect { advice ->
                _state.value =
                    _state.value.copy(tips = advice.messages, disclaimer = advice.disclaimer)
            }
        }
        viewModelScope.launch {
            val profile =
                profileRepository.getProfile() ?: Profile("local", 28, Sex.MALE, 178, 75.0, Goal.MAINTAIN, 3)
            runCatching { adviceRepository.getAdvice(profile, mapOf("topic" to "sleep")) }
        }
    }

    override fun sync() {
        viewModelScope.launch {
            _state.value = _state.value.copy(syncing = true)
            kotlinx.coroutines.delay(800)
            _state.value = _state.value.copy(syncing = false)
        }
    }
}

