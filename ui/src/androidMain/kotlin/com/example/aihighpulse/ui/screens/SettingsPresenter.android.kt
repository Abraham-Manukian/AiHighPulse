package com.example.aihighpulse.ui.screens

import androidx.compose.runtime.Composable
import com.example.aihighpulse.ui.vm.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
actual fun rememberSettingsPresenter(): SettingsPresenter = koinViewModel<SettingsViewModel>()
