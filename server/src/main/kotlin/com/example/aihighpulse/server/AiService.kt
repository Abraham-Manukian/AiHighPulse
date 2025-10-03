package com.example.aihighpulse.server

import com.example.aihighpulse.server.llm.LLMClient
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.ZoneOffset

class AiService(private val llm: LLMClient) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun training(req: AiTrainingRequest): AiTrainingResponse = runWithFallback(
        operation = "training",
        fallback = { fallbackTraining(req) }
    ) {
        val prompt = buildString {
            appendLine("You are an expert strength coach and data engineer.")
            appendLine("Task: Generate a weekly training plan as STRICT JSON matching this Kotlin schema:")
            appendLine("{\"weekIndex\": Int, \"workouts\": [ { \"id\": String, \"date\": String(YYYY-MM-DD), \"sets\": [ { \"exerciseId\": String, \"reps\": Int, \"weightKg\": Double?, \"rpe\": Double? } ] } ] }")
            appendLine("Rules: use only exerciseId from this allowed list: [squat, bench, deadlift, ohp, row, pullup, lunge].")
            appendLine("Reps 4-12, weight in kilograms or null for bodyweight, RPE 6.5-9.0.")
            appendLine("Return ONLY JSON, no commentary.")
            appendLine("Profile JSON:")
            appendLine(json.encodeToString(AiTrainingRequest.serializer(), req))
        }
        val out = llm.generateJson(prompt)
        json.decodeFromString(AiTrainingResponse.serializer(), out)
    }

    suspend fun nutrition(req: AiNutritionRequest): AiNutritionResponse = runWithFallback(
        operation = "nutrition",
        fallback = { fallbackNutrition(req) }
    ) {
        val prompt = buildString {
            appendLine("You are a sports nutritionist.")
            appendLine("Task: Generate a weekly menu as STRICT JSON matching schema:")
            appendLine("{\"weekIndex\": Int, \"mealsByDay\": { \"Mon\": [ { \"name\": String, \"ingredients\": [String], \"kcal\": Int, \"macros\": { \"proteinGrams\": Int, \"fatGrams\": Int, \"carbsGrams\": Int, \"kcal\": Int } } ] , ... } }")
            appendLine("Respect dietaryPreferences and allergies. Use realistic kcal/macros.")
            appendLine("Return ONLY JSON, no commentary.")
            appendLine("Profile JSON:")
            appendLine(json.encodeToString(AiNutritionRequest.serializer(), req))
        }
        val out = llm.generateJson(prompt)
        json.decodeFromString(AiNutritionResponse.serializer(), out)
    }

    suspend fun sleep(req: AiAdviceRequest): AiAdviceResponse = runWithFallback(
        operation = "sleep",
        fallback = { fallbackAdvice(req) }
    ) {
        val prompt = buildString {
            appendLine("You are a sleep coach.")
            appendLine("Task: Provide 5-7 concise personalized sleep tips as STRICT JSON:")
            appendLine("{\"messages\": [String], \"disclaimer\": String}")
            appendLine("Return ONLY JSON, no commentary.")
            appendLine("Profile JSON:")
            appendLine(json.encodeToString(AiAdviceRequest.serializer(), req))
        }
        val out = llm.generateJson(prompt)
        json.decodeFromString(AiAdviceResponse.serializer(), out)
    }

    private suspend fun <T> runWithFallback(
        operation: String,
        fallback: () -> T,
        block: suspend () -> T
    ): T = runCatching {
        withTimeout(LlmTimeoutMs) { block() }
    }.getOrElse {
        logger.warn("LLM ${'$'}operation fallback triggered", it)
        fallback()
    }

    private fun fallbackTraining(req: AiTrainingRequest): AiTrainingResponse {
        val today = LocalDate.now(ZoneOffset.UTC)
        val workouts = List(3) { day ->
            val id = "w_${'$'}{req.weekIndex}_${'$'}day"
            val sets = when (day) {
                0 -> listOf(AiSet("squat", 8, 40.0, 7.5), AiSet("bench", 10, 30.0, 7.0))
                1 -> listOf(AiSet("deadlift", 5, 60.0, 7.5), AiSet("ohp", 8, 20.0, 7.0))
                else -> listOf(AiSet("row", 10, 30.0, 7.0), AiSet("pullup", 6, null, 7.0))
            }
            AiWorkout(id, today.toString(), sets)
        }
        return AiTrainingResponse(req.weekIndex, workouts)
    }

    private fun fallbackNutrition(req: AiNutritionRequest): AiNutritionResponse {
        val totalKcal = 2100
        val kcalPerMeal = totalKcal / 3
        val macrosPerMeal = Macros(proteinGrams = 35, fatGrams = 18, carbsGrams = 65, kcal = kcalPerMeal)
        val mealSet = listOf(
            AiMeal("Oatmeal with Banana", listOf("oats", "milk", "banana"), kcalPerMeal, macrosPerMeal),
            AiMeal("Chicken and Rice", listOf("chicken", "rice", "vegetables"), kcalPerMeal, macrosPerMeal),
            AiMeal("Yogurt and Nuts", listOf("yogurt", "almonds"), kcalPerMeal, macrosPerMeal)
        )
        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        return AiNutritionResponse(req.weekIndex, days.associateWith { mealSet })
    }

    private fun fallbackAdvice(@Suppress("UNUSED_PARAMETER") req: AiAdviceRequest): AiAdviceResponse =
        AiAdviceResponse(
            messages = listOf(
                "Keep a consistent sleep schedule, even on weekends.",
                "Limit screens and bright light in the last hour before bed.",
                "Aim for a cool, dark, quiet bedroom environment.",
                "Avoid heavy meals and stimulants at least 3 hours before sleep.",
                "Wind down with light stretching or breathing exercises.",
                "Track your energy and adjust training loads on low sleep days."
            ),
            disclaimer = "Coaching tips only. Consult a medical professional for ongoing issues."
        )

    companion object {
        private const val LlmTimeoutMs = 60_000L
        private val logger = LoggerFactory.getLogger(AiService::class.java)
    }
}