package com.vtempe.server.features.ai.data

import com.vtempe.server.features.ai.data.service.ChatService
import com.vtempe.server.features.ai.domain.usecase.AskAiTrainerUseCase
import com.vtempe.server.shared.dto.chat.AiChatRequest
import com.vtempe.server.shared.dto.chat.AiChatResponse

class AskAiTrainerUseCaseImpl(
    private val chatService: ChatService
) : AskAiTrainerUseCase {
    override suspend fun execute(req: AiChatRequest): AiChatResponse {
        return chatService.chat(req)
    }
}
