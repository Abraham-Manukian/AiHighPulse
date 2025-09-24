package com.example.aihighpulse.server.llm

interface LLMClient {
    suspend fun generateJson(prompt: String): String
}

