package com.vtempe.server.features.ai.domain.usecase

import com.vtempe.server.shared.dto.training.AiTrainingRequest
import com.vtempe.server.shared.dto.training.AiTrainingResponse

interface TrainingUseCase {
    suspend fun execute(req: AiTrainingRequest): AiTrainingResponse
}
