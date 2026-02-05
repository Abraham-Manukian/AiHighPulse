package com.vtempe.server.features.ai.domain.usecase

import com.vtempe.server.shared.dto.bootstrap.AiBootstrapRequest
import com.vtempe.server.shared.dto.bootstrap.AiBootstrapResponse

interface GenerateCoachBundleUseCase {
    suspend fun execute(input: AiBootstrapRequest): AiBootstrapResponse
}
