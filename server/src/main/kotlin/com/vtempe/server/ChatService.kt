package com.vtempe.server

import com.vtempe.server.llm.LLMClient
import com.vtempe.server.llm.LlmRepairer
import java.util.Locale
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlin.collections.buildList
import org.slf4j.LoggerFactory

class ChatService(
    private val llm: LLMClient,
    private val aiService: AiService
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    suspend fun chat(req: AiChatRequest): AiChatResponse {
        val localeTag = req.locale?.takeIf { it.isNotBlank() } ?: DefaultLocale
        val locale = runCatching { Locale.forLanguageTag(localeTag) }.getOrElse { Locale.ENGLISH }
        val languageDisplay = locale.getDisplayLanguage(locale).ifBlank { locale.language.ifBlank { "English" } }
        val profileJson = json.encodeToString(AiProfile.serializer(), req.profile)
        val profileSummary = buildChatProfileSummary(req.profile)
        val lastUserMessage = req.messages.lastOrNull { it.role.equals("user", ignoreCase = true) }?.content
            ?: req.messages.lastOrNull()?.content
            ?: ""
        val history = req.messages.joinToString("\n\n") { message ->
            "${message.role}: ${message.content}"
        }

        return runCatching {
            withTimeout(LlmTimeoutMs) {
                val basePrompt = buildChatPrompt(
                    localeTag = localeTag,
                    languageDisplay = languageDisplay,
                    profileJson = profileJson,
                    profileSummary = profileSummary,
                    lastUserMessage = lastUserMessage,
                    history = history
                )
                val profileHash = profileJson.hashCode()
                val requestId = "chat-$profileHash-${req.messages.size}-${System.currentTimeMillis()}"
                logger.debug(
                    "LLM {} requestId={} locale={} messages={} profileHash={}",
                    "chat",
                    requestId,
                    localeTag,
                    req.messages.size,
                    profileHash
                )
                val rawResponse = LlmRepairer.generate(
                    llm = llm,
                    locale = locale,
                    operation = "chat",
                    logger = logger,
                    requestId = requestId,
                    buildPrompt = { attempt, feedback -> withChatFeedback(basePrompt, attempt, feedback) },
                    decode = { raw -> json.decodeFromString(AiChatResponse.serializer(), raw) },
                    validate = { validateChatResponse(it) },
                    textExtractor = { response ->
                        buildList {
                            add(response.reply)
                            response.trainingPlan?.let { plan ->
                                addAll(plan.workouts.flatMap { workout ->
                                    buildList {
                                        add(workout.id)
                                        add(workout.date)
                                        addAll(workout.sets.map { "${it.exerciseId}:${it.reps}" })
                                    }
                                })
                            }
                            response.nutritionPlan?.let { plan ->
                                addAll(
                                    plan.mealsByDay.values.flatMap { meals ->
                                        meals.flatMap { meal ->
                                            buildList {
                                                add(meal.name)
                                                addAll(meal.ingredients)
                                            }
                                        }
                                    }
                                )
                                addAll(plan.shoppingList)
                            }
                            response.sleepAdvice?.let { advice ->
                                addAll(advice.messages)
                                advice.disclaimer?.let { add(it) }
                            }
                        }
                    },
                    maxAttempts = 3
                )
                normalizeChatResponse(rawResponse, locale)
            }
        }.getOrElse {
            logger.warn("LLM chat fallback triggered", it)
            AiChatResponse(reply = fallbackChatMessage(locale))
        }
    }

    suspend fun bootstrap(req: AiBootstrapRequest): AiBootstrapResponse = runCatching {
        aiService.bundle(req)
    }.getOrElse {
        logger.warn("Bootstrap bundle failed", it)
        AiBootstrapResponse(
            trainingPlan = aiService.training(AiTrainingRequest(req.profile, req.weekIndex, req.locale)),
            nutritionPlan = aiService.nutrition(AiNutritionRequest(req.profile, req.weekIndex, req.locale)),
            sleepAdvice = aiService.sleep(AiAdviceRequest(req.profile, req.locale))
        )
    }

    private fun buildChatPrompt(
        localeTag: String,
        languageDisplay: String,
        profileJson: String,
        profileSummary: String,
        lastUserMessage: String,
        history: String
    ): String = buildString {
        appendLine("You are a professional AI strength coach, nutritionist, and recovery expert guiding the same athlete long-term.")
        appendLine("User locale: $languageDisplay ($localeTag). Reply in that language and measurement system.")
        appendLine()
        appendLine("PROFILE CONTEXT (JSON):")
        appendLine(profileJson)
        appendLine()
        appendLine("KEY FACTS:")
        append(profileSummary)
        appendLine()
        appendLine("When replying: first acknowledge the latest user message, then provide clear next steps. Only update trainingPlan, nutritionPlan, or sleepAdvice when the user explicitly requests changes or new plans; otherwise return null for unchanged sections.")
        appendLine("Return STRICT JSON matching this schema (no comments or extra text):")
        appendLine("{\"reply\": String,")
        appendLine(" \"trainingPlan\": {\"weekIndex\": Int, \"workouts\": [{ \"id\": String, \"date\": String(YYYY-MM-DD), \"sets\": [{ \"exerciseId\": String, \"reps\": Int, \"weightKg\": Double?, \"rpe\": Double? }] }] } | null,")
        appendLine(" \"nutritionPlan\": {\"weekIndex\": Int, \"mealsByDay\": { DayLabel: [{ \"name\": String, \"ingredients\": [String], \"kcal\": Int, \"macros\": { \"proteinGrams\": Int, \"fatGrams\": Int, \"carbsGrams\": Int, \"kcal\": Int } }] }, \"shoppingList\": [String]} | null,")
        appendLine(" \"sleepAdvice\": {\"messages\": [String], \"disclaimer\": String?} | null }")
        appendLine()
        appendLine("Guidelines:")
        appendLine("- Set plan sections to null when no update is required; never return empty arrays to signal no change.")
        appendLine("- Nutrition updates MUST include integer macros fields in every meal object. Example: {\"name\":\"Power Oats\",\"ingredients\":[\"rolled oats\",\"milk\",\"berries\"],\"kcal\":420,\"macros\":{\"proteinGrams\":35,\"fatGrams\":12,\"carbsGrams\":55,\"kcal\":420}}")
        appendLine("- Ensure macros.kcal equals proteinGrams*4 + carbsGrams*4 + fatGrams*9 (+/- 20 kcal). Fix kcal rather than omitting fields.")
        appendLine("Do not include trailing commas or comments; output must be valid JSON.")
        appendLine("Latest user message: \"$lastUserMessage\".")
        appendLine("Conversation so far:")
        if (history.isNotBlank()) {
            appendLine(history)
        } else {
            appendLine("No previous messages provided.")
        }
    }

    private fun withChatFeedback(basePrompt: String, attempt: Int, feedback: String?): String = buildString {
        append(basePrompt)
        if (feedback != null) {
            appendLine()
            appendLine("Previous attempt issue (#${attempt - 1}): $feedback")
            appendLine("Return only corrected JSON that satisfies the schema.")
        }
    }

    companion object {
        private const val LlmTimeoutMs = 90_000L
        private const val DefaultLocale = "en-US"
        private val logger = LoggerFactory.getLogger(ChatService::class.java)
    }
}

private fun buildChatProfileSummary(profile: AiProfile): String = buildString {
    val weightFormatted = String.format(Locale.US, "%.1f", profile.weightKg)
    appendLine("- Demographics: ${profile.age} y/o ${profile.sex.lowercase(Locale.US)} | ${profile.heightCm} cm | $weightFormatted kg")
    appendLine("- Goal: ${profile.goal}")
    appendLine("- Experience level (1-5): ${profile.experienceLevel}")
    val equipment = if (profile.equipment.isNotEmpty()) profile.equipment.joinToString(", ") else "bodyweight only"
    appendLine("- Available equipment: $equipment")
    if (profile.injuries.isNotEmpty()) appendLine("- Injuries / limitations: ${profile.injuries.joinToString(", ")}")
    if (profile.healthNotes.isNotEmpty()) appendLine("- Contraindications: ${profile.healthNotes.joinToString(", ")}")
    if (profile.weeklySchedule.isNotEmpty()) {
        val available = profile.weeklySchedule.filterValues { it }.keys
        if (available.isNotEmpty()) appendLine("- Preferred training days: ${available.joinToString(", ")}")
    }
    if (profile.dietaryPreferences.isNotEmpty()) appendLine("- Dietary preferences: ${profile.dietaryPreferences.joinToString(", ")}")
    if (profile.allergies.isNotEmpty()) appendLine("- Allergies: ${profile.allergies.joinToString(", ")}")
    append("- Nutrition budget level (1 low .. 3 high): ${profile.budgetLevel}")
}



private fun fallbackChatMessage(locale: Locale): String =
    if (locale.language.equals("ru", ignoreCase = true)) {
        "\u0422\u0440\u0435\u043d\u0435\u0440 \u0432\u0440\u0435\u043c\u0435\u043d\u043d\u043e \u043d\u0435\u0434\u043e\u0441\u0442\u0443\u043f\u0435\u043d. \u041f\u043e\u0436\u0430\u043b\u0443\u0439\u0441\u0442\u0430, \u043f\u043e\u043f\u0440\u043e\u0431\u0443\u0439\u0442\u0435 \u0435\u0449\u0451 \u0440\u0430\u0437 \u0447\u0443\u0442\u044c \u043f\u043e\u0437\u0436\u0435."
    } else {
        "Coach is temporarily unavailable. Please try again later."
    }

internal fun validateChatResponse(response: AiChatResponse): String? {
    if (response.reply.isBlank()) return "reply must contain user-facing text"
    response.trainingPlan?.let { plan ->
        validateTrainingPlan(plan)?.let { return "trainingPlan: $it" }
    }
    response.nutritionPlan?.let { plan ->
        validateNutritionPlan(plan)?.let { return "nutritionPlan: $it" }
    }
    response.sleepAdvice?.let { advice ->
        validateSleepAdvice(advice)?.let { return "sleepAdvice: $it" }
    }
    return null
}

private fun normalizeChatResponse(response: AiChatResponse, locale: Locale): AiChatResponse = response.copy(
    trainingPlan = response.trainingPlan?.let(::normalizeTrainingPlan),
    nutritionPlan = response.nutritionPlan?.let { normalizeNutritionPlan(it, locale) },
    sleepAdvice = response.sleepAdvice?.let(::normalizeAdvice)
)

