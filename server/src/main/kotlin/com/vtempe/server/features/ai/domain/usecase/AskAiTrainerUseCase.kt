package com.vtempe.server.features.ai.domain.usecase

import com.vtempe.server.shared.dto.chat.AiChatRequest
import com.vtempe.server.shared.dto.chat.AiChatResponse

interface AskAiTrainerUseCase {
    suspend fun execute(req: AiChatRequest): AiChatResponse
}
