package com.example.aihighpulse.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.aihighpulse.R
import com.example.aihighpulse.core.designsystem.components.PlaceholderScreen
import com.example.aihighpulse.core.designsystem.components.RingChart
import com.example.aihighpulse.core.designsystem.components.StatChip
import com.example.aihighpulse.ui.state.UiState
import com.example.aihighpulse.ui.vm.NutritionViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun NutritionScreen(
    onOpenMeal: (day: String, index: Int) -> Unit = { _, _ -> }
) {
    val vm: NutritionViewModel = koinViewModel()
    val s by vm.state.collectAsState()
    val dayOptions = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")
    val dayLabels = mapOf(
        "Mon" to stringResource(R.string.day_mon_short),
        "Tue" to stringResource(R.string.day_tue_short),
        "Wed" to stringResource(R.string.day_wed_short),
        "Thu" to stringResource(R.string.day_thu_short),
        "Fri" to stringResource(R.string.day_fri_short),
        "Sat" to stringResource(R.string.day_sat_short),
        "Sun" to stringResource(R.string.day_sun_short)
    )

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            dayOptions.forEach { key ->
                val selected = s.selectedDay == key
                Text(
                    dayLabels[key] ?: key,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .clickable { vm.selectDay(key) }
                        .background(if (selected) MaterialTheme.colorScheme.primary.copy(0.15f) else Color.Transparent)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
            Spacer(Modifier.weight(1f))
            Button(onClick = { vm.refresh() }) { Text(stringResource(R.string.action_refresh)) }
        }
        var tab by remember { mutableStateOf(0) }
        TabRow(selectedTabIndex = tab) {
            Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text(stringResource(R.string.nutrition_tab_menu)) })
            Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text(stringResource(R.string.nutrition_tab_shopping)) })
        }
        Crossfade<UiState<com.example.aihighpulse.shared.domain.model.NutritionPlan>>(targetState = s.ui, label = "nutrition-ui") { st ->
            when (st) {
                UiState.Loading -> PlaceholderScreen(title = stringResource(R.string.nutrition_loading_title), sections = listOf("…"))
                is UiState.Error -> PlaceholderScreen(title = stringResource(R.string.nutrition_error_title), sections = listOf(stringResource(R.string.nutrition_error_hint)))
                is UiState.Data -> {
                    val plan = st.value
                    if (tab == 1) {
                        val items = plan.shoppingList
                        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(items.size) { i -> Text(stringResource(R.string.nutrition_shopping_bullet, items[i])) }
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
                                        values = listOf(proteinDay.toFloat(), fatDay.toFloat(), carbsDay.toFloat()),
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.tertiary,
                                            MaterialTheme.colorScheme.secondary
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        StatChip(stringResource(R.string.nutrition_macro_protein), "${proteinDay} g")
                                        StatChip(stringResource(R.string.nutrition_macro_fat), "${fatDay} g")
                                        StatChip(stringResource(R.string.nutrition_macro_carbs), "${carbsDay} g")
                                    }
                                }
                            }
                        }
                        item {
                            Card {
                                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(stringResource(R.string.nutrition_day_totals), style = MaterialTheme.typography.titleMedium)
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        StatChip(stringResource(R.string.nutrition_macro_protein), "${proteinDay} g")
                                        StatChip(stringResource(R.string.nutrition_macro_fat), "${fatDay} g")
                                        StatChip(stringResource(R.string.nutrition_macro_carbs), "${carbsDay} g")
                                        StatChip(stringResource(R.string.nutrition_macro_kcal), kcalDay.toString())
                                    }
                                }
                            }
                        }
                        item {
                            Card {
                                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(stringResource(R.string.nutrition_week_totals), style = MaterialTheme.typography.titleMedium)
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        StatChip(stringResource(R.string.nutrition_macro_protein), "${proteinWeek} g")
                                        StatChip(stringResource(R.string.nutrition_macro_fat), "${fatWeek} g")
                                        StatChip(stringResource(R.string.nutrition_macro_carbs), "${carbsWeek} g")
                                        StatChip(stringResource(R.string.nutrition_macro_kcal), kcalWeek.toString())
                                    }
                                }
                            }
                        }
                        itemsIndexed(meals) { idx, meal ->
                            Card(modifier = Modifier.clickable { onOpenMeal(s.selectedDay, idx) }) {
                                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(meal.name, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        stringResource(
                                            R.string.nutrition_macros_line,
                                            meal.kcal,
                                            meal.macros.proteinGrams,
                                            meal.macros.fatGrams,
                                            meal.macros.carbsGrams
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(stringResource(R.string.nutrition_ingredients) + ": " + meal.ingredients.joinToString())
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
