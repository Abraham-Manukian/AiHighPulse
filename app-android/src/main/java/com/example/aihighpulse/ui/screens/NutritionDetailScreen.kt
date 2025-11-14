package com.example.aihighpulse.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aihighpulse.R
import com.example.aihighpulse.core.designsystem.theme.AiGradients
import com.example.aihighpulse.core.designsystem.components.BrandScreen
import com.example.aihighpulse.ui.state.UiState
import com.example.aihighpulse.ui.vm.NutritionViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.background

@Composable
fun NutritionDetailScreen(day: String, index: Int, onBack: () -> Unit) {
    val vm: NutritionViewModel = koinViewModel()
    val s by vm.state.collectAsState()
    val ui = s.ui
    BrandScreen(Modifier.fillMaxSize().padding(16.dp)) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(stringResource(R.string.nutrition_detail_title), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        Button(onClick = onBack) { Text(stringResource(R.string.action_back)) }
        when (ui) {
            UiState.Loading -> Text(stringResource(R.string.loading))
            is UiState.Error -> Text(stringResource(R.string.nutrition_error_title), color = MaterialTheme.colorScheme.error)
            is UiState.Data -> {
                val meals = ui.value.mealsByDay[day].orEmpty()
                val meal = meals.getOrNull(index)
                if (meal == null) {
                    Text(stringResource(R.string.nutrition_detail_missing))
                } else {
                    AnimatedVisibility(visible = true, enter = fadeIn(tween(300)) + slideInVertically(initialOffsetY = { it / 6 }, animationSpec = tween(300))) {
                        Card {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(meal.name, style = MaterialTheme.typography.titleMedium)
                                Text("Kcal: ${meal.kcal}")
                                Text("Protein: ${meal.macros.proteinGrams} g  Fat: ${meal.macros.fatGrams} g  Carbs: ${meal.macros.carbsGrams} g")
                                Text(stringResource(R.string.nutrition_ingredients))
                                meal.ingredients.forEach { ing -> Text("• $ing") }
                            }
                        }
                    }
                }
            }
        }
        }
    }
}

