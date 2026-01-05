package com.example.aihighpulse.ui.screens

import org.koin.java.KoinJavaComponent.getKoin
import com.example.aihighpulse.shared.domain.repository.ProfileRepository
import com.example.aihighpulse.shared.domain.usecase.EnsureCoachData
import com.example.aihighpulse.ui.navigation.Routes

actual suspend fun determineStartDestination(): String {
    val repo: ProfileRepository = getKoin().get()
    val ensureCoachData: EnsureCoachData = getKoin().get()
    val profile = runCatching { repo.getProfile() }.getOrNull()
    return if (profile != null) {
        runCatching { ensureCoachData() }
        Routes.Home
    } else {
        Routes.Onboarding
    }
}
