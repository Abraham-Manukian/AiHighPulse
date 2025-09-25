package com.example.aihighpulse.server.llm

import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory

class RetryingLLMClient(
    private val delegate: LLMClient,
    private val attempts: Int = 2,
    private val delayMs: Long = 1000
) : LLMClient {
    override suspend fun generateJson(prompt: String): String {
        var lastError: Throwable? = null
        repeat(attempts) { index ->
            try {
                logger.debug("RetryingLLMClient attempt {} of {}", index + 1, attempts)
                return delegate.generateJson(prompt)
            } catch (t: Throwable) {
                lastError = t
                logger.warn("Attempt {} failed: {}", index + 1, t.message)
                if (index < attempts - 1) {
                    delay(delayMs)
                }
            }
        }
        throw lastError ?: IllegalStateException("generateJson failed")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(RetryingLLMClient::class.java)
    }
}
