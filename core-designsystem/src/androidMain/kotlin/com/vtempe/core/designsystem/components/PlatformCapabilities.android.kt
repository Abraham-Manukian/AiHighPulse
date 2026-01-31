package com.vtempe.core.designsystem.components

import android.os.Build

internal actual fun isAdvancedBlurAvailable(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

