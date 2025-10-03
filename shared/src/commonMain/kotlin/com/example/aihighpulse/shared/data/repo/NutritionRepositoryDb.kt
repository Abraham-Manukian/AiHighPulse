package com.example.aihighpulse.shared.data.repo

import com.example.aihighpulse.shared.db.AppDatabase
import com.example.aihighpulse.shared.domain.model.Goal
import com.example.aihighpulse.shared.domain.model.Macros
import com.example.aihighpulse.shared.domain.model.Meal
import com.example.aihighpulse.shared.domain.model.NutritionPlan
import com.example.aihighpulse.shared.domain.model.Profile
import com.example.aihighpulse.shared.domain.model.Sex
import com.example.aihighpulse.shared.domain.repository.NutritionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.max

class NutritionRepositoryDb(
    private val db: AppDatabase,
    private val ai: com.example.aihighpulse.shared.domain.repository.AiTrainerRepository,
    private val validateSubscription: com.example.aihighpulse.shared.domain.usecase.ValidateSubscription,
) : NutritionRepository {
    private val planFlow = MutableStateFlow<NutritionPlan?>(null)

    init {
        planFlow.value = loadPlanFromDb(0)
    }

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
        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val mealsByDay = mutableMapOf<String, List<Meal>>()
        val shopping = mutableSetOf<String>()

        days.forEach { day ->
            val meals = buildDailyMeals(kcalTarget, macrosDay)
            mealsByDay[day] = meals
            meals.forEachIndexed { idx, meal ->
                val mealId = "m_${'$'}weekIndex_${'$'}day_${'$'}idx"
                db.nutritionQueries.insertMeal(
                    mealId,
                    meal.name,
                    meal.kcal.toLong(),
                    meal.macros.proteinGrams.toLong(),
                    meal.macros.fatGrams.toLong(),
                    meal.macros.carbsGrams.toLong()
                )
                db.nutritionQueries.deleteIngredientsForMeal(mealId)
                meal.ingredients.forEach { ing ->
                    db.nutritionQueries.insertMealIngredient(mealId, ing)
                    shopping += ing
                }
                db.nutritionQueries.insertMealByDay(weekIndex.toLong(), day, idx.toLong(), mealId)
            }
        }

        val plan = NutritionPlan(weekIndex, mealsByDay, shopping.toList().sorted())
        persistPlan(plan)
        return plan
    }

    override suspend fun savePlan(plan: NutritionPlan) {
        persistPlan(plan)
    }

    override fun observePlan(): Flow<NutritionPlan?> = planFlow.asStateFlow()

    override suspend fun hasPlan(weekIndex: Int): Boolean {
        if (planFlow.value?.weekIndex != weekIndex) {
            planFlow.value = loadPlanFromDb(weekIndex)
        }
        return planFlow.value?.weekIndex == weekIndex
    }

    private fun persistPlan(plan: NutritionPlan) {
        db.nutritionQueries.deleteMealsForWeek(plan.weekIndex.toLong())
        plan.mealsByDay.forEach { (day, meals) ->
            meals.forEachIndexed { idx, meal ->
                val mealId = "m_${'$'}{plan.weekIndex}_${'$'}day_${'$'}idx"
                db.nutritionQueries.insertMeal(
                    mealId,
                    meal.name,
                    meal.kcal.toLong(),
                    meal.macros.proteinGrams.toLong(),
                    meal.macros.fatGrams.toLong(),
                    meal.macros.carbsGrams.toLong()
                )
                db.nutritionQueries.deleteIngredientsForMeal(mealId)
                meal.ingredients.forEach { ing ->
                    db.nutritionQueries.insertMealIngredient(mealId, ing)
                }
                db.nutritionQueries.insertMealByDay(plan.weekIndex.toLong(), day, idx.toLong(), mealId)
            }
        }
        planFlow.value = plan
    }

    private fun loadPlanFromDb(weekIndex: Int): NutritionPlan? {
        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val mealsByDay = mutableMapOf<String, List<Meal>>()
        var hasMeals = false
        for (day in days) {
            val rows = db.nutritionQueries.selectMealsForDay(weekIndex.toLong(), day).executeAsList()
            if (rows.isEmpty()) continue
            hasMeals = true
            val grouped = rows.groupBy { it.indexInDay }
            val meals = grouped.toSortedMap().map { (_, entries) ->
                val head = entries.first()
                val ingredients = entries.mapNotNull { it.ingredient }.distinct()
                Meal(
                    name = head.name,
                    ingredients = ingredients,
                    kcal = head.kcal.toInt(),
                    macros = Macros(
                        proteinGrams = head.protein.toInt(),
                        fatGrams = head.fat.toInt(),
                        carbsGrams = head.carbs.toInt(),
                        kcal = head.kcal.toInt()
                    )
                )
            }
            mealsByDay[day] = meals
        }
        if (!hasMeals) return null
        val shopping = db.nutritionQueries.selectShoppingListForWeek(weekIndex.toLong()).executeAsList()
        return NutritionPlan(weekIndex, mealsByDay, shopping)
    }

    private fun buildDailyMeals(kcalTarget: Int, macrosDay: Macros): List<Meal> {
        val kcalPerMeal = kcalTarget / 3
        val macrosPerMeal = macrosDay.copy(kcal = kcalPerMeal)
        return listOf(
            Meal("Oatmeal with Banana", listOf("oats", "milk", "banana"), kcalPerMeal, macrosPerMeal),
            Meal("Chicken and Rice", listOf("chicken", "rice", "vegetables"), kcalPerMeal, macrosPerMeal),
            Meal("Yogurt and Nuts", listOf("yogurt", "almonds"), kcalPerMeal, macrosPerMeal)
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
        val fat = max(0.8 * p.weightKg, 40.0).toInt()
        val proteinKcal = protein * 4
        val fatKcal = fat * 9
        val carbsKcal = (kcal - proteinKcal - fatKcal).coerceAtLeast(0)
        val carbs = carbsKcal / 4
        return Macros(proteinGrams = protein, fatGrams = fat, carbsGrams = carbs, kcal = kcal)
    }
}
