package com.vtempe.server.features.ai.data.usecase

import com.vtempe.server.features.ai.data.service.ChatService
import com.vtempe.server.features.ai.domain.usecase.ChatUseCase
import com.vtempe.server.shared.dto.chat.AiChatRequest
import com.vtempe.server.shared.dto.chat.AiChatResponse

class ChatUseCaseImpl(private val chatService: ChatService) : ChatUseCase {
    override suspend fun execute(req: AiChatRequest): AiChatResponse = chatService.chat(req)
}
