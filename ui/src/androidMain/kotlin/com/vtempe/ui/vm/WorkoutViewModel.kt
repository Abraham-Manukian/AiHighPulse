package com.vtempe.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtempe.shared.domain.model.WorkoutSet
import com.vtempe.shared.domain.repository.TrainingRepository
import com.vtempe.shared.domain.usecase.EnsureCoachData
import com.vtempe.shared.domain.usecase.LogWorkoutSet
import com.vtempe.ui.screens.WorkoutFeedback
import com.vtempe.ui.screens.WorkoutPresenter
import com.vtempe.ui.screens.WorkoutState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WorkoutViewModel(
    private val trainingRepository: TrainingRepository,
    private val logWorkoutSet: LogWorkoutSet,
    private val ensureCoachData: EnsureCoachData,
) : ViewModel(), WorkoutPresenter {
    private val _state = MutableStateFlow(WorkoutState())
    override val state: StateFlow<WorkoutState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            trainingRepository.observeWorkouts().collectLatest { list ->
                val selected = _state.value.selectedWorkoutId ?: list.firstOrNull()?.id
                val updatedFeedback = list.associate { w ->
                    val current = _state.value.feedback[w.id]
                    w.id to (current ?: WorkoutFeedback())
                }
                _state.value = _state.value.copy(workouts = list, selectedWorkoutId = selected, feedback = updatedFeedback)
            }
        }
        viewModelScope.launch { runCatching { ensureCoachData() } }
    }

    override fun select(workoutId: String) { _state.value = _state.value.copy(selectedWorkoutId = workoutId) }

    override fun addSet(exerciseId: String, reps: Int, weight: Double?) {
        val id = _state.value.selectedWorkoutId ?: return
        viewModelScope.launch { logWorkoutSet(id, WorkoutSet(exerciseId, reps, weight, null)) }
    }

    override fun toggleSetCompleted(workoutId: String, index: Int, completed: Boolean) {
        val feedback = _state.value.feedback.toMutableMap()
        val current = feedback[workoutId] ?: WorkoutFeedback()
        val newSet = if (completed) current.completedSets + index else current.completedSets - index
        feedback[workoutId] = current.copy(completedSets = newSet, submitted = false)
        _state.value = _state.value.copy(feedback = feedback)
    }

    override fun updateNotes(workoutId: String, notes: String) {
        val feedback = _state.value.feedback.toMutableMap()
        val current = feedback[workoutId] ?: WorkoutFeedback()
        feedback[workoutId] = current.copy(notes = notes.take(500), submitted = false)
        _state.value = _state.value.copy(feedback = feedback)
    }

    override fun submitFeedback(workoutId: String) {
        val feedback = _state.value.feedback.toMutableMap()
        val current = feedback[workoutId] ?: WorkoutFeedback()
        feedback[workoutId] = current.copy(submitted = true)
        _state.value = _state.value.copy(feedback = feedback)
        // Hook for future AI sync
    }
}

