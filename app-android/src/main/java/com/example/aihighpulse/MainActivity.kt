package com.example.aihighpulse

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.example.aihighpulse.R
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.aihighpulse.ui.navigation.Routes
import com.example.aihighpulse.ui.navigation.nutritionDetail
import com.example.aihighpulse.ui.screens.*
import com.example.aihighpulse.ui.theme.AiHighPulseTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AiHighPulseTheme {
                Surface(modifier = Modifier.fillMaxSize()) { AppRoot() }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot() {
    val nav = rememberNavController()

    val destinations = listOf(
        TopLevelDestination(Routes.Home, stringResource(R.string.nav_home), Icons.Filled.Home),
        TopLevelDestination(Routes.Workout, stringResource(R.string.nav_workout), Icons.Filled.FitnessCenter),
        TopLevelDestination(Routes.Nutrition, stringResource(R.string.nav_nutrition), Icons.Filled.LunchDining),
        TopLevelDestination(Routes.Sleep, stringResource(R.string.nav_sleep), Icons.Filled.Bedtime),
        TopLevelDestination(Routes.Progress, stringResource(R.string.nav_progress), Icons.Filled.BarChart),
    )

    val backStackEntry by nav.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val currentTitle = destinations.firstOrNull { it.route == currentRoute }?.label ?: "AiHighPulse"
    val showChrome = currentRoute != Routes.Onboarding

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val useRail = maxWidth >= 600.dp

        Scaffold(
            topBar = {
                if (showChrome) {
                    CenterAlignedTopAppBar(
                        title = { Text(currentTitle) },
                        actions = {
                            IconButton(onClick = { nav.navigate(Routes.Paywall) }) {
                                Icon(Icons.Outlined.Star, contentDescription = "Pro")
                            }
                            IconButton(onClick = { nav.navigate(Routes.Settings) }) {
                                Icon(Icons.Outlined.Settings, contentDescription = "Settings")
                            }
                        }
                    )
                }
            },
            bottomBar = {
                if (showChrome && !useRail) {
                    NavigationBar {
                        destinations.forEach { dest ->
                            val selected = currentRoute == dest.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = { if (!selected) navigateSingleTopTo(nav, dest.route) },
                                icon = { Icon(dest.icon, contentDescription = dest.label, modifier = Modifier.size(28.dp)) },
                                label = { Text(dest.label, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                            )
                        }
                    }
                }
            }
        ) { padding ->
            Row(Modifier.fillMaxSize()) {
                if (showChrome && useRail) {
                    NavigationRail(modifier = Modifier.padding(top = padding.calculateTopPadding())) {
                        destinations.forEach { dest ->
                            val selected = currentRoute == dest.route
                            NavigationRailItem(
                                selected = selected,
                                onClick = { if (!selected) navigateSingleTopTo(nav, dest.route) },
                                icon = { Icon(dest.icon, contentDescription = dest.label) },
                                label = { Text(dest.label) }
                            )
                        }
                    }
                }
                Box(Modifier.padding(padding).fillMaxSize()) {
                    AppNavHost(nav)
                }
            }
        }
    }
}

@Composable
fun AppNavHost(nav: NavHostController) {
    NavHost(navController = nav, startDestination = Routes.Splash) {
        composable(Routes.Splash) {
            SplashScreen(onReady = { start ->
                nav.navigate(start) {
                    popUpTo(Routes.Splash) { inclusive = true }
                }
            })
        }
        composable(Routes.Onboarding) {
            OnboardingScreen(onDone = {
                nav.navigate(Routes.Home) {
                    popUpTo(Routes.Onboarding) { inclusive = true }
                }
            })
        }
        composable(Routes.Home) { HomeScreen(onNavigate = { nav.navigate(it) }) }
        composable(Routes.Workout) { WorkoutScreen() }
        composable(Routes.Nutrition) {
            NutritionScreen(
                onOpenMeal = { day, index -> nav.navigate(Routes.nutritionDetail(day, index)) }
            )
        }
        composable(Routes.NutritionDetail) { backStack ->
            val day = backStack.arguments?.getString("day") ?: "Mon"
            val index = backStack.arguments?.getString("index")?.toIntOrNull() ?: 0
            NutritionDetailScreen(day = day, index = index, onBack = { nav.popBackStack() })
        }
        composable(Routes.ShoppingList) { ShoppingListScreen(onBack = { nav.popBackStack() }) }
        composable(Routes.Sleep) { SleepScreen() }
        composable(Routes.Progress) { ProgressScreen() }
        composable(Routes.Paywall) { PaywallScreen() }
        composable(Routes.Settings) { SettingsScreen() }
    }
}

fun navigateSingleTopTo(nav: NavHostController, route: String) {
    nav.navigate(route) {
        popUpTo(Routes.Home) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

data class TopLevelDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
)
