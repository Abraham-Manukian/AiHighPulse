package com.example.aihighpulse.shared.data.repo

import com.example.aihighpulse.shared.db.AppDatabase
import com.example.aihighpulse.shared.domain.model.*
import com.example.aihighpulse.shared.domain.repository.NutritionRepository

class NutritionRepositoryDb(
    private val db: AppDatabase,
    private val ai: com.example.aihighpulse.shared.domain.repository.AiTrainerRepository,
    private val validateSubscription: com.example.aihighpulse.shared.domain.usecase.ValidateSubscription,
) : NutritionRepository {

    override suspend fun generatePlan(profile: Profile, weekIndex: Int): NutritionPlan {
        val useAi = runCatching { validateSubscription() }.getOrDefault(false)
        if (useAi) {
            val aiPlan = runCatching { ai.generateNutritionPlan(profile, weekIndex) }.getOrNull()
            if (aiPlan != null) {
                persistPlan(aiPlan)
                return aiPlan
            }
        }

        db.nutritionQueries.deleteMealsForWeek(weekIndex.toLong())

        val kcalTarget = tdeeKcal(profile)
        val macrosDay = macrosFor(profile, kcalTarget)
        val days = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")
        val mealsByDay = mutableMapOf<String, List<Meal>>()
        val shopping = mutableSetOf<String>()

        days.forEach { day ->
            val meals = buildDailyMeals(kcalTarget, macrosDay)
            mealsByDay[day] = meals
            meals.forEachIndexed { idx, meal ->
                val mealId = "m_${weekIndex}_${day}_${idx}"
                db.nutritionQueries.insertMeal(mealId, meal.name, meal.kcal.toLong(), meal.macros.proteinGrams.toLong(), meal.macros.fatGrams.toLong(), meal.macros.carbsGrams.toLong())
                db.nutritionQueries.deleteIngredientsForMeal(mealId)
                meal.ingredients.forEach { ing ->
                    db.nutritionQueries.insertMealIngredient(mealId, ing)
                    shopping += ing
                }
                db.nutritionQueries.insertMealByDay(weekIndex.toLong(), day, idx.toLong(), mealId)
            }
        }

        val plan = NutritionPlan(weekIndex, mealsByDay, shopping.toList().sorted())
        return plan
    }

    private fun persistPlan(plan: NutritionPlan) {
        db.nutritionQueries.deleteMealsForWeek(plan.weekIndex.toLong())
        plan.mealsByDay.forEach { (day, meals) ->
            meals.forEachIndexed { idx, meal ->
                val mealId = "m_${'$'}{plan.weekIndex}_${'$'}day_${'$'}idx"
                db.nutritionQueries.insertMeal(mealId, meal.name, meal.kcal.toLong(), meal.macros.proteinGrams.toLong(), meal.macros.fatGrams.toLong(), meal.macros.carbsGrams.toLong())
                db.nutritionQueries.deleteIngredientsForMeal(mealId)
                meal.ingredients.forEach { ing -> db.nutritionQueries.insertMealIngredient(mealId, ing) }
                db.nutritionQueries.insertMealByDay(plan.weekIndex.toLong(), day, idx.toLong(), mealId)
            }
        }
    }

    private fun buildDailyMeals(kcalTarget: Int, macrosDay: Macros): List<Meal> {
        val kcalPerMeal = kcalTarget / 3
        val macrosPerMeal = macrosDay.copy(kcal = kcalPerMeal)
        return listOf(
            Meal("Овсянка", listOf("овсянка", "молоко", "банан"), kcalPerMeal, macrosPerMeal),
            Meal("Гречка с курицей", listOf("курица", "гречка", "овощи"), kcalPerMeal, macrosPerMeal),
            Meal("Йогурт с орехами", listOf("йогурт", "орехи"), kcalPerMeal, macrosPerMeal)
        )
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
        val fat = kotlin.math.max(0.8 * p.weightKg, 40.0).toInt()
        val proteinKcal = protein * 4
        val fatKcal = fat * 9
        val carbsKcal = (kcal - proteinKcal - fatKcal).coerceAtLeast(0)
        val carbs = carbsKcal / 4
        return Macros(proteinGrams = protein, fatGrams = fat, carbsGrams = carbs, kcal = kcal)
    }
}
