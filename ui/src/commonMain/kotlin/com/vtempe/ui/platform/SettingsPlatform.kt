package com.vtempe.ui.platform

import androidx.compose.runtime.Composable

interface SettingsPlatformActions {
    fun restartApp()
}

@Composable
expect fun rememberSettingsPlatformActions(): SettingsPlatformActions

