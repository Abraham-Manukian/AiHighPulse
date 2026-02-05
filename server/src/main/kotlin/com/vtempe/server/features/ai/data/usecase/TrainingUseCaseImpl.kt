package com.vtempe.server.features.ai.data.usecase

import com.vtempe.server.features.ai.data.service.AiService
import com.vtempe.server.features.ai.domain.usecase.TrainingUseCase
import com.vtempe.server.shared.dto.training.AiTrainingRequest
import com.vtempe.server.shared.dto.training.AiTrainingResponse

class TrainingUseCaseImpl(private val aiService: AiService) : TrainingUseCase {
    override suspend fun execute(req: AiTrainingRequest): AiTrainingResponse = aiService.training(req)
}
