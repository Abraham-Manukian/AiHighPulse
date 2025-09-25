package com.example.aihighpulse.server.llm

import com.example.aihighpulse.server.llm.dto.ChatCompletionRequestDto
import com.example.aihighpulse.server.llm.dto.ChatCompletionResponseDto
import com.example.aihighpulse.server.llm.dto.ChatMessageDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val REQUEST_TIMEOUT_MS = 90_000L
private const val SOCKET_TIMEOUT_MS = 90_000L
private const val CONNECT_TIMEOUT_MS = 20_000L
private const val MAX_TOKENS = 512

class OpenRouterLLMClient(
    apiKey: String,
    model: String = DEFAULT_MODEL,
    baseUrl: String? = null,
    temperature: Double? = null,
    siteUrl: String? = null,
    appName: String? = null,
) : LLMClient {
    private val resolvedBaseUrl = (baseUrl ?: DEFAULT_BASE_URL).trimEnd('/')
    private val resolvedTemperature = temperature ?: DEFAULT_TEMPERATURE
    private val resolvedSiteUrl = siteUrl ?: DEFAULT_SITE_URL
    private val resolvedAppName = appName ?: DEFAULT_APP_NAME
    private val resolvedModel = model

    private val http = HttpClient(OkHttp) {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        install(HttpTimeout) {
            requestTimeoutMillis = REQUEST_TIMEOUT_MS
            socketTimeoutMillis = SOCKET_TIMEOUT_MS
            connectTimeoutMillis = CONNECT_TIMEOUT_MS
        }
        defaultRequest {
            bearerAuth(apiKey)
            header("HTTP-Referer", resolvedSiteUrl)
            header("X-Title", resolvedAppName)
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header(HttpHeaders.UserAgent, "AiHighPulseServer/1.0")
        }
    }

    override suspend fun generateJson(prompt: String): String {
        val body = ChatCompletionRequestDto(
            model = resolvedModel,
            messages = listOf(
                ChatMessageDto(role = "system", content = SYSTEM_PROMPT),
                ChatMessageDto(role = "user", content = prompt)
            ),
            temperature = resolvedTemperature,
            stream = false,
            maxTokens = MAX_TOKENS
        )
        val response: ChatCompletionResponseDto = http.post("$resolvedBaseUrl/chat/completions") { setBody(body) }.body()
        response.error?.let {
            throw IllegalStateException("OpenRouter error ${it.codeAsString}: ${it.message}")
        }
        return response.choices.firstOrNull()?.message?.content
            ?: error("OpenRouter response did not contain choices")
    }

    companion object {
        private const val DEFAULT_MODEL = "openrouter/auto"
        private const val DEFAULT_BASE_URL = "https://openrouter.ai/api/v1"
        private const val DEFAULT_TEMPERATURE = 0.4
        private const val DEFAULT_SITE_URL = "https://github.com/example/aihighpulse"
        private const val DEFAULT_APP_NAME = "AiHighPulse"
        private const val SYSTEM_PROMPT = "You are a helpful assistant that returns valid JSON responses."
    }
}
