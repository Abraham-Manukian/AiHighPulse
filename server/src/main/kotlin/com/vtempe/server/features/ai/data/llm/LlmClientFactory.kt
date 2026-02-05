package com.vtempe.server.features.ai.data.llm

import com.vtempe.server.config.Env
import org.slf4j.LoggerFactory

object LlmClientFactory {
    private val startupLogger = LoggerFactory.getLogger("V-TempeServer")

    fun createFromEnv(): LLMClient {
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
}
