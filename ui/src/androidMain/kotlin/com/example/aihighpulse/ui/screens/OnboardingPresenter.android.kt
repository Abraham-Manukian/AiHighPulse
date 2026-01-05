package com.example.aihighpulse.ui.screens

import androidx.compose.runtime.Composable
import com.example.aihighpulse.ui.vm.OnboardingViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
actual fun rememberOnboardingPresenter(): OnboardingPresenter =
    koinViewModel<OnboardingViewModel>()
