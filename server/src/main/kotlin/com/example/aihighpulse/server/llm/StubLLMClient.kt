package com.example.aihighpulse.server.llm

class StubLLMClient(private val message: String) : LLMClient {
    override suspend fun generateJson(prompt: String): String = message
}
