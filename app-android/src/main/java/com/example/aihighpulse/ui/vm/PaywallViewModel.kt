package com.example.aihighpulse.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aihighpulse.shared.domain.usecase.ValidateSubscription
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PaywallState(val active: Boolean = false)

class PaywallViewModel(
    private val validateSubscription: ValidateSubscription
) : ViewModel() {
    private val _state = MutableStateFlow(PaywallState())
    val state: StateFlow<PaywallState> = _state.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            val active = runCatching { validateSubscription() }.getOrDefault(false)
            _state.value = PaywallState(active)
        }
    }
}
