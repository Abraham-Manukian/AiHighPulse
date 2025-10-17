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
import com.example.aihighpulse.shared.domain.util.DataResult
import io.github.aakira.napier.Napier

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
    ): DataResult<CoachResponse> {
        val profile = profileRepository.getProfile() ?: error("Profile required")
        val locale = localeOverride ?: preferencesRepository.getLanguageTag()
        val result = chatRepository.send(profile, history, userMessage, locale)
        return when (result) {
            is DataResult.Success -> {
                result.data.trainingPlan?.let { trainingRepository.savePlan(it) }
                result.data.nutritionPlan?.let { nutritionRepository.savePlan(it) }
                result.data.sleepAdvice?.let { adviceRepository.saveAdvice("sleep", it) }
                result
            }
            is DataResult.Failure -> {
                Napier.w(
                    message = "Chat send failed: ${result.reason} ${result.message.orEmpty()}",
                    throwable = result.throwable
                )
                result
            }
        }
    }
}


