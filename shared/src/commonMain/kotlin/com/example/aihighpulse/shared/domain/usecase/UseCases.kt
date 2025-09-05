package com.example.aihighpulse.shared.domain.usecase

import com.example.aihighpulse.shared.domain.model.*
import com.example.aihighpulse.shared.domain.repository.*
import kotlinx.coroutines.flow.Flow

class GenerateTrainingPlan(
    private val profileRepository: ProfileRepository,
    private val trainingRepository: TrainingRepository,
) {
    suspend operator fun invoke(weekIndex: Int): TrainingPlan {
        val profile = profileRepository.getProfile() ?: error("Profile required")
        return trainingRepository.generatePlan(profile, weekIndex)
    }
}

class LogWorkoutSet(
    private val trainingRepository: TrainingRepository
) {
    suspend operator fun invoke(workoutId: String, set: WorkoutSet) {
        trainingRepository.logSet(workoutId, set)
    }
}

class GenerateNutritionPlan(
    private val profileRepository: ProfileRepository,
    private val nutritionRepository: NutritionRepository
) {
    suspend operator fun invoke(weekIndex: Int): NutritionPlan {
        val profile = profileRepository.getProfile() ?: error("Profile required")
        return nutritionRepository.generatePlan(profile, weekIndex)
    }
}

class SyncWithBackend(
    private val syncRepository: SyncRepository
) {
    suspend operator fun invoke(): Boolean = syncRepository.syncAll()
}

class ValidateSubscription(
    private val purchasesRepository: PurchasesRepository
) {
    suspend operator fun invoke(): Boolean = purchasesRepository.isSubscriptionActive()
}

interface AnalyticsLogger {
    fun log(event: String, params: Map<String, Any?> = emptyMap())
}

object AnalyticsEvents {
    const val OnboardingCompleted = "onboarding_completed"
    const val WorkoutStarted = "workout_started"
    const val WorkoutCompleted = "workout_completed"
    const val SetLogged = "set_logged"
    const val MealCompleted = "meal_completed"
    const val AdviceOpened = "advice_opened"
    const val PaywallViewed = "paywall_viewed"
    const val PurchaseSuccess = "purchase_success"
    const val SubscriptionRenewed = "subscription_renewed"
    const val ChurnWarning = "churn_warning"
}

