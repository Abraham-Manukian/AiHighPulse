package com.example.aihighpulse.server

import com.example.aihighpulse.server.llm.LLMClient
import java.util.Locale
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.encodeToString
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

            val trainingPlan = fetchTrainingPlan(req)
            val nutritionPlan = fetchNutritionPlan(req)
            val sleepAdvice = fetchSleepAdvice(req)

            val history = req.messages.joinToString("\n\n") { m -> "${'$'}{m.role.uppercase()}: ${'$'}{m.content}" }

            val prompt = buildString {
                appendLine("You are a professional AI strength coach and nutritionist guiding the same user over time.")
                appendLine("User locale: ${'$'}localeTag ($languageDisplay). Use locale-appropriate measurement units and language.")
                appendLine("Latest user message: \"${lastUserMessage}\" — respond to it first.")
                appendLine("Baseline plans (use as reference, only repeat parts that change):")
                trainingPlan?.let {
                    appendLine("TrainingPlanJSON:")
                    appendLine(json.encodeToString(AiTrainingResponse.serializer(), it))
                }
                nutritionPlan?.let {
                    appendLine("NutritionPlanJSON:")
                    appendLine(json.encodeToString(AiNutritionResponse.serializer(), it))
                }
                sleepAdvice?.let {
                    appendLine("SleepAdviceJSON:")
                    appendLine(json.encodeToString(AiAdviceResponse.serializer(), it))
                }
                appendLine("Guidelines:")
                appendLine("- Personalize training, nutrition, recovery, and pain management based on the latest message and conversation history.")
                appendLine("- Track consistency, soreness, stress, and nutrition compliance. Highlight progress and recommend adjustments.")
                appendLine("- For pain/injury, suggest form tweaks, deloads, mobility work, and remind them to consult a medical professional; never diagnose.")
                appendLine("- For training updates, provide specific exercises, sets, reps, tempo/rest, and progression or deload suggestions.")
                appendLine("- For nutrition, update calories/macros, meal swaps, hydration, and lifestyle habits.")
                appendLine("- Avoid dumping the entire baseline plan repeatedly; only summarize changes or key focus areas.")
                appendLine("- Always close with 1–3 concise next actions for the user.")
                appendLine("Conversation so far:\n$history")
                appendLine("Task: reply in natural language (no JSON wrapper). Keep tone supportive, expert, and actionable.")
            }

            val raw = llm.generateJson(prompt)
            val reply = runCatching {
                json.decodeFromString(AiChatResponse.serializer(), raw).reply
            }.getOrElse {
                logger.warn("Failed to decode assistant response as JSON, using raw text. Raw={}", raw)
                raw.trim().trim('"')
            }
            AiChatResponse(reply = reply)
        }
    }.getOrElse {
        logger.warn("LLM chat fallback triggered", it)
        AiChatResponse(reply = "Coach is temporarily unavailable. Please try again later.")
    }

    private suspend fun fetchTrainingPlan(req: AiChatRequest): AiTrainingResponse? =
        withTimeoutOrNull(ContextTimeoutMs) {
            runCatching { aiService.training(AiTrainingRequest(req.profile, weekIndex = 0)) }
                .onFailure { logger.debug("Training plan unavailable: {}", it.message) }
                .getOrNull()
        }

    private suspend fun fetchNutritionPlan(req: AiChatRequest): AiNutritionResponse? =
        withTimeoutOrNull(ContextTimeoutMs) {
            runCatching { aiService.nutrition(AiNutritionRequest(req.profile, weekIndex = 0)) }
                .onFailure { logger.debug("Nutrition plan unavailable: {}", it.message) }
                .getOrNull()
        }

    private suspend fun fetchSleepAdvice(req: AiChatRequest): AiAdviceResponse? =
        withTimeoutOrNull(ContextTimeoutMs) {
            runCatching { aiService.sleep(AiAdviceRequest(req.profile)) }
                .onFailure { logger.debug("Sleep advice unavailable: {}", it.message) }
                .getOrNull()
        }

    companion object {
        private const val LlmTimeoutMs = 45_000L
        private const val ContextTimeoutMs = 12_000L
        private const val DefaultLocale = "en-US"
        private val logger = LoggerFactory.getLogger(ChatService::class.java)
    }
}
