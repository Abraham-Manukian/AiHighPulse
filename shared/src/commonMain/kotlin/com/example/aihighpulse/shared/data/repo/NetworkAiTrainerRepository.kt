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

class NetworkAiTrainerRepository(
    private val api: ApiClient
) : AiTrainerRepository {

    override suspend fun generateTrainingPlan(profile: Profile, weekIndex: Int): TrainingPlan? =
        runCatching {
            val res: TrainingPlanDto = api.post("/ai/training", AiTrainingRequestDto.fromDomain(profile, weekIndex))
            res.toDomain()
        }.getOrNull()

    override suspend fun generateNutritionPlan(profile: Profile, weekIndex: Int): NutritionPlan? =
        runCatching {
            val res: NutritionPlanDto = api.post("/ai/nutrition", AiNutritionRequestDto.fromDomain(profile, weekIndex))
            res.toDomain()
        }.getOrNull()

    override suspend fun getSleepAdvice(profile: Profile): Advice? =
        runCatching {
            val res: AdviceDto = api.post("/ai/sleep", AiAdviceRequestDto.fromDomain(profile))
            res.toDomain()
        }.getOrNull()

    override suspend fun bootstrap(profile: Profile, weekIndex: Int): CoachBundle? =
        runCatching {
            val res: AiBootstrapResponseDto = api.post("/ai/bootstrap", AiBootstrapRequestDto.fromDomain(profile, weekIndex))
            res.toDomain()
        }.getOrNull()
}
