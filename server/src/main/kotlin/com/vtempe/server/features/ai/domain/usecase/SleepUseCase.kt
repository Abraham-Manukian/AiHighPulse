package com.vtempe.server.features.ai.domain.usecase

import com.vtempe.server.shared.dto.advice.AiAdviceRequest
import com.vtempe.server.shared.dto.advice.AiAdviceResponse

interface SleepUseCase {
    suspend fun execute(req: AiAdviceRequest): AiAdviceResponse
}
