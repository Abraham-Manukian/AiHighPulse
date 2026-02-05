package com.vtempe.server.features.ai.data.usecase

import com.vtempe.server.features.ai.data.service.AiService
import com.vtempe.server.features.ai.domain.usecase.BootstrapUseCase
import com.vtempe.server.shared.dto.bootstrap.AiBootstrapRequest
import com.vtempe.server.shared.dto.bootstrap.AiBootstrapResponse

class BootstrapUseCaseImpl(private val aiService: AiService) : BootstrapUseCase {
    override suspend fun execute(req: AiBootstrapRequest): AiBootstrapResponse = aiService.bundle(req)
}
