package com.example.aihighpulse.shared.data.repo

import com.example.aihighpulse.shared.data.network.ApiClient
import com.example.aihighpulse.shared.data.network.dto.AdviceDto
import com.example.aihighpulse.shared.data.network.dto.NutritionPlanDto
import com.example.aihighpulse.shared.data.network.dto.TrainingPlanDto
import com.example.aihighpulse.shared.domain.model.Profile
import com.example.aihighpulse.shared.domain.repository.ChatMessage
import com.example.aihighpulse.shared.domain.repository.ChatRepository
import com.example.aihighpulse.shared.domain.repository.CoachResponse
import kotlinx.serialization.Serializable

class NetworkChatRepository(private val api: ApiClient): ChatRepository {
    override suspend fun send(
        profile: Profile,
        history: List<ChatMessage>,
        userMessage: String,
        locale: String?
    ): CoachResponse {
        val req = ChatRequest(
            profile = ChatProfileDto.from(profile),
            messages = history.map { ChatMsgDto(it.role, it.content) } + ChatMsgDto("user", userMessage),
            locale = locale
        )
        val res: ChatResponse = api.post("/ai/chat", req)
        return CoachResponse(
            reply = res.reply,
            trainingPlan = res.trainingPlan?.toDomain(),
            nutritionPlan = res.nutritionPlan?.toDomain(),
            sleepAdvice = res.sleepAdvice?.toDomain()
        )
    }
}

@Serializable
data class ChatProfileDto(
    val age: Int,
    val sex: String,
    val heightCm: Int,
    val weightKg: Double,
    val goal: String,
    val experienceLevel: Int,
    val equipment: List<String>,
    val dietaryPreferences: List<String>,
    val allergies: List<String>,
    val weeklySchedule: Map<String, Boolean>,
) {
    companion object { fun from(p: Profile) = ChatProfileDto(
            age = p.age,
            sex = p.sex.name,
            heightCm = p.heightCm,
            weightKg = p.weightKg,
            goal = p.goal.name,
            experienceLevel = p.experienceLevel,
            equipment = p.equipment.items,
            dietaryPreferences = p.dietaryPreferences,
            allergies = p.allergies,
            weeklySchedule = p.weeklySchedule,
        )
    }
}

@Serializable
data class ChatMsgDto(val role: String, val content: String)

@Serializable
data class ChatRequest(
    val profile: ChatProfileDto,
    val messages: List<ChatMsgDto>,
    val locale: String? = null
)

@Serializable
data class ChatResponse(
    val reply: String,
    val trainingPlan: TrainingPlanDto? = null,
    val nutritionPlan: NutritionPlanDto? = null,
    val sleepAdvice: AdviceDto? = null
)
