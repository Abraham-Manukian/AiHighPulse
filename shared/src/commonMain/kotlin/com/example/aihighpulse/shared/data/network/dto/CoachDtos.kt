package com.example.aihighpulse.shared.data.network.dto

import com.example.aihighpulse.shared.domain.model.Advice
import com.example.aihighpulse.shared.domain.model.Macros
import com.example.aihighpulse.shared.domain.model.Meal
import com.example.aihighpulse.shared.domain.model.NutritionPlan
import com.example.aihighpulse.shared.domain.model.Profile
import com.example.aihighpulse.shared.domain.model.TrainingPlan
import com.example.aihighpulse.shared.domain.model.Workout
import com.example.aihighpulse.shared.domain.model.WorkoutSet
import com.example.aihighpulse.shared.domain.repository.CoachBundle
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
internal data class AiProfileDto(
    val age: Int,
    val sex: String,
    val heightCm: Int,
    val weightKg: Double,
    val goal: String,
    val experienceLevel: Int,
    val equipment: List<String>,
    val dietaryPreferences: List<String>,
    val allergies: List<String>,
    val weeklySchedule: Map<String, Boolean>
) {
    companion object {
        fun fromDomain(profile: Profile) = AiProfileDto(
            age = profile.age,
            sex = profile.sex.name,
            heightCm = profile.heightCm,
            weightKg = profile.weightKg,
            goal = profile.goal.name,
            experienceLevel = profile.experienceLevel,
            equipment = profile.equipment.items,
            dietaryPreferences = profile.dietaryPreferences,
            allergies = profile.allergies,
            weeklySchedule = profile.weeklySchedule
        )
    }
}

@Serializable
internal data class AiTrainingRequestDto(val profile: AiProfileDto, val weekIndex: Int) {
    companion object {
        fun fromDomain(profile: Profile, weekIndex: Int) =
            AiTrainingRequestDto(AiProfileDto.fromDomain(profile), weekIndex)
    }
}

@Serializable
internal data class AiNutritionRequestDto(val profile: AiProfileDto, val weekIndex: Int) {
    companion object {
        fun fromDomain(profile: Profile, weekIndex: Int) =
            AiNutritionRequestDto(AiProfileDto.fromDomain(profile), weekIndex)
    }
}

@Serializable
internal data class AiAdviceRequestDto(val profile: AiProfileDto) {
    companion object {
        fun fromDomain(profile: Profile) = AiAdviceRequestDto(AiProfileDto.fromDomain(profile))
    }
}

@Serializable
data class TrainingPlanDto(
    val weekIndex: Int,
    val workouts: List<WorkoutDto>
) {
    @Serializable
    data class WorkoutDto(
        val id: String,
        val date: String,
        val sets: List<SetDto>
    )

    @Serializable
    data class SetDto(
        val exerciseId: String,
        val reps: Int,
        val weightKg: Double? = null,
        val rpe: Double? = null
    )

    fun toDomain(): TrainingPlan = TrainingPlan(
        weekIndex = weekIndex,
        workouts = workouts.map { workout ->
            Workout(
                id = workout.id,
                date = LocalDate.parse(workout.date),
                sets = workout.sets.map { set ->
                    WorkoutSet(
                        exerciseId = set.exerciseId,
                        reps = set.reps,
                        weightKg = set.weightKg,
                        rpe = set.rpe
                    )
                }
            )
        }
    )
}

@Serializable
data class NutritionPlanDto(
    val weekIndex: Int,
    val mealsByDay: Map<String, List<MealDto>>
) {
    @Serializable
    data class MealDto(
        val name: String,
        val ingredients: List<String>,
        val kcal: Int,
        val macros: Macros
    )

    fun toDomain(): NutritionPlan = NutritionPlan(
        weekIndex = weekIndex,
        mealsByDay = mealsByDay.mapValues { (_, meals) ->
            meals.map { meal ->
                Meal(
                    name = meal.name,
                    ingredients = meal.ingredients,
                    kcal = meal.kcal,
                    macros = meal.macros
                )
            }
        },
        shoppingList = mealsByDay.values
            .flatten()
            .flatMap { it.ingredients }
            .distinct()
            .sorted()
    )
}

@Serializable
data class AdviceDto(
    val messages: List<String>,
    val disclaimer: String? = null
) {
    fun toDomain(): Advice = Advice(
        messages = messages,
        disclaimer = disclaimer ?: "Not medical advice"
    )
}

@Serializable
internal data class AiBootstrapRequestDto(val profile: AiProfileDto, val weekIndex: Int) {
    companion object {
        fun fromDomain(profile: Profile, weekIndex: Int) = AiBootstrapRequestDto(AiProfileDto.fromDomain(profile), weekIndex)
    }
}

@Serializable
data class AiBootstrapResponseDto(
    val trainingPlan: TrainingPlanDto? = null,
    val nutritionPlan: NutritionPlanDto? = null,
    val sleepAdvice: AdviceDto? = null
) {
    fun toDomain(): CoachBundle = CoachBundle(
        trainingPlan = trainingPlan?.toDomain(),
        nutritionPlan = nutritionPlan?.toDomain(),
        sleepAdvice = sleepAdvice?.toDomain()
    )
}
