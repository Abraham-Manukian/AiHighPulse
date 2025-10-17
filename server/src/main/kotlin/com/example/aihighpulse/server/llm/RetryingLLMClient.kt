package com.example.aihighpulse.server.llm

import kotlin.math.max
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory

class RetryingLLMClient(
    private val delegate: LLMClient,
    private val attempts: Int = 3,
    private val initialDelayMs: Long = 1_500,
    private val maxDelayMs: Long = 12_000,
    private val backoffMultiplier: Double = 2.0
) : LLMClient {
    override suspend fun generateJson(prompt: String): String {
        var attempt = 1
        var nextDelay = initialDelayMs.coerceAtLeast(0)
        var lastError: Throwable? = null

        while (attempt <= attempts) {
            try {
                return delegate.generateJson(prompt)
            } catch (rateLimit: RateLimitException) {
                lastError = rateLimit
                if (attempt >= attempts) break

                val hintedDelay = rateLimit.retryAfterMillis
                    ?.takeIf { it > 0 }
                    ?.coerceAtMost(maxDelayMs)
                val waitFor = (hintedDelay ?: nextDelay).coerceAtMost(maxDelayMs).coerceAtLeast(0)
                logger.warn(
                    "Retrying LLM after rate limit (attempt {}/{}). Waiting {} ms (hint={}, nextDelay={}).",
                    attempt,
                    attempts,
                    waitFor,
                    hintedDelay,
                    nextDelay
                )
                delay(waitFor)
                nextDelay = (max(nextDelay.toDouble() * backoffMultiplier, initialDelayMs.toDouble()))
                    .toLong()
                    .coerceAtMost(maxDelayMs)
                attempt += 1
                continue
            } catch (ex: Throwable) {
                throw ex
            }
        }
        throw lastError ?: IllegalStateException("LLM request failed after $attempts attempt(s)")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(RetryingLLMClient::class.java)
    }
}
