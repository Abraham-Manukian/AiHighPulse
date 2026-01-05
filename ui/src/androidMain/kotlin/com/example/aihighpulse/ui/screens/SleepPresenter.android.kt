package com.example.aihighpulse.ui.screens

import androidx.compose.runtime.Composable
import com.example.aihighpulse.ui.vm.SleepViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
actual fun rememberSleepPresenter(): SleepPresenter = koinViewModel<SleepViewModel>()
