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

