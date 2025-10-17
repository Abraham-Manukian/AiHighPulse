package com.example.aihighpulse.shared.data.repo

import com.example.aihighpulse.shared.data.network.dto.AiBootstrapResponseDto
import com.example.aihighpulse.shared.data.network.dto.NutritionPlanDto
import com.example.aihighpulse.shared.data.network.dto.TrainingPlanDto
import com.example.aihighpulse.shared.data.network.dto.AdviceDto
import com.example.aihighpulse.shared.data.network.dto.ChatResponse
import com.russhwolf.settings.Settings
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

private const val KEY_TRAINING = "cache_training_plan"
private const val KEY_NUTRITION = "cache_nutrition_plan"
private const val KEY_ADVICE = "cache_sleep_advice"
private const val KEY_BUNDLE = "cache_bootstrap_bundle"
private const val KEY_CHAT = "cache_chat_response"

class AiResponseCache(
    private val settings: Settings,
    private val json: Json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
) {
    fun storeTraining(dto: TrainingPlanDto) {
        settings.putString(KEY_TRAINING, json.encodeToString(dto))
    }

    fun lastTraining(): TrainingPlanDto? =
        settings.getStringOrNull(KEY_TRAINING)?.let { runCatching { json.decodeFromString<TrainingPlanDto>(it) }.getOrNull() }

    fun storeNutrition(dto: NutritionPlanDto) {
        settings.putString(KEY_NUTRITION, json.encodeToString(dto))
    }

    fun lastNutrition(): NutritionPlanDto? =
        settings.getStringOrNull(KEY_NUTRITION)?.let { runCatching { json.decodeFromString<NutritionPlanDto>(it) }.getOrNull() }

    fun storeAdvice(dto: AdviceDto) {
        settings.putString(KEY_ADVICE, json.encodeToString(dto))
    }

    fun lastAdvice(): AdviceDto? =
        settings.getStringOrNull(KEY_ADVICE)?.let { runCatching { json.decodeFromString<AdviceDto>(it) }.getOrNull() }

    fun storeBundle(dto: AiBootstrapResponseDto) {
        settings.putString(KEY_BUNDLE, json.encodeToString(dto))
    }

    fun lastBundle(): AiBootstrapResponseDto? =
        settings.getStringOrNull(KEY_BUNDLE)?.let { runCatching { json.decodeFromString<AiBootstrapResponseDto>(it) }.getOrNull() }

    fun storeChatResponse(dto: ChatResponse) {
        settings.putString(KEY_CHAT, json.encodeToString(dto))
    }

    fun lastChatResponse(): ChatResponse? =
        settings.getStringOrNull(KEY_CHAT)?.let { runCatching { json.decodeFromString<ChatResponse>(it) }.getOrNull() }
}
