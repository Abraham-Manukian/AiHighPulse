package com.example.aihighpulse.ui.screens

import androidx.compose.runtime.Composable
import com.example.aihighpulse.ui.vm.PaywallViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
actual fun rememberPaywallPresenter(): PaywallPresenter = koinViewModel<PaywallViewModel>()
