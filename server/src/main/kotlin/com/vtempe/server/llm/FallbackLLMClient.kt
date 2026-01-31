package com.vtempe.server.llm

import org.slf4j.LoggerFactory

class FallbackLLMClient(private val clients: List<LLMClient>) : LLMClient {
    init {
        require(clients.isNotEmpty()) { "FallbackLLMClient requires at least one LLMClient" }
    }

    override suspend fun generateJson(prompt: String): String {
        var lastError: Throwable? = null
        for (client in clients) {
            val name = client::class.simpleName ?: client::class.java.simpleName
            try {
                logger.debug("Trying LLM client: {}", name)
                return client.generateJson(prompt)
            } catch (t: Throwable) {
                logger.warn("LLM client {} failed: {}", name, t.message)
                logger.debug("LLM client {} stack", name, t)
                lastError = t
            }
        }
        throw lastError ?: IllegalStateException("All LLM clients failed")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(FallbackLLMClient::class.java)
    }
}

