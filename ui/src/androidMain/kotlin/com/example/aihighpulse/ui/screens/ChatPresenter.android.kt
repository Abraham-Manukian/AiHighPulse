package com.example.aihighpulse.ui.screens

import androidx.compose.runtime.Composable
import com.example.aihighpulse.ui.vm.ChatViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
actual fun rememberChatPresenter(): ChatPresenter = koinViewModel<ChatViewModel>()
