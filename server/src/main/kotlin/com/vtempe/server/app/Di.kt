package com.vtempe.server.app

import com.vtempe.server.features.ai.data.AskAiTrainerUseCaseImpl
import com.vtempe.server.features.ai.data.GenerateCoachBundleUseCaseImpl
import com.vtempe.server.features.ai.domain.usecase.AskAiTrainerUseCase
import com.vtempe.server.features.ai.domain.usecase.GenerateCoachBundleUseCase
import org.koin.dsl.module

val appModule = module {
    // usecases
    single<GenerateCoachBundleUseCase> { GenerateCoachBundleUseCaseImpl(aiService = get()) }
    single<AskAiTrainerUseCase> { AskAiTrainerUseCaseImpl(chatService = get()) }

    // AiService/ChatService и LLM клиент у тебя уже где-то создаются —
    // оставляем как есть, просто убедись что они зарегистрированы single { ... }
}
