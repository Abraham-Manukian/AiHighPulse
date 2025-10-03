package com.example.aihighpulse.shared.domain.usecase

import com.example.aihighpulse.shared.domain.model.Advice
import com.example.aihighpulse.shared.domain.repository.AdviceRepository
import com.example.aihighpulse.shared.domain.repository.ChatMessage
import com.example.aihighpulse.shared.domain.repository.ChatRepository
import com.example.aihighpulse.shared.domain.repository.CoachResponse
import com.example.aihighpulse.shared.domain.repository.NutritionRepository
import com.example.aihighpulse.shared.domain.repository.PreferencesRepository
import com.example.aihighpulse.shared.domain.repository.ProfileRepository
import com.example.aihighpulse.shared.domain.repository.TrainingRepository

class AskAiTrainer(
    private val profileRepository: ProfileRepository,
    private val chatRepository: ChatRepository,
    private val preferencesRepository: PreferencesRepository,
    private val trainingRepository: TrainingRepository,
    private val nutritionRepository: NutritionRepository,
    private val adviceRepository: AdviceRepository
) {
    suspend operator fun invoke(
        history: List<ChatMessage>,
        userMessage: String,
        localeOverride: String? = null
    ): CoachResponse {
        val profile = profileRepository.getProfile() ?: error("Profile required")
        val locale = localeOverride ?: preferencesRepository.getLanguageTag()
        val response = chatRepository.send(profile, history, userMessage, locale)
        response.trainingPlan?.let { plan -> trainingRepository.savePlan(plan) }
        response.nutritionPlan?.let { plan -> nutritionRepository.savePlan(plan) }
        response.sleepAdvice?.let { advice -> adviceRepository.saveAdvice("sleep", advice) }
        return response
    }
}


