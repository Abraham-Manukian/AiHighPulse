package com.vtempe.server.features.ai.data.llm.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

@Serializable
data class ChatCompletionRequestDto(
    val model: String,
    val messages: List<ChatMessageDto>,
    val temperature: Double? = null,
    @SerialName("top_p") val topP: Double? = null,
    @SerialName("max_tokens") val maxTokens: Int? = null,
    @SerialName("response_format") val responseFormat: ResponseFormatDto? = null
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

@Serializable
data class ResponseFormatDto(
    val type: String
)

