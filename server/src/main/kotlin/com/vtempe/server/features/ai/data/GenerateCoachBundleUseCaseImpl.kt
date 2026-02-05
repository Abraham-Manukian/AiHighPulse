package com.vtempe.server.features.ai.data

import com.vtempe.server.features.ai.data.service.AiService
import com.vtempe.server.features.ai.domain.usecase.GenerateCoachBundleUseCase
import com.vtempe.server.shared.dto.bootstrap.AiBootstrapRequest
import com.vtempe.server.shared.dto.bootstrap.AiBootstrapResponse

class GenerateCoachBundleUseCaseImpl(
    private val aiService: AiService
) : GenerateCoachBundleUseCase {

    override suspend fun execute(input: AiBootstrapRequest): AiBootstrapResponse {
        return aiService.bundle(input)
    }
}
