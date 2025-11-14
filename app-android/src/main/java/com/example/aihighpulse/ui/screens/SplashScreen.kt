package com.example.aihighpulse.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.get
import com.example.aihighpulse.shared.domain.repository.ProfileRepository
import com.example.aihighpulse.shared.domain.usecase.EnsureCoachData
import com.example.aihighpulse.ui.navigation.Routes
import com.example.aihighpulse.core.designsystem.theme.AiGradients

@Composable
fun SplashScreen(onReady: (String) -> Unit) {
    val repo: ProfileRepository = get()
    val ensureCoachData: EnsureCoachData = get()

    val decided = remember { mutableStateOf(false) }
    val scale = remember { Animatable(0.8f) }
    LaunchedEffect(Unit) {
        scale.animateTo(1f, animationSpec = tween(600))
        val profile = runCatching { repo.getProfile() }.getOrNull()
        val destination = if (profile != null) {
            runCatching { ensureCoachData() }.onFailure { it.printStackTrace() }
            Routes.Home
        } else {
            Routes.Onboarding
        }
        onReady(destination)
        decided.value = true
    }
    if (!decided.value) {
        Box(Modifier.fillMaxSize().background(AiGradients.purpleWave()), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.size(42.dp).scale(scale.value), color = Color.White)
        }
    }
}


