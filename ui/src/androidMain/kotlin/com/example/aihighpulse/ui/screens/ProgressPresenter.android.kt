package com.example.aihighpulse.ui.screens

import androidx.compose.runtime.Composable
import com.example.aihighpulse.ui.vm.ProgressViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
actual fun rememberProgressPresenter(): ProgressPresenter = koinViewModel<ProgressViewModel>()
