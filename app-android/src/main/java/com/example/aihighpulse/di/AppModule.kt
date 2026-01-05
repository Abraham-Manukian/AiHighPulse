package com.example.aihighpulse.di

import com.example.aihighpulse.billing.AndroidPurchasesRepository
import com.example.aihighpulse.shared.domain.repository.PurchasesRepository
import com.example.aihighpulse.ui.vm.ChatViewModel
import com.example.aihighpulse.ui.vm.HomeViewModel
import com.example.aihighpulse.ui.vm.NutritionViewModel
import com.example.aihighpulse.ui.vm.OnboardingViewModel
import com.example.aihighpulse.ui.vm.PaywallViewModel
import com.example.aihighpulse.ui.vm.ProgressViewModel
import com.example.aihighpulse.ui.vm.SettingsViewModel
import com.example.aihighpulse.ui.vm.SleepViewModel
import com.example.aihighpulse.ui.vm.WorkoutViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import app.cash.sqldelight.db.SqlDriver
import com.example.aihighpulse.shared.db.AppDatabase

object AppModule {
    val module = module {
        // Android-specific DI overrides
        single<PurchasesRepository> { AndroidPurchasesRepository(androidContext()) }

        // SQLDelight database
        single<SqlDriver> { AndroidSqliteDriver(AppDatabase.Schema, androidContext(), "app_v2.db") }
        single { AppDatabase(get()) }

        // ViewModels (Android implementations live in :ui)
        viewModel { OnboardingViewModel(get(), get(), get()) }
        viewModel { HomeViewModel(get(), get()) }
        viewModel { WorkoutViewModel(get(), get(), get()) }
        viewModel { NutritionViewModel(get(), get()) }
        viewModel { SleepViewModel(get(), get()) }
        viewModel { ProgressViewModel(get()) }
        viewModel { PaywallViewModel(get()) }
        viewModel { SettingsViewModel(get(), get()) }
        viewModel { ChatViewModel(get()) }
    }
}
