package com.example.aihighpulse.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aihighpulse.shared.domain.usecase.ValidateSubscription
import com.example.aihighpulse.ui.screens.PaywallPresenter
import com.example.aihighpulse.ui.screens.PaywallState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PaywallViewModel(
    private val validateSubscription: ValidateSubscription
) : ViewModel(), PaywallPresenter {
    private val _state = MutableStateFlow(PaywallState())
    override val state: StateFlow<PaywallState> = _state.asStateFlow()

    override fun refresh() {
        viewModelScope.launch {
            val active = runCatching { validateSubscription() }.getOrDefault(false)
            _state.value = PaywallState(active)
        }
    }
}
