package com.vtempe.server.shared.dto.chat

import com.vtempe.server.shared.dto.profile.AiProfile
import com.vtempe.server.shared.dto.training.AiTrainingResponse
import com.vtempe.server.shared.dto.nutrition.AiNutritionResponse
import com.vtempe.server.shared.dto.advice.AiAdviceResponse
import kotlinx.serialization.Serializable

@Serializable
data class AiChatMessage(
    val role: String,
    val content: String,
)

@Serializable
data class AiChatRequest(
    val profile: AiProfile,
    val messages: List<AiChatMessage>,
    val locale: String? = null,
)

@Serializable
data class AiChatResponse(
    val reply: String,
    val trainingPlan: AiTrainingResponse? = null,
    val nutritionPlan: AiNutritionResponse? = null,
    val sleepAdvice: AiAdviceResponse? = null,
)
