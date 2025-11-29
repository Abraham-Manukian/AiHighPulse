package com.example.aihighpulse.ui.screens
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.aihighpulse.R
import com.example.aihighpulse.core.designsystem.components.BrandScreen
import com.example.aihighpulse.core.designsystem.theme.AiPalette
import com.example.aihighpulse.ui.vm.WorkoutFeedback
import com.example.aihighpulse.ui.vm.WorkoutViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen() {
    val vm: WorkoutViewModel = koinViewModel()
    val s by vm.state.collectAsState()
    val showAddSheet = remember { mutableStateOf(false) }

    BrandScreen(Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent,
            floatingActionButton = {
                FloatingActionButton(onClick = { showAddSheet.value = true }) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.workout_add_button))
                }
            }
        ) { padding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(s.workouts) { w ->
                        val feedback: WorkoutFeedback = s.feedback[w.id] ?: WorkoutFeedback()
                        val selected = s.selectedWorkoutId == w.id
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(300)) + slideInVertically(initialOffsetY = { it / 5 }, animationSpec = tween(300))
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { vm.select(w.id) },
                                colors = workoutCardColors(),
                                elevation = workoutCardElevation(),
                                shape = MaterialTheme.shapes.large,
                                border = if (selected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)) else null
                            ) {
                                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    val leadExercise = w.sets.firstOrNull()?.exerciseId ?: "Session"
                                    val progress = if (w.sets.isNotEmpty()) feedback.completedSets.size / w.sets.size.toFloat() else 0f
                                    WorkoutCardHeader(
                                        title = leadExercise,
                                        date = w.date.toString(),
                                        setsCount = w.sets.size,
                                        completed = feedback.completedSets.size,
                                        progress = progress
                                    )
                                    LinearProgressIndicator(
                                        progress = progress,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(CircleShape),
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                                    w.sets.forEachIndexed { index, set ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier.width(48.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Checkbox(
                                                    checked = feedback.completedSets.contains(index),
                                                    onCheckedChange = { vm.toggleSetCompleted(w.id, index, it) }
                                                )
                                            }
                                            Column(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(start = 12.dp),
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text(
                                                    set.exerciseId,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = FontWeight.SemiBold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                val weightLabel = set.weightKg?.let {
                                                    stringResource(R.string.workout_weight_display, it)
                                                } ?: stringResource(R.string.workout_weight_bodyweight)
                                                                                                Text(
                                                    stringResource(
                                                        R.string.workout_set_summary,
                                                        set.reps,
                                                        weightLabel
                                                    ),
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                    if (selected) {
                                        Text(
                                            stringResource(R.string.workout_feedback_title),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        OutlinedTextField(
                                            value = feedback.notes,
                                            onValueChange = { vm.updateNotes(w.id, it) },
                                            modifier = Modifier.fillMaxWidth(),
                                            label = { Text(stringResource(R.string.workout_notes_label)) },
                                            placeholder = { Text(stringResource(R.string.workout_notes_hint)) },
                                            singleLine = false,
                                            shape = MaterialTheme.shapes.medium
                                        )
                                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                            Button(
                                                onClick = { vm.submitFeedback(w.id) },
                                                enabled = feedback.notes.isNotBlank() || feedback.completedSets.isNotEmpty(),
                                                colors = workoutButtonColors(),
                                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 8.dp)
                                            ) {
                                                Text(stringResource(R.string.workout_mark_complete))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showAddSheet.value) {
            ModalBottomSheet(onDismissRequest = { showAddSheet.value = false }) {
                AddSetPanel(onAdd = { ex, reps, weight ->
                    vm.addSet(ex, reps, weight)
                    showAddSheet.value = false
                })
            }
        }
    }
}

@Composable
private fun workoutCardColors() = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.96f))

@Composable
private fun workoutCardElevation() = CardDefaults.cardElevation(defaultElevation = 10.dp)

@Composable
private fun workoutButtonColors() = ButtonDefaults.buttonColors(containerColor = AiPalette.DeepAccent, contentColor = Color.White)

@Composable
private fun AddSetPanel(onAdd: (exerciseId: String, reps: Int, weight: Double?) -> Unit) {
    val ex = remember { mutableStateOf("squat") }
    val reps = remember { mutableStateOf("10") }
    val weight = remember { mutableStateOf("40") }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = workoutCardColors(),
        elevation = workoutCardElevation(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(stringResource(R.string.workout_add_set_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(ex.value, onValueChange = { ex.value = it }, label = { Text(stringResource(R.string.workout_exercise_label)) }, modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.medium)
                OutlinedTextField(reps.value, onValueChange = { reps.value = it.filter { c -> c.isDigit() }.take(3) }, label = { Text(stringResource(R.string.workout_reps_label)) }, modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.medium)
                OutlinedTextField(
                    weight.value,
                    onValueChange = { weight.value = it.filter { ch -> ch.isDigit() || ch == '.' }.take(5) },
                    label = { Text(stringResource(R.string.workout_weight_label)) },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium
                )
            }
            Button(onClick = {
                onAdd(ex.value, reps.value.toIntOrNull() ?: 10, weight.value.toDoubleOrNull())
            }, colors = workoutButtonColors(), elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 8.dp)) { Text(stringResource(R.string.workout_add_button)) }
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
                progress.value < 0.66f -> Color(0xFF00B894)
                progress.value < 0.9f -> Color(0xFFF4C20D)
                else -> Color(0xFFD63031)
            }
            val remaining = ((totalSec * (1f - progress.value)).coerceAtLeast(0f)).toInt()
            Box(Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    val stroke = 16.dp.toPx()
                    drawArc(
                        color = bgColor,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
                    )
                    drawArc(
                        color = ringColor,
                        startAngle = -90f,
                        sweepAngle = 360f * progress.value,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
                    )
                }
                Text(stringResource(R.string.workout_rest_remaining, remaining), style = MaterialTheme.typography.titleLarge)
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

@Composable
private fun WorkoutCardHeader(
    title: String,
    date: String,
    setsCount: Int,
    completed: Int,
    progress: Float
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ExerciseIllustration(title)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    date,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    stringResource(R.string.workout_sets_scheduled, setsCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        FilledTonalButton(
            onClick = {},
            enabled = false,
            shape = CircleShape,
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                disabledContentColor = MaterialTheme.colorScheme.primary
            ),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp)
        ) {
            Text(
                stringResource(R.string.workout_progress_status, (progress * 100).roundToInt(), completed, setsCount),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ExerciseIllustration(exerciseId: String) {
    val icon = iconForExercise(exerciseId)
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = exerciseId, tint = Color.White)
    }
}

private fun iconForExercise(exerciseId: String): ImageVector = when {
    exerciseId.contains("run", ignoreCase = true) -> Icons.Filled.DirectionsRun
    exerciseId.contains("bike", ignoreCase = true) -> Icons.Filled.DirectionsBike
    exerciseId.contains("yoga", ignoreCase = true) -> Icons.Filled.SelfImprovement
    else -> Icons.Filled.FitnessCenter
}



