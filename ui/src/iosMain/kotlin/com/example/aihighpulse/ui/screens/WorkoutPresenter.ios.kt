package com.example.aihighpulse.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.example.aihighpulse.shared.domain.model.WorkoutSet
import com.example.aihighpulse.shared.domain.repository.TrainingRepository
import com.example.aihighpulse.shared.domain.usecase.EnsureCoachData
import com.example.aihighpulse.shared.domain.usecase.LogWorkoutSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.aihighpulse.shared.data.di.KoinProvider

private class IosWorkoutPresenter(
    private val trainingRepository: TrainingRepository,
    private val logWorkoutSet: LogWorkoutSet,
    private val ensureCoachData: EnsureCoachData,
) : WorkoutPresenter {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private val mutableState = MutableStateFlow(WorkoutState())
    override val state: StateFlow<WorkoutState> = mutableState

    init {
        scope.launch {
            trainingRepository.observeWorkouts().collectLatest { list ->
                val selected = mutableState.value.selectedWorkoutId ?: list.firstOrNull()?.id
                val updatedFeedback = list.associate { w ->
                    val current = mutableState.value.feedback[w.id]
                    w.id to (current ?: WorkoutFeedback())
                }
                mutableState.value =
                    mutableState.value.copy(
                        workouts = list,
                        selectedWorkoutId = selected,
                        feedback = updatedFeedback
                    )
            }
        }
        scope.launch { runCatching { ensureCoachData() } }
    }

    override fun select(workoutId: String) {
        mutableState.value = mutableState.value.copy(selectedWorkoutId = workoutId)
    }

    override fun addSet(exerciseId: String, reps: Int, weight: Double?) {
        val id = mutableState.value.selectedWorkoutId ?: return
        scope.launch { logWorkoutSet(id, WorkoutSet(exerciseId, reps, weight, null)) }
    }

    override fun updateNotes(workoutId: String, notes: String) {
        val feedback = mutableState.value.feedback.toMutableMap()
        val current = feedback[workoutId] ?: WorkoutFeedback()
        feedback[workoutId] = current.copy(notes = notes.take(500), submitted = false)
        mutableState.value = mutableState.value.copy(feedback = feedback)
    }

    override fun toggleSetCompleted(workoutId: String, index: Int, completed: Boolean) {
        val feedback = mutableState.value.feedback.toMutableMap()
        val current = feedback[workoutId] ?: WorkoutFeedback()
        val newSet = if (completed) current.completedSets + index else current.completedSets - index
        feedback[workoutId] = current.copy(completedSets = newSet, submitted = false)
        mutableState.value = mutableState.value.copy(feedback = feedback)
    }

    override fun submitFeedback(workoutId: String) {
        val feedback = mutableState.value.feedback.toMutableMap()
        val current = feedback[workoutId] ?: WorkoutFeedback()
        feedback[workoutId] = current.copy(submitted = true)
        mutableState.value = mutableState.value.copy(feedback = feedback)
    }

    fun close() {
        job.cancel()
    }
}

@Composable
actual fun rememberWorkoutPresenter(): WorkoutPresenter {
    val presenter = remember {
        val koin = requireNotNull(KoinProvider.koin) { "Koin is not started" }
        IosWorkoutPresenter(
            trainingRepository = koin.get<TrainingRepository>(),
            logWorkoutSet = koin.get<LogWorkoutSet>(),
            ensureCoachData = koin.get<EnsureCoachData>()
        )
    }
    DisposableEffect(Unit) { onDispose { presenter.close() } }
    return presenter
}
