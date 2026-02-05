package com.vtempe.server.shared.dto.bootstrap

import com.vtempe.server.shared.dto.profile.AiProfile
import com.vtempe.server.shared.dto.training.AiTrainingResponse
import com.vtempe.server.shared.dto.nutrition.AiNutritionResponse
import com.vtempe.server.shared.dto.advice.AiAdviceResponse
import kotlinx.serialization.Serializable

@Serializable
data class AiBootstrapRequest(
    val profile: AiProfile,
    val weekIndex: Int = 0,
    val locale: String? = null,
)

@Serializable
data class AiBootstrapResponse(
    val trainingPlan: AiTrainingResponse? = null,
    val nutritionPlan: AiNutritionResponse? = null,
    val sleepAdvice: AiAdviceResponse? = null,
)
