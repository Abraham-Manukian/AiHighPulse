package com.vtempe.server.features.ai.data.usecase

import com.vtempe.server.features.ai.data.service.AiService
import com.vtempe.server.features.ai.domain.usecase.NutritionUseCase
import com.vtempe.server.shared.dto.nutrition.AiNutritionRequest
import com.vtempe.server.shared.dto.nutrition.AiNutritionResponse

class NutritionUseCaseImpl(private val aiService: AiService) : NutritionUseCase {
    override suspend fun execute(req: AiNutritionRequest): AiNutritionResponse = aiService.nutrition(req)
}
