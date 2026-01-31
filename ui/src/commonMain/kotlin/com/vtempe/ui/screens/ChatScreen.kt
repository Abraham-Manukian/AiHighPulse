@file:OptIn(org.jetbrains.compose.resources.ExperimentalResourceApi::class)

package com.vtempe.ui.screens
import com.vtempe.ui.*

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.vtempe.core.designsystem.components.BrandScreen
import com.vtempe.shared.domain.repository.ChatMessage
import com.vtempe.ui.LocalBottomBarHeight
import com.vtempe.ui.LocalTopBarHeight
import org.jetbrains.compose.resources.stringResource

@Composable
fun ChatScreen(
    presenter: ChatPresenter = rememberChatPresenter()
) {
    val state by presenter.state.collectAsState()
    
    val topBarHeight = LocalTopBarHeight.current
    val bottomBarHeight = LocalBottomBarHeight.current

    BrandScreen(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            // РћС‚СЃС‚СѓРї СЃРІРµСЂС…Сѓ РґР»СЏ С‚РѕРї Р±Р°СЂР°
            Spacer(Modifier.size(topBarHeight))
            
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { Spacer(Modifier.size(12.dp)) }
                items(state.messages) { msg ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(250)) + slideInVertically(
                            initialOffsetY = { it / 8 },
                            animationSpec = tween(250)
                        )
                    ) {
                        MessageBubble(msg)
                    }
                }
                item { Spacer(Modifier.size(12.dp)) }
            }
            Divider()
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .padding(bottom = bottomBarHeight), // РћС‚СЃС‚СѓРї СЃРЅРёР·Сѓ РґР»СЏ Р±РѕС‚С‚РѕРј Р±Р°СЂР°
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = state.input,
                    onValueChange = presenter::updateInput,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(stringResource(Res.string.chat_hint)) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )
                Spacer(Modifier.size(8.dp))
                val isLoading = state.sendState is ChatSendState.Loading
                Button(enabled = !isLoading, onClick = { presenter.send() }) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.size(8.dp))
                    }
                    Icon(imageVector = Icons.Filled.Send, contentDescription = null)
                    Spacer(Modifier.size(6.dp))
                    Text(stringResource(Res.string.chat_send))
                }
            }
            val errorMessage = (state.sendState as? ChatSendState.Error)?.message
            if (errorMessage != null) {
                Text(
                    errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp).padding(bottom = bottomBarHeight)
                )
            }
        }
    }
}

@Composable
private fun MessageBubble(msg: ChatMessage) {
    val isUser = msg.role == "user"
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 1.dp
        ) {
            Text(
                msg.content,
                color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .padding(12.dp)
            )
        }
    }
}

