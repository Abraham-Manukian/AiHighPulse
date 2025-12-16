package com.example.aihighpulse.ios

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/**
 * Точка входа для iOS-приложения.
 * Xcode будет вызывать эту функцию, чтобы получить root UIViewController.
 */
fun MainViewController(): UIViewController {
    return ComposeUIViewController {
        AppIosRoot()
    }
}
