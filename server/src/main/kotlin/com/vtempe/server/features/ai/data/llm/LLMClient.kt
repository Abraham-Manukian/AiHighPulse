package com.vtempe.server.features.ai.data.llm

interface LLMClient {
    suspend fun generateJson(prompt: String): String
}


