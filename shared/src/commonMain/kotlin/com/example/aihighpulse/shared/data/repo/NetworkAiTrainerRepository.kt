package com.example.aihighpulse.shared.data.repo

import com.example.aihighpulse.shared.data.network.ApiClient
import com.example.aihighpulse.shared.domain.model.*
import com.example.aihighpulse.shared.domain.repository.AiTrainerRepository
import kotlinx.serialization.Serializable

class NetworkAiTrainerRepository(
    private val api: ApiClient
) : AiTrainerRepository {

    override suspend fun generateTrainingPlan(profile: Profile, weekIndex: Int): TrainingPlan? =
        runCatching {
            val res: AiTrainingResponse = api.post("/ai/training", AiTrainingRequest.from(profile, weekIndex))
            res.toDomain()
        }.getOrNull()

    override suspend fun generateNutritionPlan(profile: Profile, weekIndex: Int): NutritionPlan? =
        runCatching {
            val res: AiNutritionResponse = api.post("/ai/nutrition", AiNutritionRequest.from(profile, weekIndex))
            res.toDomain()
        }.getOrNull()

    override suspend fun getSleepAdvice(profile: Profile): Advice? =
        runCatching {
            val res: AiAdviceResponse = api.post("/ai/sleep", AiAdviceRequest.from(profile))
            Advice(messages = res.messages, disclaimer = res.disclaimer ?: "Not medical advice")
        }.getOrNull()
}

@Serializable
private data class AiTrainingRequest(val profile: AiProfile, val weekIndex: Int) {
    companion object { fun from(p: Profile, w: Int) = AiTrainingRequest(AiProfile.from(p), w) }
}

@Serializable
private data class AiNutritionRequest(val profile: AiProfile, val weekIndex: Int) {
    companion object { fun from(p: Profile, w: Int) = AiNutritionRequest(AiProfile.from(p), w) }
}

@Serializable
private data class AiAdviceRequest(val profile: AiProfile) {
    companion object { fun from(p: Profile) = AiAdviceRequest(AiProfile.from(p)) }
}

@Serializable
private data class AiProfile(
    val age: Int,
    val sex: String,
    val heightCm: Int,
    val weightKg: Double,
    val goal: String,
    val experienceLevel: Int,
    val equipment: List<String>,
    val dietaryPreferences: List<String>,
    val allergies: List<String>,
    val weeklySchedule: Map<String, Boolean>,
) {
    companion object {
        fun from(p: Profile) = AiProfile(
            age = p.age,
            sex = p.sex.name,
            heightCm = p.heightCm,
            weightKg = p.weightKg,
            goal = p.goal.name,
            experienceLevel = p.experienceLevel,
            equipment = p.equipment.items,
            dietaryPreferences = p.dietaryPreferences,
            allergies = p.allergies,
            weeklySchedule = p.weeklySchedule,
        )
    }
}

@Serializable
private data class AiTrainingResponse(
    val weekIndex: Int,
    val workouts: List<AiWorkout>
) {
    @Serializable
    data class AiWorkout(
        val id: String,
        val date: String,
        val sets: List<AiSet>
    )
    @Serializable
    data class AiSet(
        val exerciseId: String,
        val reps: Int,
        val weightKg: Double? = null,
        val rpe: Double? = null
    )
    fun toDomain(): TrainingPlan = TrainingPlan(
        weekIndex = weekIndex,
        workouts = workouts.map { w ->
            Workout(id = w.id, date = kotlinx.datetime.LocalDate.parse(w.date), sets = w.sets.map { s ->
                WorkoutSet(s.exerciseId, s.reps, s.weightKg, s.rpe)
            })
        }
    )
}

@Serializable
private data class AiNutritionResponse(
    val weekIndex: Int,
    val mealsByDay: Map<String, List<AiMeal>>,
) {
    @Serializable
    data class AiMeal(
        val name: String,
        val ingredients: List<String>,
        val kcal: Int,
        val macros: Macros
    )
    fun toDomain(): NutritionPlan = NutritionPlan(
        weekIndex = weekIndex,
        mealsByDay = mealsByDay.mapValues { (_, list) ->
            list.map { m -> Meal(m.name, m.ingredients, m.kcal, m.macros) }
        },
        shoppingList = mealsByDay.values.flatten().flatMap { it.ingredients }.distinct().sorted()
    )
}

@Serializable
private data class AiAdviceResponse(val messages: List<String>, val disclaimer: String? = null)

