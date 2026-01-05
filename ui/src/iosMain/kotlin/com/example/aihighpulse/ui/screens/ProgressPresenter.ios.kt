package com.example.aihighpulse.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.example.aihighpulse.shared.domain.repository.TrainingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.aihighpulse.shared.data.di.KoinProvider

private class IosProgressPresenter(
    private val trainingRepository: TrainingRepository
) : ProgressPresenter {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private val mutableState = MutableStateFlow(ProgressState())
    override val state: StateFlow<ProgressState> = mutableState

    init {
        scope.launch {
            trainingRepository.observeWorkouts().collectLatest { list ->
                val sets = list.sumOf { it.sets.size }
                val volume = list.sumOf { it.sets.sumOf { s -> ((s.weightKg ?: 0.0) * s.reps).toInt() } }
                val daily = IntArray(7) { 0 }
                list.forEach { w ->
                    val v = w.sets.sumOf { s -> ((s.weightKg ?: 0.0) * s.reps).toInt() }
                    val index = w.date.dayOfWeek.ordinal
                    daily[index] = daily[index] + v
                }
                val vols = daily.toList()
                val baseWeight = 78f
                val weight = (0 until 14).map { i ->
                    baseWeight + kotlin.random.Random(i).nextInt(-2, 3) * 0.5f
                }
                val calories = (0 until 7).map { 2300 + (vols.getOrNull(it) ?: 0) * 2 }
                mutableState.value = ProgressState(
                    totalWorkouts = list.size,
                    totalSets = sets,
                    totalVolume = volume,
                    weeklyVolumes = vols,
                    weightSeries = weight,
                    caloriesSeries = calories,
                    sleepHoursWeek = listOf(7, 7, 6, 8, 7, 9, 8)
                )
            }
        }
    }

    fun close() {
        job.cancel()
    }
}

@Composable
actual fun rememberProgressPresenter(): ProgressPresenter {
    val presenter = remember {
        val koin = requireNotNull(KoinProvider.koin) { "Koin is not started" }
        IosProgressPresenter(trainingRepository = koin.get<TrainingRepository>())
    }
    DisposableEffect(Unit) { onDispose { presenter.close() } }
    return presenter
}
