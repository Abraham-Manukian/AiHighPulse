package com.vtempe.shared.domain.usecase

import com.vtempe.shared.domain.model.Advice
import com.vtempe.shared.data.repo.AiResponseCache
import com.vtempe.shared.domain.repository.AdviceRepository
import com.vtempe.shared.domain.repository.ChatMessage
import com.vtempe.shared.domain.repository.ChatRepository
import com.vtempe.shared.domain.repository.CoachResponse
import com.vtempe.shared.domain.repository.NutritionRepository
import com.vtempe.shared.domain.repository.PreferencesRepository
import com.vtempe.shared.domain.repository.ProfileRepository
import com.vtempe.shared.domain.repository.TrainingRepository
import com.vtempe.shared.domain.util.DataResult
import com.vtempe.shared.domain.util.CoachDataFreshness
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock

class AskAiTrainer(
    private val profileRepository: ProfileRepository,
    private val chatRepository: ChatRepository,
    private val preferencesRepository: PreferencesRepository,
    private val trainingRepository: TrainingRepository,
    private val nutritionRepository: NutritionRepository,
    private val adviceRepository: AdviceRepository,
    private val aiResponseCache: AiResponseCache
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
                val hasCoachUpdates =
                    result.data.trainingPlan != null ||
                        result.data.nutritionPlan != null ||
                        result.data.sleepAdvice != null
                result.data.trainingPlan?.let { trainingRepository.savePlan(it) }
                result.data.nutritionPlan?.let { nutritionRepository.savePlan(it) }
                result.data.sleepAdvice?.let { adviceRepository.saveAdvice("sleep", it) }
                if (hasCoachUpdates) {
                    aiResponseCache.markBundleFresh(
                        version = CoachDataFreshness.SCHEMA_VERSION,
                        timestampMillis = Clock.System.now().toEpochMilliseconds()
                    )
                }
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



