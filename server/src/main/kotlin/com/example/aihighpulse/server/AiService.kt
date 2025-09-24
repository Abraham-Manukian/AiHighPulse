package com.example.aihighpulse.server

import com.example.aihighpulse.server.llm.LLMClient
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.ZoneOffset

class AiService(private val llm: LLMClient) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun training(req: AiTrainingRequest): AiTrainingResponse {
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
        return runCatching { json.decodeFromString(AiTrainingResponse.serializer(), out) }
            .getOrElse { fallbackTraining(req) }
    }

    suspend fun nutrition(req: AiNutritionRequest): AiNutritionResponse {
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
        return runCatching { json.decodeFromString(AiNutritionResponse.serializer(), out) }
            .getOrElse { fallbackNutrition(req) }
    }

    suspend fun sleep(req: AiAdviceRequest): AiAdviceResponse {
        val prompt = buildString {
            appendLine("You are a sleep coach.")
            appendLine("Task: Provide 5-7 concise personalized sleep tips as STRICT JSON:")
            appendLine("{\"messages\": [String], \"disclaimer\": String}")
            appendLine("Return ONLY JSON, no commentary.")
            appendLine("Profile JSON:")
            appendLine(json.encodeToString(AiAdviceRequest.serializer(), req))
        }
        val out = llm.generateJson(prompt)
        return runCatching { json.decodeFromString(AiAdviceResponse.serializer(), out) }
            .getOrElse { fallbackAdvice(req) }
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
        val kcal = 2200
        val mPer = Macros(120, 60, 250, kcal / 3)
        val mealSet = listOf(
            AiMeal("Овсянка", listOf("овсянка", "молоко", "банан"), kcal / 3, mPer),
            AiMeal("Курица с рисом", listOf("курица", "рис", "овощи"), kcal / 3, mPer),
            AiMeal("Йогурт с орехами", listOf("йогурт", "орехи"), kcal / 3, mPer)
        )
        val days = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")
        return AiNutritionResponse(req.weekIndex, days.associateWith { mealSet })
    }

    private fun fallbackAdvice(@Suppress("UNUSED_PARAMETER") req: AiAdviceRequest): AiAdviceResponse =
        AiAdviceResponse(
            messages = listOf(
                "Ложитесь и вставайте в одно и то же время",
                "Избегайте экранов за 60 минут до сна",
                "Держите спальню прохладной (18–20°C)",
                "Лёгкая прогулка и растяжка вечером",
                "Не тренируйтесь интенсивно за 3–4 часа до сна"
            )
        )
}
