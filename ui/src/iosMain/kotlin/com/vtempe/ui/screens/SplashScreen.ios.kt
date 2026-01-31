package com.vtempe.ui.screens

import com.vtempe.shared.domain.repository.ProfileRepository
import com.vtempe.shared.domain.usecase.EnsureCoachData
import com.vtempe.shared.data.di.KoinProvider
import com.vtempe.ui.navigation.Routes

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

