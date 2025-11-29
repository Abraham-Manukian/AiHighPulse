package com.example.aihighpulse

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.outlined.KeyboardBackspace
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.aihighpulse.core.designsystem.theme.AiGradients
import com.example.aihighpulse.ui.navigation.Routes
import com.example.aihighpulse.ui.navigation.Routes.bottomNavRoutes
import com.example.aihighpulse.ui.navigation.nutritionDetail
import com.example.aihighpulse.ui.screens.*
import com.example.aihighpulse.ui.theme.AiHighPulseTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AiHighPulseTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) { AppRoot() }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot() {
    val nav = rememberNavController()
    val configuration = LocalConfiguration.current
    val useRail = configuration.smallestScreenWidthDp >= 600

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
    val showBottomBar = currentRoute in bottomNavRoutes


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AiGradients.lavenderMist())
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                if (showChrome) {
                    CenterAlignedTopAppBar(
                        title = { Text(currentTitle) },
                        windowInsets = WindowInsets.statusBars,
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.Transparent,
                            scrolledContainerColor = Color.Transparent,
                            titleContentColor = Color.White,
                            navigationIconContentColor = Color.White,
                            actionIconContentColor = Color.White
                        ),
                        navigationIcon = {
                            IconButton(onClick = { nav.navigate(Routes.Chat) }) {
                                Icon(Icons.Outlined.Chat, contentDescription = stringResource(R.string.nav_chat))
                            }
                        },
                        actions = {
                            if (currentRoute !in bottomNavRoutes){
                                IconButton(onClick = {nav.navigate(Routes.Home)}) {
                                    Icon(Icons.Outlined.KeyboardBackspace, contentDescription = "back")
                                }
                            }
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
            bottomBar = {}
        ) { innerPadding ->
            Row(Modifier.fillMaxSize()) {
                if (showChrome && useRail) {
                    NavigationRail(
                        modifier = Modifier
                            .padding(
                                top = innerPadding.calculateTopPadding(),
                                start = 12.dp,
                                end = 12.dp,
                                bottom = 12.dp
                            )
                            .shadow(10.dp, MaterialTheme.shapes.large, clip = false)
                            .clip(MaterialTheme.shapes.large),
                        containerColor = Color.White.copy(alpha = 0.9f)
                    ) {
                        destinations.forEach { dest ->
                            val selected = currentRoute == dest.route
                            NavigationRailItem(
                                selected = selected,
                                onClick = { if (!selected) navigateSingleTopTo(nav, dest.route) },
                                icon = { Icon(dest.icon, contentDescription = dest.label, modifier = Modifier.size(28.dp)) },
                                label = { Text(dest.label, style = MaterialTheme.typography.bodySmall) },
                                colors = androidx.compose.material3.NavigationRailItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            )
                        }
                    }
                }
                Box(
                    Modifier
                        .padding(
                            top = innerPadding.calculateTopPadding(),
                            start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                            end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
                        )
                        .fillMaxSize()
                ) {
                    AppNavHost(nav)
                }
            }
        }
        if (showBottomBar && showChrome && !useRail) {
            FloatingNavBar(
                destinations = destinations,
                currentRoute = currentRoute,
                onDestinationSelected = { destination ->
                    navigateSingleTopTo(nav, destination.route)
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .imePadding()
            )
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
        composable(Routes.Settings) {
            SettingsScreen(
                onEditProfile = { nav.navigate(Routes.EditProfile) }
            )
        }
        composable(Routes.EditProfile) {
            EditProfileScreen(
                onDone = { nav.popBackStack() }
            )
        }
        composable(Routes.Chat) { ChatScreen() }
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

@Composable
private fun FloatingNavBar(
    destinations: List<TopLevelDestination>,
    currentRoute: String?,
    onDestinationSelected: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val NavBarHeight = 64.dp
    val navShape = RoundedCornerShape(32.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(NavBarHeight)
                .shadow(
                    elevation = 16.dp,
                    shape = navShape,
                    clip = false,
                    ambientColor = Color.Black.copy(alpha = 0.18f),
                    spotColor = Color.Black.copy(alpha = 0.18f)
                )
                .clip(navShape)
        ) {

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.05f))
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .blur(26.dp)
                    .background(Color.White.copy(alpha = 0.45f))
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .border(1.dp, Color.White.copy(alpha = 0.6f), navShape)
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                destinations.forEach { dest ->
                    val selected = currentRoute == dest.route
                    val itemShape = RoundedCornerShape(20.dp)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(itemShape)
                            .background(
                                if (selected) Color(0x4D7C4DFF) else Color.Transparent
                            )
                            .border(
                                width = if (selected) 1.dp else 0.dp,
                                color = Color.White.copy(alpha = if (selected) 0.5f else 0.25f),
                                shape = itemShape
                            )
                            .clickable { if (!selected) onDestinationSelected(dest) }
                            .padding(vertical = 6.dp, horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(28.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (selected) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .blur(
                                            radius = 20.dp,
                                            edgeTreatment = BlurredEdgeTreatment.Unbounded
                                        )
                                        .background(Color(0x807C4DFF))
                                )
                            }
                            Icon(
                                dest.icon,
                                contentDescription = dest.label,
                                tint = if (selected) Color.White else Color(0xFF8F87BD),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Text(
                            dest.label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                shadow = Shadow(
                                    color = Color.Black.copy(alpha = 0.3f),
                                    offset = Offset(0f, 1f),
                                    blurRadius = 4f
                                )
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = if (selected) Color.White else Color(0xFFB2ACD6)
                        )
                    }
                }
            }
        }
    }

}
