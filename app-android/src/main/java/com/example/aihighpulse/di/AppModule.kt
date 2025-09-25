package com.example.aihighpulse.di

import com.example.aihighpulse.billing.AndroidPurchasesRepository
import com.example.aihighpulse.shared.domain.repository.PurchasesRepository
import com.example.aihighpulse.ui.vm.*
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
        // Bump DB name to recreate schema including new tables (dev only). TODO: add proper migrations.
        single<SqlDriver> { AndroidSqliteDriver(AppDatabase.Schema, androidContext(), "app_v2.db") }
        single { AppDatabase(get()) }

        // ViewModels
        viewModel { OnboardingViewModel(get()) }
        viewModel { HomeViewModel(get(), get()) }
        viewModel { WorkoutViewModel(get(), get(), get()) }
        viewModel { NutritionViewModel(get()) }
        viewModel { SleepViewModel(get(), get()) }
        viewModel { ProgressViewModel(get()) }
        viewModel { PaywallViewModel(get()) }
        viewModel { SettingsViewModel(get()) }
        viewModel { com.example.aihighpulse.ui.vm.ChatViewModel(get()) }
    }
}
