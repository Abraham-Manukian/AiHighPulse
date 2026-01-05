package com.example.aihighpulse.ui.platform

import androidx.compose.runtime.Composable

interface SettingsPlatformActions {
    fun restartApp()
}

@Composable
expect fun rememberSettingsPlatformActions(): SettingsPlatformActions
