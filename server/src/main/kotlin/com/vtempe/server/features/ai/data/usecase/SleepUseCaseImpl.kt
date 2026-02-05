package com.vtempe.server.features.ai.data.usecase

import com.vtempe.server.features.ai.data.service.AiService
import com.vtempe.server.features.ai.domain.usecase.SleepUseCase
import com.vtempe.server.shared.dto.advice.AiAdviceRequest
import com.vtempe.server.shared.dto.advice.AiAdviceResponse

class SleepUseCaseImpl(private val aiService: AiService) : SleepUseCase {
    override suspend fun execute(req: AiAdviceRequest): AiAdviceResponse = aiService.sleep(req)
}
