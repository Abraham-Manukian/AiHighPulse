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
}

interface NutritionRepository {
    suspend fun generatePlan(profile: Profile, weekIndex: Int): NutritionPlan
}

interface AdviceRepository {
    suspend fun getAdvice(profile: Profile, context: Map<String, Any?>): Advice
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
    fun getTheme(): String? // "light" | "dark" | "system"
    fun setTheme(theme: String?)
    fun getUnits(): String? // "metric" | "imperial"
    fun setUnits(units: String?)
}

interface AiTrainerRepository {
    suspend fun generateTrainingPlan(profile: Profile, weekIndex: Int): TrainingPlan?
    suspend fun generateNutritionPlan(profile: Profile, weekIndex: Int): NutritionPlan?
    suspend fun getSleepAdvice(profile: Profile): Advice?
}
