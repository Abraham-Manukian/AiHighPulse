package com.example.aihighpulse.server

import com.example.aihighpulse.server.llm.LLMClient
import java.util.Locale
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

class ChatService(
    private val llm: LLMClient,
    private val aiService: AiService
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun chat(req: AiChatRequest): AiChatResponse = runCatching {
        withTimeout(LlmTimeoutMs) {
            val localeTag = req.locale?.takeIf { it.isNotBlank() } ?: DefaultLocale
            val locale = runCatching { Locale.forLanguageTag(localeTag) }.getOrElse { Locale.ENGLISH }
            val languageDisplay = locale.getDisplayLanguage(locale).ifBlank { locale.language.ifBlank { "English" } }

            val lastUserMessage = req.messages.lastOrNull { it.role.equals("user", ignoreCase = true) }?.content
                ?: req.messages.lastOrNull()?.content
                ?: ""

            val history = req.messages.joinToString("\n\n") { message ->
                "${message.role}: ${message.content}"
            }

            val prompt = buildString {
                appendLine("You are a professional AI strength coach, nutritionist, and recovery expert guiding the same user over time.")
                appendLine("User locale: $languageDisplay ($localeTag). Reply in that language and measurement system.")
                appendLine("Return STRICT JSON that matches this Kotlin data class (no extra text):")
                appendLine("{\"reply\": String,")
                appendLine(" \"trainingPlan\": {\"weekIndex\": Int, \"workouts\": [{ \"id\": String, \"date\": String(YYYY-MM-DD), \"sets\": [{ \"exerciseId\": String, \"reps\": Int, \"weightKg\": Double?, \"rpe\": Double? }] }] } | null,")
                appendLine(" \"nutritionPlan\": {\"weekIndex\": Int, \"mealsByDay\": { DayLabel: [{ \"name\": String, \"ingredients\": [String], \"kcal\": Int, \"macros\": { \"proteinGrams\": Int, \"fatGrams\": Int, \"carbsGrams\": Int, \"kcal\": Int } }] } } | null,")
                appendLine(" \"sleepAdvice\": {\"messages\": [String], \"disclaimer\": String?} | null }")
                appendLine("If you do not want to update a section, set it to null.")
                appendLine("Latest user message: \"$lastUserMessage\". Address it in the reply first.")
                appendLine("Conversation so far:")
                if (history.isNotBlank()) {
                    appendLine(history)
                } else {
                    appendLine("No previous messages provided.")
                }
            }

            val raw = llm.generateJson(prompt)
            runCatching {
                json.decodeFromString(AiChatResponse.serializer(), raw)
            }.getOrElse {
                logger.warn("Failed to decode assistant response as JSON, using raw text. Raw={}", raw)
                AiChatResponse(reply = raw.trim().trim('"'))
            }
        }
    }.getOrElse {
        logger.warn("LLM chat fallback triggered", it)
        AiChatResponse(reply = "Coach is temporarily unavailable. Please try again later.")
    }

    suspend fun bootstrap(req: AiBootstrapRequest): AiBootstrapResponse = coroutineScope {
        val trainingDeferred = async {
            runCatching { aiService.training(AiTrainingRequest(req.profile, req.weekIndex)) }
                .onFailure { logger.warn("Bootstrap training failed", it) }
                .getOrNull()
        }
        val nutritionDeferred = async {
            runCatching { aiService.nutrition(AiNutritionRequest(req.profile, req.weekIndex)) }
                .onFailure { logger.warn("Bootstrap nutrition failed", it) }
                .getOrNull()
        }
        val sleepDeferred = async {
            runCatching { aiService.sleep(AiAdviceRequest(req.profile)) }
                .onFailure { logger.warn("Bootstrap sleep failed", it) }
                .getOrNull()
        }
        AiBootstrapResponse(
            trainingPlan = trainingDeferred.await(),
            nutritionPlan = nutritionDeferred.await(),
            sleepAdvice = sleepDeferred.await()
        )
    }

    companion object {
        private const val LlmTimeoutMs = 90_000L
        private const val DefaultLocale = "en-US"
        private val logger = LoggerFactory.getLogger(ChatService::class.java)
    }
}
