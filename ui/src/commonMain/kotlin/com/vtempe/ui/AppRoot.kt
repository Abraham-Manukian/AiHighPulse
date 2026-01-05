package com.vtempe.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.aihighpulse.core.designsystem.icons.AiIcons
import com.example.aihighpulse.ui.navigation.Routes
import com.example.aihighpulse.ui.navigation.nutritionDetail
import com.example.aihighpulse.ui.screens.ChatScreen
import com.example.aihighpulse.ui.screens.EditProfileScreen
import com.example.aihighpulse.ui.screens.HomeScreen
import com.example.aihighpulse.ui.screens.NutritionDetailScreen
import com.example.aihighpulse.ui.screens.NutritionScreen
import com.example.aihighpulse.ui.screens.OnboardingScreen
import com.example.aihighpulse.ui.screens.PaywallScreen
import com.example.aihighpulse.ui.screens.ProgressScreen
import com.example.aihighpulse.ui.screens.SettingsScreen
import com.example.aihighpulse.ui.screens.ShoppingListScreen
import com.example.aihighpulse.ui.screens.SleepScreen
import com.example.aihighpulse.ui.screens.SplashScreen
import com.example.aihighpulse.ui.screens.WorkoutScreen
import com.example.aihighpulse.ui.theme.AiHighPulseTheme
import com.example.aihighpulse.ui.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppRoot() {
    AiHighPulseTheme {
        var currentRoute by remember { mutableStateOf(Routes.Splash) }
        val tabRoutes = bottomDestinations.map { it.route }

        Scaffold(
            bottomBar = {
                if (currentRoute in tabRoutes) {
                    BottomTabs(
                        selectedRoute = currentRoute,
                        onRouteSelected = { currentRoute = it }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AppNavigationHost(
                    currentRoute = currentRoute,
                    onNavigate = { destination -> currentRoute = destination }
                )
            }
        }
    }
}

@Composable
private fun AppNavigationHost(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    when (currentRoute) {
        Routes.Splash -> SplashScreen(onReady = onNavigate)
        Routes.Onboarding -> OnboardingScreen(onDone = { onNavigate(Routes.Home) })
        Routes.Home -> HomeScreen(onNavigate = onNavigate)
        Routes.Workout -> WorkoutScreen()
        Routes.Nutrition -> NutritionScreen(onOpenMeal = { day, index ->
            onNavigate(Routes.nutritionDetail(day, index))
        })
        Routes.Sleep -> SleepScreen()
        Routes.Progress -> ProgressScreen()
        Routes.Paywall -> PaywallScreen()
        Routes.Settings -> SettingsScreen(onEditProfile = { onNavigate(Routes.EditProfile) })
        Routes.EditProfile -> EditProfileScreen(onDone = { onNavigate(Routes.Settings) })
        Routes.Chat -> ChatScreen()
        Routes.ShoppingList -> ShoppingListScreen(onBack = { onNavigate(Routes.Nutrition) })
        else -> {
            // Handle simple detail route string like nutrition_detail/Mon/0
            if (currentRoute.startsWith("nutrition_detail")) {
                val segments = currentRoute.split("/")
                val day = segments.getOrNull(1) ?: "Mon"
                val idx = segments.getOrNull(2)?.toIntOrNull() ?: 0
                NutritionDetailScreen(day = day, index = idx, onBack = { onNavigate(Routes.Nutrition) })
            } else {
                HomeScreen(onNavigate = onNavigate)
            }
        }
    }
}

@Composable
private fun BottomTabs(
    selectedRoute: String,
    onRouteSelected: (String) -> Unit
) {
    NavigationBar {
        bottomDestinations.forEach { destination ->
            val label = stringResource(destination.labelRes)
            NavigationBarItem(
                selected = destination.route == selectedRoute,
                onClick = { onRouteSelected(destination.route) },
                icon = { Icon(imageVector = destination.icon, contentDescription = label) },
                label = { Text(label) }
            )
        }
    }
}

private data class BottomDestination(
    val route: String,
    val labelRes: StringResource,
    val icon: ImageVector
)

private val bottomDestinations = listOf(
    BottomDestination(Routes.Home, Res.string.nav_home, AiIcons.Dashboard),
    BottomDestination(Routes.Workout, Res.string.nav_workout, AiIcons.Strength),
    BottomDestination(Routes.Nutrition, Res.string.nav_nutrition, AiIcons.Nutrition),
    BottomDestination(Routes.Sleep, Res.string.nav_sleep, AiIcons.Sleep),
    BottomDestination(Routes.Progress, Res.string.nav_progress, AiIcons.Progress)
)
