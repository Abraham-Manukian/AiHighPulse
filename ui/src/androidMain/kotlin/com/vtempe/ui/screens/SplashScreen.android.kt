package com.vtempe.ui.screens

import com.vtempe.ui.navigation.Routes

actual suspend fun determineStartDestination(): String {
    return Routes.Onboarding
}