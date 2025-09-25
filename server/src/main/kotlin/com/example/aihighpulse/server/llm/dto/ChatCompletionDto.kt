package com.example.aihighpulse.server.llm.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

@Serializable
data class ChatCompletionRequestDto(
    val model: String,
    val messages: List<ChatMessageDto>,
    val temperature: Double,
    val stream: Boolean,
    @SerialName("max_tokens") val maxTokens: Int
)

@Serializable
data class ChatMessageDto(
    val role: String,
    val content: String,
)

@Serializable
data class ChatCompletionResponseDto(
    val choices: List<ChatChoiceDto> = emptyList(),
    val error: ChatErrorDto? = null,
)

@Serializable
data class ChatChoiceDto(
    val message: ChatMessageDto? = null,
)

@Serializable
data class ChatErrorDto(
    val message: String,
    val code: JsonElement? = null,
    @SerialName("type") val type: String? = null,
) {
    val codeAsString: String?
        get() = (code as? JsonPrimitive)?.content
}
