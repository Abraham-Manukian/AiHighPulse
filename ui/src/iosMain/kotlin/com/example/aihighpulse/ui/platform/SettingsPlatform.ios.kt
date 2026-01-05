package com.example.aihighpulse.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

private object IosSettingsPlatformActions : SettingsPlatformActions {
    override fun restartApp() {}
}

@Composable
actual fun rememberSettingsPlatformActions(): SettingsPlatformActions =
    remember { IosSettingsPlatformActions }
