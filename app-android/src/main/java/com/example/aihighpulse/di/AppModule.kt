package com.example.aihighpulse.di

import com.example.aihighpulse.ui.vm.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object AppModule {
    val module = module {
        viewModel { OnboardingViewModel(get()) }
        viewModel { HomeViewModel(get(), get()) }
        viewModel { WorkoutViewModel(get(), get(), get()) }
        viewModel { NutritionViewModel(get()) }
        viewModel { SleepViewModel(get(), get()) }
        viewModel { ProgressViewModel(get()) }
        viewModel { PaywallViewModel(get()) }
        viewModel { SettingsViewModel(get()) }
    }
}

