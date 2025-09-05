package com.example.aihighpulse.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aihighpulse.shared.domain.model.Profile
import com.example.aihighpulse.shared.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsState(
    val profile: Profile? = null,
    val saving: Boolean = false
)

class SettingsViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        viewModelScope.launch { _state.value = SettingsState(profileRepository.getProfile()) }
    }

    fun save(profile: Profile) {
        viewModelScope.launch {
            _state.value = _state.value.copy(saving = true)
            profileRepository.upsertProfile(profile)
            _state.value = SettingsState(profile, saving = false)
        }
    }
}
