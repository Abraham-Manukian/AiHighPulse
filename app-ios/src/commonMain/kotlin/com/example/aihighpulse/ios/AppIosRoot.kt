package com.example.aihighpulse.ios

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.aihighpulse.shared.domain.usecase.AnalyticsEvents

@Composable
fun AppIosRoot() {
    MaterialTheme {
        // Placeholder: mirror Android navigation; host via Compose UI for iOS
        // In a real app, integrate with UIKit/Compose WindowScene
        IosPlaceholder()
    }
}

@Composable
private fun IosPlaceholder() {
    androidx.compose.material.Text("Compose for iOS placeholder UI. Implement Nav & Screens.")
}

