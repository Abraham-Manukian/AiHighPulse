package com.example.aihighpulse.ui.vm

import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aihighpulse.shared.domain.repository.ChatMessage
import com.example.aihighpulse.shared.domain.usecase.AskAiTrainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val input: String = "",
    val sending: Boolean = false,
    val error: String? = null
)

class ChatViewModel(private val ask: AskAiTrainer): ViewModel() {
    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    fun updateInput(text: String) { _state.value = _state.value.copy(input = text) }

    fun send() {
        val text = _state.value.input.trim()
        if (text.isEmpty() || _state.value.sending) return
        val history = _state.value.messages
        val newHistory = history + ChatMessage("user", text)
        _state.value = _state.value.copy(messages = newHistory, input = "", sending = true, error = null)
        viewModelScope.launch {
            val localeTag = LocaleListCompat.getAdjustedDefault().get(0)?.toLanguageTag()
            val reply = runCatching { ask(history, text, localeTag) }
            _state.value = reply.fold(
                onSuccess = { r -> _state.value.copy(messages = newHistory + ChatMessage("assistant", r.reply), sending = false) },
                onFailure = { e -> _state.value.copy(sending = false, error = e.message) }
            )
        }
    }
}


