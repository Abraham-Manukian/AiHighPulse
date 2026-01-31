package com.vtempe.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.vtempe.shared.domain.repository.TrainingRepository
import com.vtempe.shared.domain.usecase.EnsureCoachData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.vtempe.shared.data.di.KoinProvider

private class IosHomePresenter(
    private val trainingRepository: TrainingRepository,
    private val ensureCoachData: EnsureCoachData,
) : HomePresenter {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private val mutableState = MutableStateFlow(HomeState(loading = true))
    override val state: StateFlow<HomeState> = mutableState

    init {
        scope.launch { runCatching { ensureCoachData() } }
        scope.launch {
            trainingRepository.observeWorkouts().collectLatest { list ->
                val sets = list.firstOrNull()?.sets?.size ?: 0
                val volume =
                    list.firstOrNull()?.sets?.sumOf { ((it.weightKg ?: 0.0) * it.reps).toInt() } ?: 0
                val sleepMinutes = mutableState.value.sleepMinutes
                mutableState.value = HomeState(
                    workouts = list,
                    todaySets = sets,
                    totalVolume = volume,
                    sleepMinutes = sleepMinutes,
                    loading = false
                )
            }
        }
    }

    fun close() {
        job.cancel()
    }
}

@Composable
actual fun rememberHomePresenter(): HomePresenter {
    val presenter = remember {
        val koin = requireNotNull(KoinProvider.koin) { "Koin is not started" }
        IosHomePresenter(
            trainingRepository = koin.get<TrainingRepository>(),
            ensureCoachData = koin.get<EnsureCoachData>()
        )
    }
    DisposableEffect(Unit) { onDispose { presenter.close() } }
    return presenter
}

