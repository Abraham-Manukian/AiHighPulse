package com.vtempe.ui.screens

import androidx.compose.runtime.Composable
import com.vtempe.shared.domain.model.Profile
import kotlinx.coroutines.flow.StateFlow

data class SettingsState(
    val profile: Profile? = null,
    val saving: Boolean = false
)

interface SettingsPresenter {
    val state: StateFlow<SettingsState>
    fun refresh()
    fun save(profile: Profile)
    fun reset(onDone: () -> Unit)
    fun setUnits(units: String)
    fun setLanguage(tag: String?)
}

@Composable
expect fun rememberSettingsPresenter(): SettingsPresenter

