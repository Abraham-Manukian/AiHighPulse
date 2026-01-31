package com.vtempe.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.vtempe.shared.domain.usecase.ValidateSubscription
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.vtempe.shared.data.di.KoinProvider

private class IosPaywallPresenter(
    private val validateSubscription: ValidateSubscription
) : PaywallPresenter {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private val mutableState = MutableStateFlow(PaywallState())
    override val state: StateFlow<PaywallState> = mutableState

    override fun refresh() {
        scope.launch {
            val active = runCatching { validateSubscription() }.getOrDefault(false)
            mutableState.value = PaywallState(active)
        }
    }

    fun close() {
        job.cancel()
    }
}

@Composable
actual fun rememberPaywallPresenter(): PaywallPresenter {
    val presenter = remember {
        val koin = requireNotNull(KoinProvider.koin) { "Koin is not started" }
        IosPaywallPresenter(validateSubscription = koin.get<ValidateSubscription>())
    }
    DisposableEffect(Unit) { onDispose { presenter.close() } }
    return presenter
}

