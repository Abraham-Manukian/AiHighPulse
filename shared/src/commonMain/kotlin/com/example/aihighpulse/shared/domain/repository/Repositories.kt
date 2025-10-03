package com.example.aihighpulse.shared.domain.repository

import com.example.aihighpulse.shared.domain.model.*
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun getProfile(): Profile?
    suspend fun upsertProfile(profile: Profile)
}

interface TrainingRepository {
    suspend fun generatePlan(profile: Profile, weekIndex: Int): TrainingPlan
    suspend fun logSet(workoutId: String, set: WorkoutSet)
    fun observeWorkouts(): Flow<List<Workout>>
    suspend fun savePlan(plan: TrainingPlan)
    suspend fun hasPlan(weekIndex: Int): Boolean
}

interface NutritionRepository {
    suspend fun generatePlan(profile: Profile, weekIndex: Int): NutritionPlan
    suspend fun savePlan(plan: NutritionPlan)
    fun observePlan(): Flow<NutritionPlan?>
    suspend fun hasPlan(weekIndex: Int): Boolean
}

interface AdviceRepository {
    suspend fun getAdvice(profile: Profile, context: Map<String, Any?>): Advice
    suspend fun saveAdvice(topic: String, advice: Advice)
    fun observeAdvice(topic: String): Flow<Advice>
    suspend fun hasAdvice(topic: String): Boolean
}

interface PurchasesRepository {
    suspend fun isSubscriptionActive(): Boolean
}

interface SyncRepository {
    suspend fun syncAll(): Boolean
}

interface PreferencesRepository {
    fun getLanguageTag(): String?
    fun setLanguageTag(tag: String?)
    fun getTheme(): String?
    fun setTheme(theme: String?)
    fun getUnits(): String?
    fun setUnits(units: String?)
}

interface AiTrainerRepository {
    suspend fun generateTrainingPlan(profile: Profile, weekIndex: Int): TrainingPlan?
    suspend fun generateNutritionPlan(profile: Profile, weekIndex: Int): NutritionPlan?
    suspend fun getSleepAdvice(profile: Profile): Advice?
    suspend fun bootstrap(profile: Profile, weekIndex: Int): CoachBundle?
}

data class ChatMessage(val role: String, val content: String)

data class CoachBundle(
    val trainingPlan: TrainingPlan? = null,
    val nutritionPlan: NutritionPlan? = null,
    val sleepAdvice: Advice? = null
)

data class CoachResponse(
    val reply: String,
    val trainingPlan: TrainingPlan? = null,
    val nutritionPlan: NutritionPlan? = null,
    val sleepAdvice: Advice? = null
)

interface ChatRepository {
    suspend fun send(profile: Profile, history: List<ChatMessage>, userMessage: String, locale: String?): CoachResponse
}






