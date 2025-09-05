package com.example.aihighpulse.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import com.example.aihighpulse.core.designsystem.components.PlaceholderScreen
import com.example.aihighpulse.ui.state.UiState
import com.example.aihighpulse.ui.vm.NutritionViewModel

@Composable
fun NutritionScreen() {
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
            TextButton(onClick = { vm.refresh() }) { Text("Обновить") }
        }
        when (val ui = s.ui) {
            UiState.Loading -> PlaceholderScreen(title = "Загрузка меню", sections = listOf("..."))
            is UiState.Error -> PlaceholderScreen(title = ui.message ?: "Ошибка загрузки", sections = listOf("Попробуйте обновить"))
            is UiState.Data -> {
                val meals = ui.value.mealsByDay[s.selectedDay].orEmpty()
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(meals) { meal ->
                        Card {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(meal.name, style = MaterialTheme.typography.titleMedium)
                                Text("Ккал: ${'$'}{meal.kcal}  Б:${'$'}{meal.macros.proteinGrams} Ж:${'$'}{meal.macros.fatGrams} У:${'$'}{meal.macros.carbsGrams}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Ингредиенты: ${'$'}{meal.ingredients.joinToString()}")
                            }
                        }
                    }
                }
            }
        }
    }
}
