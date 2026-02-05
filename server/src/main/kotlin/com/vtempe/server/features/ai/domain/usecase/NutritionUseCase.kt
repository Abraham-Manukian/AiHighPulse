package com.vtempe.server.features.ai.domain.usecase

import com.vtempe.server.shared.dto.nutrition.AiNutritionRequest
import com.vtempe.server.shared.dto.nutrition.AiNutritionResponse

interface NutritionUseCase {
    suspend fun execute(req: AiNutritionRequest): AiNutritionResponse
}
