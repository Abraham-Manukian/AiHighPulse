package com.example.aihighpulse.shared.domain.model

import kotlinx.datetime.LocalDate

enum class Goal { LOSE_FAT, GAIN_MUSCLE, MAINTAIN }
enum class Sex { MALE, FEMALE, OTHER }

data class Constraints(
    val injuries: List<String> = emptyList(),
    val healthNotes: List<String> = emptyList(),
)

data class Equipment(
    val items: List<String> = emptyList()
)

data class Profile(
    val id: String,
    val age: Int,
    val sex: Sex,
    val heightCm: Int,
    val weightKg: Double,
    val goal: Goal,
    val experienceLevel: Int, // 1..5
    val constraints: Constraints = Constraints(),
    val equipment: Equipment = Equipment(),
    val dietaryPreferences: List<String> = emptyList(),
    val allergies: List<String> = emptyList(),
    val weeklySchedule: Map<String, Boolean> = emptyMap(),
    val budgetLevel: Int = 2 // 1 low .. 3 high
)

data class Exercise(
    val id: String,
    val name: String,
    val muscleGroups: List<String>,
    val difficulty: Int,
    val videoUrl: String? = null,
    val technique: String? = null,
    val contraindications: List<String> = emptyList(),
)

data class WorkoutSet(
    val exerciseId: String,
    val reps: Int,
    val weightKg: Double? = null,
    val rpe: Double? = null
)

data class Workout(
    val id: String,
    val date: LocalDate,
    val sets: List<WorkoutSet>
)

data class TrainingPlan(
    val weekIndex: Int,
    val workouts: List<Workout>
)

data class Macros(
    val proteinGrams: Int,
    val fatGrams: Int,
    val carbsGrams: Int,
    val kcal: Int
)

data class Meal(
    val name: String,
    val ingredients: List<String>,
    val kcal: Int,
    val macros: Macros
)

data class NutritionPlan(
    val weekIndex: Int,
    val mealsByDay: Map<String, List<Meal>>,
    val shoppingList: List<String>
)

data class Advice(
    val messages: List<String>,
    val disclaimer: String = "Not medical advice"
)
