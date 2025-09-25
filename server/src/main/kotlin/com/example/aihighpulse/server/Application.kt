package com.example.aihighpulse.server

import com.example.aihighpulse.server.config.Env
import com.example.aihighpulse.server.llm.LLMClient
import com.example.aihighpulse.server.llm.OpenRouterLLMClient
import com.example.aihighpulse.server.llm.RetryingLLMClient
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
        warmUpLLM(llm)
        val chatService = ChatService(llm, service)
        routing {
            post("/ai/training") { call.respond(service.training(call.receive())) }
            post("/ai/nutrition") { call.respond(service.nutrition(call.receive())) }
            post("/ai/sleep") { call.respond(service.sleep(call.receive())) }
            post("/ai/chat") { call.respond(chatService.chat(call.receive())) }
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
        return RetryingLLMClient(openRouter, attempts = 3, delayMs = 1500)
    }
    startupLogger.warn("OPENROUTER_API_KEY not provided; falling back to stubbed responses")
    return StubLLMClient("{\"reply\":\"Coach is offline right now. Please configure OPENROUTER_API_KEY.\"}")
}

private fun warmUpLLM(llm: LLMClient) {
    runBlocking {
        val result = withTimeoutOrNull(20_000L) {
            runCatching { llm.generateJson("Warm-up ping") }
                .onSuccess { startupLogger.info("LLM warm-up completed") }
                .onFailure { startupLogger.warn("LLM warm-up failed", it) }
        }
        if (result == null) {
            startupLogger.warn("LLM warm-up timed out")
        }
    }
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

@Serializable
data class AiChatMessage(val role: String, val content: String)

@Serializable
data class AiChatRequest(val profile: AiProfile, val messages: List<AiChatMessage>, val locale: String? = null)

@Serializable
data class AiChatResponse(val reply: String)
