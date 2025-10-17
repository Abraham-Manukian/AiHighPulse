package com.example.aihighpulse.ui.vm

import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aihighpulse.shared.domain.repository.ChatMessage
import com.example.aihighpulse.shared.domain.usecase.AskAiTrainer
import com.example.aihighpulse.shared.domain.util.DataResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ChatSendState {
    data object Idle : ChatSendState
    data object Loading : ChatSendState
    data object Success : ChatSendState
    data class Error(val message: String) : ChatSendState
}

data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val input: String = "",
    val sendState: ChatSendState = ChatSendState.Idle
)

class ChatViewModel(private val ask: AskAiTrainer) : ViewModel() {
    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    fun updateInput(text: String) {
        _state.update { current ->
            val normalizedState = when (current.sendState) {
                is ChatSendState.Error -> ChatSendState.Idle
                ChatSendState.Success -> ChatSendState.Idle
                else -> current.sendState
            }
            current.copy(input = text, sendState = normalizedState)
        }
    }

    fun send() {
        val trimmed = _state.value.input.trim()
        val currentState = _state.value
        if (trimmed.isEmpty() || currentState.sendState is ChatSendState.Loading) return

        val history = currentState.messages
        val newHistory = history + ChatMessage("user", trimmed)
        _state.value = currentState.copy(messages = newHistory, input = "", sendState = ChatSendState.Loading)

        viewModelScope.launch {
            val localeTag = LocaleListCompat.getAdjustedDefault().get(0)?.toLanguageTag()
            when (val result = ask(history, trimmed, localeTag)) {
                is DataResult.Success -> {
                    _state.update { current ->
                        current.copy(
                            messages = current.messages + ChatMessage("assistant", result.data.reply),
                            sendState = ChatSendState.Success
                        )
                    }
                }
                is DataResult.Failure -> {
                    val message = result.message ?: when (result.reason) {
                        DataResult.Reason.Timeout -> "Request timed out. Please try again."
                        DataResult.Reason.Network -> "No network connection."
                        DataResult.Reason.Http -> "Server returned an error (${result.code ?: "unknown"})."
                        DataResult.Reason.InvalidFormat -> "Could not parse the assistant reply."
                        DataResult.Reason.CacheMissing -> "No cached response is available."
                        DataResult.Reason.Unknown -> "Unable to send your message."
                    }
                    _state.update {
                        it.copy(sendState = ChatSendState.Error(message))
                    }
                }
            }
        }
    }
}
