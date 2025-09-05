package com.example.aihighpulse.shared.data.di

import com.example.aihighpulse.shared.data.network.ApiClient
import com.example.aihighpulse.shared.data.network.createHttpClient
import com.example.aihighpulse.shared.domain.model.*
import com.example.aihighpulse.shared.domain.repository.*
import com.example.aihighpulse.shared.domain.usecase.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.module.Module
import org.koin.dsl.module

object DI {
    fun coreModule(apiBaseUrl: String): Module = module {
        single { createHttpClient() }
        single { ApiClient(get(), apiBaseUrl) }

        // Repositories (simple in-memory/local placeholders)
        single<ProfileRepository> { InMemoryProfileRepository() }
        single<TrainingRepository> { LocalTrainingRepository() }
        single<NutritionRepository> { LocalNutritionRepository() }
        single<AdviceRepository> { StubAdviceRepository() }
        single<PurchasesRepository> { StubPurchasesRepository() }
        single<SyncRepository> { StubSyncRepository() }

        // Use cases
        factory { GenerateTrainingPlan(get(), get()) }
        factory { LogWorkoutSet(get()) }
        factory { GenerateNutritionPlan(get(), get()) }
        factory { SyncWithBackend(get()) }
        factory { ValidateSubscription(get()) }
    }
}
// --- Simple placeholder implementations (MVP offline-first scaffolding) ---

class InMemoryProfileRepository : ProfileRepository {
    private var profile: Profile? = null
    override suspend fun getProfile(): Profile? = profile
    override suspend fun upsertProfile(profile: Profile) { this.profile = profile }
}

class LocalTrainingRepository : TrainingRepository {
    private val workouts = MutableStateFlow<List<Workout>>(emptyList())
    override suspend fun generatePlan(profile: Profile, weekIndex: Int): TrainingPlan {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val w = List(3) { day ->
            Workout(
                id = "w_${weekIndex}_$day",
                date = today,
                sets = listOf(
                    WorkoutSet(exerciseId = "squat", reps = 8, weightKg = 40.0, rpe = 7.5),
                    WorkoutSet(exerciseId = "bench", reps = 10, weightKg = 30.0, rpe = 7.0)
                )
            )
        }
        workouts.value = w
        return TrainingPlan(weekIndex = weekIndex, workouts = w)
    }
    override suspend fun logSet(workoutId: String, set: WorkoutSet) {
        val updated = workouts.value.map { if (it.id == workoutId) it.copy(sets = it.sets + set) else it }
        workouts.value = updated
    }
    override fun observeWorkouts(): Flow<List<Workout>> = workouts.asStateFlow()
}

class LocalNutritionRepository : NutritionRepository {
    override suspend fun generatePlan(profile: Profile, weekIndex: Int): NutritionPlan {
        val kcalTarget = tdeeKcal(profile)
        val macros = macrosFor(profile, kcalTarget)
        val meals = listOf(
            Meal("Oatmeal", listOf("oats", "milk", "banana"), kcalTarget / 3, macros.copy(kcal = kcalTarget / 3)),
            Meal("Chicken & Rice", listOf("chicken", "rice", "veg"), kcalTarget / 3, macros.copy(kcal = kcalTarget / 3)),
            Meal("Yogurt & Nuts", listOf("yogurt", "nuts"), kcalTarget / 3, macros.copy(kcal = kcalTarget / 3))
        )
        val menu = mapOf(
            "Mon" to meals,
            "Tue" to meals,
            "Wed" to meals,
            "Thu" to meals,
            "Fri" to meals,
            "Sat" to meals,
            "Sun" to meals,
        )
        val shopping = listOf("oats", "milk", "banana", "chicken", "rice", "veg", "yogurt", "nuts")
        return NutritionPlan(weekIndex, menu, shopping)
    }

    private fun tdeeKcal(p: Profile): Int {
        val s = if (p.sex == Sex.MALE) 5 else -161
        val bmr = 10 * p.weightKg + 6.25 * p.heightCm - 5 * p.age + s
        val activity = 1.4
        val goalAdj = when (p.goal) {
            Goal.LOSE_FAT -> 0.85
            Goal.GAIN_MUSCLE -> 1.1
            Goal.MAINTAIN -> 1.0
        }
        return (bmr * activity * goalAdj).toInt()
    }

    private fun macrosFor(p: Profile, kcal: Int): Macros {
        val protein = (1.8 * p.weightKg).toInt()
        val fat = maxOf(0.8 * p.weightKg, 40.0).toInt()
        val proteinKcal = protein * 4
        val fatKcal = fat * 9
        val carbsKcal = (kcal - proteinKcal - fatKcal).coerceAtLeast(0)
        val carbs = carbsKcal / 4
        return Macros(proteinGrams = protein, fatGrams = fat, carbsGrams = carbs, kcal = kcal)
    }
}

class StubAdviceRepository : AdviceRepository {
    override suspend fun getAdvice(profile: Profile, context: Map<String, Any?>): Advice =
        Advice(messages = listOf("Stay hydrated", "Warm up properly"))
}

class StubPurchasesRepository : PurchasesRepository {
    override suspend fun isSubscriptionActive(): Boolean = false
}

class StubSyncRepository : SyncRepository {
    override suspend fun syncAll(): Boolean = true
}
