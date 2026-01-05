package com.example.aihighpulse.ui.screens

import com.example.aihighpulse.shared.domain.repository.ProfileRepository
import com.example.aihighpulse.shared.domain.usecase.EnsureCoachData
import com.example.aihighpulse.shared.data.di.KoinProvider
import com.example.aihighpulse.ui.navigation.Routes

actual suspend fun determineStartDestination(): String {
    val koin = requireNotNull(KoinProvider.koin) { "Koin is not started" }
    val repo: ProfileRepository = koin.get<ProfileRepository>()
    val ensureCoachData: EnsureCoachData = koin.get<EnsureCoachData>()
    val profile = runCatching { repo.getProfile() }.getOrNull()
    return if (profile != null) {
        runCatching { ensureCoachData() }
        Routes.Home
    } else {
        Routes.Onboarding
    }
}
