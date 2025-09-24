package com.example.aihighpulse.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.get
import com.example.aihighpulse.shared.domain.repository.ProfileRepository
import com.example.aihighpulse.ui.navigation.Routes

@Composable
fun SplashScreen(onReady: (String) -> Unit) {
    val repo: ProfileRepository = get()
    val decided = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        val dest = if (runCatching { repo.getProfile() }.getOrNull() != null) Routes.Home else Routes.Onboarding
        onReady(dest)
        decided.value = true
    }
    if (!decided.value) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

