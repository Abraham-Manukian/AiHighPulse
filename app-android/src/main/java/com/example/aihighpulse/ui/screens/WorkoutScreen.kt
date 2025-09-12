package com.example.aihighpulse.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.height
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.aihighpulse.R
import com.example.aihighpulse.ui.vm.WorkoutViewModel
import org.koin.androidx.compose.koinViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen() {
    val vm: WorkoutViewModel = koinViewModel()
    val s by vm.state.collectAsState()
    val showAddSheet = remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddSheet.value = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add set")
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding), verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                            .border(
                                1.dp,
                                if (s.selectedWorkoutId == w.id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                MaterialTheme.shapes.medium
                            )
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("${w.date}", style = MaterialTheme.typography.titleMedium)
                            w.sets.forEach { set ->
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(set.exerciseId)
                                    Text("${set.reps} x ${set.weightKg ?: 0.0} kg  RPE ${set.rpe ?: 7.0}")
                                }
                            }
                        }
                    }
                }
            }
            RestTimer()
        }
        if (showAddSheet.value) {
            ModalBottomSheet(onDismissRequest = { showAddSheet.value = false }) {
                AddSetPanel(onAdd = { ex, reps, weight, rpe ->
                    vm.addSet(ex, reps, weight, rpe)
                    showAddSheet.value = false
                })
            }
        }
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
            Text(stringResource(R.string.workout_add_set_title), style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(ex.value, onValueChange = { ex.value = it }, label = { Text(stringResource(R.string.workout_exercise_label)) }, modifier = Modifier.weight(1f))
                OutlinedTextField(reps.value, onValueChange = { reps.value = it.filter { c -> c.isDigit() }.take(3) }, label = { Text(stringResource(R.string.workout_reps_label)) }, modifier = Modifier.weight(1f))
                OutlinedTextField(weight.value, onValueChange = { weight.value = it.filter { c -> c.isDigit() || c == '.' }.take(5) }, label = { Text(stringResource(R.string.workout_weight_label)) }, modifier = Modifier.weight(1f))
                OutlinedTextField(rpe.value, onValueChange = { rpe.value = it.filter { c -> c.isDigit() || c == '.' }.take(4) }, label = { Text("RPE") }, modifier = Modifier.weight(1f))
            }
            Button(onClick = {
                onAdd(ex.value, reps.value.toIntOrNull() ?: 10, weight.value.toDoubleOrNull(), rpe.value.toDoubleOrNull())
            }) { Text(stringResource(R.string.workout_add_button)) }
        }
    }
}

@Composable
private fun RestTimer(totalSec: Int = 60) {
    val scope = rememberCoroutineScope()
    val progress = remember { Animatable(0f) }
    val running = remember { mutableStateOf(false) }
    Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.workout_rest_timer_title), style = MaterialTheme.typography.titleMedium)
            val bgColor = MaterialTheme.colorScheme.surfaceVariant
            val ringColor: Color = when {
                progress.value < 0.66f -> Color(0xFF00B894) // green
                progress.value < 0.9f -> Color(0xFFF4C20D) // yellow
                else -> Color(0xFFD63031) // red
            }
            val remaining = ((totalSec * (1f - progress.value)).coerceAtLeast(0f)).toInt()
            Box(Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
                androidx.compose.foundation.Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    val stroke = 16.dp.toPx()
                    val radius = (size.minDimension - stroke) / 2f
                    val center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f)
                    // background circle
                    drawArc(
                        color = bgColor,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
                    )
                    // progress
                    drawArc(
                        color = ringColor,
                        startAngle = -90f,
                        sweepAngle = 360f * progress.value,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
                    )
                }
                Text("${remaining}s", style = MaterialTheme.typography.titleLarge)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(enabled = !running.value, onClick = {
                    running.value = true
                    scope.launch {
                        progress.snapTo(0f)
                        progress.animateTo(1f, animationSpec = tween(durationMillis = totalSec * 1000))
                        running.value = false
                    }
                }) { Text(stringResource(R.string.workout_rest_start)) }
                OutlinedButton(enabled = running.value, onClick = {
                    scope.launch { progress.stop(); running.value = false }
                }) { Text(stringResource(R.string.workout_rest_stop)) }
            }
        }
    }
}
