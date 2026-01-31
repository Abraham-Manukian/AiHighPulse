package com.vtempe.ui.screens

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.StateFlow

data class PaywallState(val active: Boolean = false)

interface PaywallPresenter {
    val state: StateFlow<PaywallState>
    fun refresh()
}

@Composable
expect fun rememberPaywallPresenter(): PaywallPresenter

