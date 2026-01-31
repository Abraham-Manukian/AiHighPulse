package com.vtempe.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.vtempe.shared.domain.model.Goal
import com.vtempe.shared.domain.model.Profile
import com.vtempe.shared.domain.model.Sex
import com.vtempe.shared.domain.repository.AdviceRepository
import com.vtempe.shared.domain.repository.ProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import com.vtempe.shared.data.di.KoinProvider

private class IosSleepPresenter(
    private val adviceRepository: AdviceRepository,
    private val profileRepository: ProfileRepository
) : SleepPresenter {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private val mutableState = MutableStateFlow(SleepState())
    override val state: StateFlow<SleepState> = mutableState

    init {
        scope.launch {
            adviceRepository.observeAdvice("sleep").collect { advice ->
                mutableState.value = mutableState.value.copy(
                    tips = advice.messages,
                    disclaimer = advice.disclaimer
                )
            }
        }
        scope.launch {
            val profile =
                profileRepository.getProfile()
                    ?: Profile("local", 28, Sex.MALE, 178, 75.0, Goal.MAINTAIN, 3)
            runCatching { adviceRepository.getAdvice(profile, mapOf("topic" to "sleep")) }
        }
    }

    override fun sync() {
        scope.launch {
            mutableState.value = mutableState.value.copy(syncing = true)
            delay(800)
            mutableState.value = mutableState.value.copy(syncing = false)
        }
    }

    fun close() {
        job.cancel()
    }
}

@Composable
actual fun rememberSleepPresenter(): SleepPresenter {
    val presenter = remember {
        val koin = requireNotNull(KoinProvider.koin) { "Koin is not started" }
        IosSleepPresenter(
            adviceRepository = koin.get<AdviceRepository>(),
            profileRepository = koin.get<ProfileRepository>()
        )
    }
    DisposableEffect(Unit) { onDispose { presenter.close() } }
    return presenter
}

