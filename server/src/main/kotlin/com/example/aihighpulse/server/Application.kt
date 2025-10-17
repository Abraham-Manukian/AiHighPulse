package com.example.aihighpulse.server

import com.example.aihighpulse.server.config.Env
import com.example.aihighpulse.server.llm.LLMClient
import com.example.aihighpulse.server.llm.OpenRouterLLMClient
import com.example.aihighpulse.server.llm.RetryingLLMClient
import com.example.aihighpulse.server.llm.ThrottledLLMClient
import com.example.aihighpulse.server.llm.StubLLMClient
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.plugins.cors.routing.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

private val startupLogger = LoggerFactory.getLogger("AiHighPulseServer")

fun main() {
    val port = Env["PORT"]?.toIntOrNull() ?: 8080
    embeddedServer(Netty, port = port) {
        install(CORS) { anyHost() }
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true; encodeDefaults = true }) }
        val llm = createLLMClient()
        val service = AiService(llm)
        val chatService = ChatService(llm, service)
        routing {
            post("/ai/training") { call.respond(service.training(call.receive())) }
            post("/ai/nutrition") { call.respond(service.nutrition(call.receive())) }
            post("/ai/sleep") { call.respond(service.sleep(call.receive())) }
            post("/ai/chat") { call.respond(chatService.chat(call.receive())) }
            post("/ai/bootstrap") { call.respond(chatService.bootstrap(call.receive())) }
            get("/health") { call.respondText("OK") }
        }
    }.start(wait = true)
}

private fun createLLMClient(): LLMClient {
    val openRouterKey = Env["OPENROUTER_API_KEY"]?.takeIf { it.isNotBlank() }
    if (openRouterKey != null) {
        val model = Env["OPENROUTER_MODEL"]?.takeIf { it.isNotBlank() } ?: "openrouter/auto"
        val baseUrl = Env["OPENROUTER_BASE_URL"]?.takeIf { it.isNotBlank() }
        val temperature = Env["OPENROUTER_TEMPERATURE"]?.toDoubleOrNull()
        val siteUrl = Env["OPENROUTER_SITE_URL"]?.takeIf { it.isNotBlank() }
        val appName = Env["OPENROUTER_APP_NAME"]?.takeIf { it.isNotBlank() }
        startupLogger.info("Registering OpenRouterLLMClient (model=$model)")
        val openRouter = OpenRouterLLMClient(
            apiKey = openRouterKey,
            model = model,
            baseUrl = baseUrl,
            temperature = temperature,
            siteUrl = siteUrl,
            appName = appName
        )
        val throttled = ThrottledLLMClient(openRouter, minSpacingMs = 2_500)
        return RetryingLLMClient(
            delegate = throttled,
            attempts = 1,
            initialDelayMs = 2_500,
            maxDelayMs = 15_000,
            backoffMultiplier = 1.8
        )
    }
    startupLogger.warn("OPENROUTER_API_KEY not provided; falling back to stubbed responses")
    return StubLLMClient("{\"reply\":\"Coach is offline right now. Please configure OPENROUTER_API_KEY.\"}")
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
    val injuries: List<String> = emptyList(),
    val healthNotes: List<String> = emptyList(),
    val budgetLevel: Int = 2,
)

@Serializable
data class AiTrainingRequest(val profile: AiProfile, val weekIndex: Int, val locale: String? = null)

@Serializable
data class AiSet(val exerciseId: String, val reps: Int, val weightKg: Double? = null, val rpe: Double? = null)

@Serializable
data class AiWorkout(val id: String, val date: String, val sets: List<AiSet>)

@Serializable
data class AiTrainingResponse(val weekIndex: Int, val workouts: List<AiWorkout>)

@Serializable
data class Macros(
    val proteinGrams: Int = 0,
    val fatGrams: Int = 0,
    val carbsGrams: Int = 0,
    val kcal: Int = 0
)

@Serializable
data class AiMeal(
    val name: String,
    val ingredients: List<String>,
    val kcal: Int = 0,
    val macros: Macros = Macros()
)

@Serializable
data class AiNutritionRequest(val profile: AiProfile, val weekIndex: Int, val locale: String? = null)

@Serializable
data class AiNutritionResponse(
    val weekIndex: Int,
    val mealsByDay: Map<String, List<AiMeal>>,
    val shoppingList: List<String> = emptyList()
)

@Serializable
data class AiAdviceRequest(val profile: AiProfile, val locale: String? = null)

@Serializable
data class AiAdviceResponse(val messages: List<String>, val disclaimer: String? = "Not medical advice")

@Serializable
data class AiChatMessage(val role: String, val content: String)

@Serializable
data class AiChatRequest(val profile: AiProfile, val messages: List<AiChatMessage>, val locale: String? = null)

@Serializable
data class AiChatResponse(val reply: String, val trainingPlan: AiTrainingResponse? = null, val nutritionPlan: AiNutritionResponse? = null, val sleepAdvice: AiAdviceResponse? = null)

@Serializable
data class AiBootstrapRequest(
    val profile: AiProfile,
    val weekIndex: Int = 0,
    val locale: String? = null
)

@Serializable
data class AiBootstrapResponse(
    val trainingPlan: AiTrainingResponse? = null,
    val nutritionPlan: AiNutritionResponse? = null,
    val sleepAdvice: AiAdviceResponse? = null
)







