package com.vtempe.server.app.di

import com.vtempe.server.features.ai.data.usecase.BootstrapUseCaseImpl
import com.vtempe.server.features.ai.data.usecase.ChatUseCaseImpl
import com.vtempe.server.features.ai.data.usecase.NutritionUseCaseImpl
import com.vtempe.server.features.ai.data.usecase.SleepUseCaseImpl
import com.vtempe.server.features.ai.data.usecase.TrainingUseCaseImpl
import com.vtempe.server.features.ai.domain.usecase.BootstrapUseCase
import com.vtempe.server.features.ai.domain.usecase.ChatUseCase
import com.vtempe.server.features.ai.domain.usecase.NutritionUseCase
import com.vtempe.server.features.ai.domain.usecase.SleepUseCase
import com.vtempe.server.features.ai.domain.usecase.TrainingUseCase
import com.vtempe.server.features.ai.data.service.AiService
import com.vtempe.server.features.ai.data.service.ChatService
import com.vtempe.server.features.ai.data.llm.LLMClient
import com.vtempe.server.features.ai.data.llm.OpenRouterLLMClient
import com.vtempe.server.features.ai.data.llm.RetryingLLMClient
import com.vtempe.server.features.ai.data.llm.StubLLMClient
import com.vtempe.server.features.ai.data.llm.ThrottledLLMClient
import com.vtempe.server.config.Env
import org.koin.dsl.module
import org.slf4j.LoggerFactory

val serverModule = module {

    single<LLMClient> {
        val startupLogger = LoggerFactory.getLogger("V-TempeServer")
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

            RetryingLLMClient(
                delegate = throttled,
                attempts = 1,
                initialDelayMs = 2_500,
                maxDelayMs = 15_000,
                backoffMultiplier = 1.8
            )
        } else {
            startupLogger.warn("OPENROUTER_API_KEY not provided; falling back to stubbed responses")
            StubLLMClient("{\"reply\":\"Coach is offline right now. Please configure OPENROUTER_API_KEY.\"}")
        }
    }

    single { AiService(llm = get()) }
    single { ChatService(llm = get(), aiService = get()) }

    // usecases (domain ports)
    single<TrainingUseCase> { TrainingUseCaseImpl(aiService = get()) }
    single<NutritionUseCase> { NutritionUseCaseImpl(aiService = get()) }
    single<SleepUseCase> { SleepUseCaseImpl(aiService = get()) }
    single<ChatUseCase> { ChatUseCaseImpl(chatService = get()) }
    single<BootstrapUseCase> { BootstrapUseCaseImpl(aiService = get()) }
}
