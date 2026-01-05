package com.example.aihighpulse.ui.screens

import androidx.compose.runtime.Composable
import com.example.aihighpulse.ui.vm.WorkoutViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
actual fun rememberWorkoutPresenter(): WorkoutPresenter = koinViewModel<WorkoutViewModel>()
