package com.example.aihighpulse.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import com.example.aihighpulse.ui.vm.WorkoutViewModel

@Composable
fun WorkoutScreen() {
    val vm: WorkoutViewModel = koinViewModel()
    val s by vm.state.collectAsState()

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(s.workouts) { w ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { vm.select(w.id) }
                        .border(1.dp, if (s.selectedWorkoutId == w.id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("${'$'}{w.date}", style = MaterialTheme.typography.titleMedium)
                        w.sets.forEach { set ->
                            Text("• ${'$'}{set.exerciseId}: ${'$'}{set.reps} x ${'$'}{set.weightKg ?: 0.0} кг  RPE ${'$'}{set.rpe ?: 7.0}")
                        }
                    }
                }
            }
        }
        AddSetPanel(onAdd = { ex, reps, weight, rpe -> vm.addSet(ex, reps, weight, rpe) })
    }
}

@Composable
private fun AddSetPanel(onAdd: (exerciseId: String, reps: Int, weight: Double?, rpe: Double?) -> Unit) {
    val ex = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("squat") }
    val reps = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("10") }
    val weight = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("40") }
    val rpe = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("7.5") }
    Card(Modifier.fillMaxWidth().padding(16.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Добавить подход", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(ex.value, onValueChange = { ex.value = it }, label = { Text("Упражнение") }, modifier = Modifier.weight(1f))
                OutlinedTextField(reps.value, onValueChange = { reps.value = it.filter { c -> c.isDigit() }.take(3) }, label = { Text("Повт") }, modifier = Modifier.weight(1f))
                OutlinedTextField(weight.value, onValueChange = { weight.value = it.filter { c -> c.isDigit() || c == '.' }.take(5) }, label = { Text("Вес") }, modifier = Modifier.weight(1f))
                OutlinedTextField(rpe.value, onValueChange = { rpe.value = it.filter { c -> c.isDigit() || c == '.' }.take(4) }, label = { Text("RPE") }, modifier = Modifier.weight(1f))
            }
            Button(onClick = {
                onAdd(ex.value, reps.value.toIntOrNull() ?: 10, weight.value.toDoubleOrNull(), rpe.value.toDoubleOrNull())
            }) { Text("Добавить") }
        }
    }
}
