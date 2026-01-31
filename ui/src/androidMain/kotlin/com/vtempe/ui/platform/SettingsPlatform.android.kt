package com.vtempe.ui.platform

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

private class AndroidSettingsPlatformActions(
    private val activity: Activity?
) : SettingsPlatformActions {
    override fun restartApp() {
        activity?.recreate()
    }
}

@Composable
actual fun rememberSettingsPlatformActions(): SettingsPlatformActions {
    val context = LocalContext.current
    val activity = context as? Activity
    return remember(activity) { AndroidSettingsPlatformActions(activity) }
}

