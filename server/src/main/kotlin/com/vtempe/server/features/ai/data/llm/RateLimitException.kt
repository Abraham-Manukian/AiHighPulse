package com.vtempe.server.features.ai.data.llm

/**
 * Indicates that the upstream LLM rejected a request due to rate limiting.
 *
 * @param retryAfterMillis Optional hint (milliseconds) for when the caller should retry.
 */
class RateLimitException(
    message: String,
    val retryAfterMillis: Long? = null,
    cause: Throwable? = null
) : RuntimeException(message, cause)


