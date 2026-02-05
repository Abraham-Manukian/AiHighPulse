package com.vtempe.server.features.ai.domain.usecase

import com.vtempe.server.shared.dto.bootstrap.AiBootstrapRequest
import com.vtempe.server.shared.dto.bootstrap.AiBootstrapResponse

interface BootstrapUseCase {
    suspend fun execute(req: AiBootstrapRequest): AiBootstrapResponse
}
