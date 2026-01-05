package com.example.aihighpulse.ui.screens

import androidx.compose.runtime.Composable
import com.example.aihighpulse.ui.vm.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
actual fun rememberHomePresenter(): HomePresenter = koinViewModel<HomeViewModel>()
