package com.example.aihighpulse.ui.screens

import androidx.compose.foundation.background
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import com.example.aihighpulse.core.designsystem.components.PlaceholderScreen
import com.example.aihighpulse.core.designsystem.components.StatChip
import com.example.aihighpulse.core.designsystem.components.RingChart
import com.example.aihighpulse.ui.state.UiState
import com.example.aihighpulse.ui.vm.NutritionViewModel
import com.example.aihighpulse.R
import com.example.aihighpulse.shared.domain.model.Macros

@Composable
fun NutritionScreen(
    onOpenMeal: (day: String, index: Int) -> Unit = { _, _ -> }
) {
    val vm: NutritionViewModel = koinViewModel()
    val s by vm.state.collectAsState()
    val days = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            days.forEach { d ->
                val selected = s.selectedDay == d
                Text(
                    d,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .clickable { vm.selectDay(d) }
                        .background(if (selected) MaterialTheme.colorScheme.primary.copy(0.15f) else Color.Transparent)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
            Spacer(Modifier.weight(1f))
            TextButton(onClick = { vm.refresh() }) { Text(stringResource(R.string.action_refresh)) }
        }
        var tab by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(0) }
        TabRow(selectedTabIndex = tab) {
            Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text(stringResource(R.string.nutrition_tab_menu)) })
            Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text(stringResource(R.string.nutrition_tab_shopping)) })
        }
        Crossfade<UiState<com.example.aihighpulse.shared.domain.model.NutritionPlan>>(targetState = s.ui, label = "nutrition-ui") { st ->
            when (st) {
                UiState.Loading -> PlaceholderScreen(title = stringResource(R.string.nutrition_loading_title), sections = listOf("..."))
                is UiState.Error -> PlaceholderScreen(title = stringResource(R.string.nutrition_error_title), sections = listOf(stringResource(R.string.nutrition_error_hint)))
                is UiState.Data -> {
                val plan = st.value
                if (tab == 1) {
                    // Shopping list tab
                    val items = plan.shoppingList
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(items.size) { i -> Text("• ${items[i]}") }
                    }
                    return@Crossfade
                }
                val meals = plan.mealsByDay[s.selectedDay].orEmpty()
                val proteinDay = meals.sumOf { it.macros.proteinGrams }
                val fatDay = meals.sumOf { it.macros.fatGrams }
                val carbsDay = meals.sumOf { it.macros.carbsGrams }
                val kcalDay = meals.sumOf { it.macros.kcal }
                val allMeals = plan.mealsByDay.values.flatten()
                val proteinWeek = allMeals.sumOf { it.macros.proteinGrams }
                val fatWeek = allMeals.sumOf { it.macros.fatGrams }
                val carbsWeek = allMeals.sumOf { it.macros.carbsGrams }
                val kcalWeek = allMeals.sumOf { it.macros.kcal }
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    item {
                        Card {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(stringResource(R.string.nutrition_macros_chart_title), style = MaterialTheme.typography.titleMedium)
                                RingChart(
                                    values = listOf(
                                        proteinDay.toFloat(),
                                        fatDay.toFloat(),
                                        carbsDay.toFloat()
                                    ),
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.tertiary,
                                        MaterialTheme.colorScheme.secondary
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    StatChip("Protein", "${proteinDay} g")
                                    StatChip("Fat", "${fatDay} g")
                                    StatChip("Carbs", "${carbsDay} g")
                                }
                            }
                        }
                    }
                    item {
                        Card {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(stringResource(R.string.nutrition_day_totals), style = MaterialTheme.typography.titleMedium)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    StatChip("Protein", "${proteinDay} g")
                                    StatChip("Fat", "${fatDay} g")
                                    StatChip("Carbs", "${carbsDay} g")
                                    StatChip("Kcal", kcalDay.toString())
                                }
                            }
                        }
                    }
                    item {
                        Card {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(stringResource(R.string.nutrition_week_totals), style = MaterialTheme.typography.titleMedium)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    StatChip("Protein", "${proteinWeek} g")
                                    StatChip("Fat", "${fatWeek} g")
                                    StatChip("Carbs", "${carbsWeek} g")
                                    StatChip("Kcal", kcalWeek.toString())
                                }
                            }
                        }
                    }
                    itemsIndexed(meals) { idx: Int, meal ->
                        Card(modifier = Modifier.clickable { onOpenMeal(s.selectedDay, idx) }) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(meal.name, style = MaterialTheme.typography.titleMedium)
                                Text("Kcal: ${meal.kcal}  P:${meal.macros.proteinGrams} F:${meal.macros.fatGrams} C:${meal.macros.carbsGrams}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Ingredients: ${meal.ingredients.joinToString()}")
                            }
                        }
                    }
                }
            }
          }
        }
    }
}
