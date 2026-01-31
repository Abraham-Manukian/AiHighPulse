package com.vtempe.server.llm

interface LLMClient {
    suspend fun generateJson(prompt: String): String
}


