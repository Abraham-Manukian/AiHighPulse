package com.example.aihighpulse.shared.data.repo

import com.example.aihighpulse.shared.data.network.ApiClient
import com.example.aihighpulse.shared.data.network.dto.AdviceDto
import com.example.aihighpulse.shared.data.network.dto.AiAdviceRequestDto
import com.example.aihighpulse.shared.data.network.dto.AiBootstrapRequestDto
import com.example.aihighpulse.shared.data.network.dto.AiBootstrapResponseDto
import com.example.aihighpulse.shared.data.network.dto.AiNutritionRequestDto
import com.example.aihighpulse.shared.data.network.dto.AiTrainingRequestDto
import com.example.aihighpulse.shared.data.network.dto.NutritionPlanDto
import com.example.aihighpulse.shared.data.network.dto.TrainingPlanDto
import com.example.aihighpulse.shared.domain.model.Advice
import com.example.aihighpulse.shared.domain.model.NutritionPlan
import com.example.aihighpulse.shared.domain.model.Profile
import com.example.aihighpulse.shared.domain.model.TrainingPlan
import com.example.aihighpulse.shared.domain.repository.AiTrainerRepository
import com.example.aihighpulse.shared.domain.repository.CoachBundle
import com.example.aihighpulse.shared.domain.repository.PreferencesRepository
import com.example.aihighpulse.shared.domain.util.DataResult
import io.github.aakira.napier.Napier

class NetworkAiTrainerRepository(
    private val api: ApiClient,
    private val preferences: PreferencesRepository,
    private val cache: AiResponseCache
) : AiTrainerRepository {

    private fun currentLocale(): String? = preferences.getLanguageTag()?.takeIf { it.isNotBlank() }

    override suspend fun generateTrainingPlan(profile: Profile, weekIndex: Int): DataResult<TrainingPlan> {
        val request = AiTrainingRequestDto.fromDomain(profile, weekIndex, currentLocale())
        return when (val result = api.postResult<AiTrainingRequestDto, TrainingPlanDto>("/ai/training", request)) {
            is DataResult.Success -> {
                val domain = result.data.toDomain()
                cache.storeTraining(result.data)
                DataResult.Success(domain, fromCache = result.fromCache, rawPayload = result.rawPayload)
            }
            is DataResult.Failure -> {
                cache.lastTraining()?.let { cached ->
                    Napier.w("AI training plan request failed, using cached version", result.throwable)
                    return DataResult.Success(cached.toDomain(), fromCache = true, rawPayload = result.rawPayload)
                }
                result
            }
        }
    }

    override suspend fun generateNutritionPlan(profile: Profile, weekIndex: Int): DataResult<NutritionPlan> {
        val request = AiNutritionRequestDto.fromDomain(profile, weekIndex, currentLocale())
        return when (val result = api.postResult<AiNutritionRequestDto, NutritionPlanDto>("/ai/nutrition", request)) {
            is DataResult.Success -> {
                val domain = result.data.toDomain()
                cache.storeNutrition(result.data)
                DataResult.Success(domain, fromCache = result.fromCache, rawPayload = result.rawPayload)
            }
            is DataResult.Failure -> {
                cache.lastNutrition()?.let { cached ->
                    Napier.w("AI nutrition plan request failed, using cached version", result.throwable)
                    return DataResult.Success(cached.toDomain(), fromCache = true, rawPayload = result.rawPayload)
                }
                result
            }
        }
    }

    override suspend fun getSleepAdvice(profile: Profile): DataResult<Advice> {
        val request = AiAdviceRequestDto.fromDomain(profile, currentLocale())
        return when (val result = api.postResult<AiAdviceRequestDto, AdviceDto>("/ai/sleep", request)) {
            is DataResult.Success -> {
                val domain = result.data.toDomain()
                cache.storeAdvice(result.data)
                DataResult.Success(domain, fromCache = result.fromCache, rawPayload = result.rawPayload)
            }
            is DataResult.Failure -> {
                cache.lastAdvice()?.let { cached ->
                    Napier.w("AI advice request failed, using cached version", result.throwable)
                    return DataResult.Success(cached.toDomain(), fromCache = true, rawPayload = result.rawPayload)
                }
                result
            }
        }
    }

    override suspend fun bootstrap(profile: Profile, weekIndex: Int): DataResult<CoachBundle> {
        val request = AiBootstrapRequestDto.fromDomain(profile, weekIndex, currentLocale())
        val result = api.postResult<AiBootstrapRequestDto, AiBootstrapResponseDto>("/ai/bootstrap", request)
        return when (result) {
            is DataResult.Success -> {
                cache.storeBundle(result.data)
                result.data.trainingPlan?.let { cache.storeTraining(it) }
                result.data.nutritionPlan?.let { cache.storeNutrition(it) }
                result.data.sleepAdvice?.let { cache.storeAdvice(it) }
                DataResult.Success(result.data.toDomain(), fromCache = result.fromCache, rawPayload = result.rawPayload)
            }
            is DataResult.Failure -> {
                cache.lastBundle()?.let { cached ->
                    Napier.w("Bootstrap request failed, using cached bundle", result.throwable)
                    return DataResult.Success(cached.toDomain(), fromCache = true, rawPayload = result.rawPayload)
                }
                result
            }
        }
    }
}
