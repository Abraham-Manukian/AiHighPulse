package com.example.aihighpulse.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.aihighpulse.R
import com.example.aihighpulse.shared.domain.repository.ChatMessage
import com.example.aihighpulse.ui.vm.ChatSendState
import com.example.aihighpulse.ui.vm.ChatViewModel
import org.koin.androidx.compose.koinViewModel
import com.example.aihighpulse.core.designsystem.theme.AiGradients
import com.example.aihighpulse.core.designsystem.components.BrandScreen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon

@Composable
fun ChatScreen() {
    val vm: ChatViewModel = koinViewModel()
    val s by vm.state.collectAsState()
    BrandScreen(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(s.messages) { msg ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(250)) + slideInVertically(initialOffsetY = { it / 8 }, animationSpec = tween(250))
                    ) {
                        MessageBubble(msg)
                    }
                }
            }
            Divider()
            Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = s.input,
                onValueChange = vm::updateInput,
                modifier = Modifier.weight(1f),
                placeholder = { Text(stringResource(R.string.chat_hint)) },
                trailingIcon = {
                    Icon(imageVector = Icons.Filled.Mic, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            )
            Spacer(Modifier.width(8.dp))
            val isLoading = s.sendState is ChatSendState.Loading
            Button(enabled = !isLoading, onClick = { vm.send() }) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Icon(imageVector = Icons.Filled.Send, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text(stringResource(R.string.chat_send))
            }
            }
            val errorMessage = (s.sendState as? ChatSendState.Error)?.message
            if (errorMessage != null) {
                Text(
                    errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun MessageBubble(msg: ChatMessage) {
    val isUser = msg.role == "user"
    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 1.dp
        ) {
            Text(
                msg.content,
                color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}
