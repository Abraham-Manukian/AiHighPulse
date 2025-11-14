package com.example.aihighpulse.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import com.example.aihighpulse.ui.state.UiState
import com.example.aihighpulse.ui.vm.NutritionViewModel
import org.koin.androidx.compose.koinViewModel
import com.example.aihighpulse.core.designsystem.theme.AiGradients
import com.example.aihighpulse.core.designsystem.components.BrandScreen

@Composable
fun ShoppingListScreen(onBack: () -> Unit) {
    val vm: NutritionViewModel = koinViewModel()
    val s by vm.state.collectAsState()
    BrandScreen(Modifier.fillMaxSize()) {
        Button(onClick = onBack, modifier = Modifier.padding(16.dp)) { Text(stringResource(R.string.action_back)) }
        when (val ui = s.ui) {
            UiState.Loading -> Text(stringResource(R.string.loading), modifier = Modifier.padding(16.dp))
            is UiState.Error -> Text(stringResource(R.string.nutrition_error_title), color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
            is UiState.Data -> {
                val items = ui.value.shoppingList
                Text(stringResource(R.string.nutrition_tab_shopping), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(16.dp))
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { it ->
                        AnimatedVisibility(visible = true, enter = fadeIn(tween(250)) + slideInVertically(initialOffsetY = { it / 10 }, animationSpec = tween(250))) {
                            Text("• $it")
                        }
                    }
                }
            }
        }
    }
}
