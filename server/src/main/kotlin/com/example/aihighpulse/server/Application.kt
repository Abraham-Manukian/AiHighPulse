package com.example.aihighpulse.server

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.plugins.cors.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = (System.getenv("PORT") ?: "8080").toInt()) {
        // CallLogging and RateLimit can be enabled later
        install(CORS) { anyHost() }
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true; encodeDefaults = true }) }
        val llm = com.example.aihighpulse.server.llm.OllamaLLMClient()
        val service = AiService(llm)
        routing {
            post("/ai/training") { call.respond(service.training(call.receive())) }
            post("/ai/nutrition") { call.respond(service.nutrition(call.receive())) }
            post("/ai/sleep") { call.respond(service.sleep(call.receive())) }
            get("/health") { call.respondText("OK") }
        }
    }.start(wait = true)
}

@Serializable
data class AiProfile(
    val age: Int,
    val sex: String,
    val heightCm: Int,
    val weightKg: Double,
    val goal: String,
    val experienceLevel: Int,
    val equipment: List<String> = emptyList(),
    val dietaryPreferences: List<String> = emptyList(),
    val allergies: List<String> = emptyList(),
    val weeklySchedule: Map<String, Boolean> = emptyMap(),
)

@Serializable
data class AiTrainingRequest(val profile: AiProfile, val weekIndex: Int)

@Serializable
data class AiSet(val exerciseId: String, val reps: Int, val weightKg: Double? = null, val rpe: Double? = null)

@Serializable
data class AiWorkout(val id: String, val date: String, val sets: List<AiSet>)

@Serializable
data class AiTrainingResponse(val weekIndex: Int, val workouts: List<AiWorkout>)

@Serializable
data class Macros(val proteinGrams: Int, val fatGrams: Int, val carbsGrams: Int, val kcal: Int)

@Serializable
data class AiMeal(val name: String, val ingredients: List<String>, val kcal: Int, val macros: Macros)

@Serializable
data class AiNutritionRequest(val profile: AiProfile, val weekIndex: Int)

@Serializable
data class AiNutritionResponse(val weekIndex: Int, val mealsByDay: Map<String, List<AiMeal>>)

@Serializable
data class AiAdviceRequest(val profile: AiProfile)

@Serializable
data class AiAdviceResponse(val messages: List<String>, val disclaimer: String? = "Not medical advice")

