package com.example.aihighpulse.shared.data.di

import com.example.aihighpulse.shared.data.network.ApiClient
import com.example.aihighpulse.shared.data.network.createHttpClient
import com.example.aihighpulse.shared.domain.model.*
import com.example.aihighpulse.shared.domain.repository.*
import com.example.aihighpulse.shared.data.repo.TrainingRepositoryDb
import com.example.aihighpulse.shared.data.repo.NetworkAiTrainerRepository
import com.example.aihighpulse.shared.data.repo.NetworkChatRepository
import com.example.aihighpulse.shared.data.repo.AiResponseCache
import com.example.aihighpulse.shared.data.repo.NutritionRepositoryDb
import com.example.aihighpulse.shared.domain.usecase.*
import com.example.aihighpulse.shared.data.repo.ProfileSettingsRepository
import com.example.aihighpulse.shared.data.repo.ProfileRepositoryDb
import com.example.aihighpulse.shared.data.repo.SettingsPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.max
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import com.russhwolf.settings.Settings
import org.koin.core.module.Module
import org.koin.dsl.module

object DI {
    fun coreModule(apiBaseUrl: String): Module = module {
        single { createHttpClient() }
        single { ApiClient(get(), apiBaseUrl) }

        // Settings storage
        single { Settings() }
        single { AiResponseCache(get()) }

        // Repositories
        single<PreferencesRepository> { SettingsPreferencesRepository(get()) }
        single<ProfileRepository> { ProfileRepositoryDb(get()) }
        single<AiTrainerRepository> { NetworkAiTrainerRepository(get(), get(), get()) }
        single<ChatRepository> { NetworkChatRepository(get(), get()) }
        single<TrainingRepository> {
            TrainingRepositoryDb(
                db = get(),
                ai = get(),
                validateSubscription = get(),
                cache = get()
            )
        }
        single<NutritionRepository> {
            NutritionRepositoryDb(
                db = get(),
                ai = get(),
                validateSubscription = get(),
                preferences = get(),
                cache = get()
            )
        }
        single<AdviceRepository> { StubAdviceRepository() }
        single<PurchasesRepository> { StubPurchasesRepository() }
        single<SyncRepository> { StubSyncRepository() }

        // Use cases
        factory { GenerateTrainingPlan(get(), get()) }
        factory { LogWorkoutSet(get()) }
        factory { GenerateNutritionPlan(get(), get()) }
        factory { BootstrapCoachData(get(), get(), get(), get(), get(), get()) }
        factory { EnsureCoachData(get(), get(), get(), get(), get(), get()) }
        factory { SyncWithBackend(get()) }
        factory { ValidateSubscription(get()) }
        factory { AskAiTrainer(get(), get(), get(), get(), get(), get(), get()) }
    }
}
// --- Simple placeholder implementations (MVP offline-first scaffolding) ---

class InMemoryProfileRepository : ProfileRepository {
    private var profile: Profile? = null
    override suspend fun getProfile(): Profile? = profile
    override suspend fun upsertProfile(profile: Profile) { this.profile = profile }
    override suspend fun clearAll() { profile = null }
}

class LocalTrainingRepository : TrainingRepository {
    private val workouts = MutableStateFlow<List<Workout>>(emptyList())
    override suspend fun generatePlan(profile: Profile, weekIndex: Int): TrainingPlan {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val w = List(3) { day ->
            Workout(
                id = "w_${'$'}weekIndex_${'$'}day",
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
    override suspend fun savePlan(plan: TrainingPlan) {
        workouts.value = plan.workouts
    }
    override fun observeWorkouts(): Flow<List<Workout>> = workouts.asStateFlow()
    override suspend fun hasPlan(weekIndex: Int): Boolean = workouts.value.isNotEmpty()
}

class LocalNutritionRepository(
    private val preferences: PreferencesRepository
) : NutritionRepository {
    private val planFlow = MutableStateFlow<NutritionPlan?>(null)

    override suspend fun generatePlan(profile: Profile, weekIndex: Int): NutritionPlan {
        val kcalTarget = tdeeKcal(profile)
        val macros = macrosFor(profile, kcalTarget)
        val languageTag = preferences.getLanguageTag()?.lowercase() ?: ""
        val templates = weeklyMealTemplates(languageTag)
        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val mealsByDay = mutableMapOf<String, List<Meal>>()
        val shopping = mutableSetOf<String>()

        days.forEachIndexed { index, day ->
            val meals = buildMealsForDay(templates[index % templates.size], macros, kcalTarget)
            mealsByDay[day] = meals
            meals.flatMap { it.ingredients }.map { it.trim() }.filter { it.isNotEmpty() }.forEach { shopping += it }
        }
        val plan = NutritionPlan(weekIndex, mealsByDay, shopping.toList().sorted())
        planFlow.value = plan
        return plan
    }

    override suspend fun savePlan(plan: NutritionPlan) {
        planFlow.value = plan
    }

    override fun observePlan(): Flow<NutritionPlan?> = planFlow.asStateFlow()
    override suspend fun hasPlan(weekIndex: Int): Boolean = planFlow.value?.weekIndex == weekIndex

    private data class MealTemplate(val name: String, val ingredients: List<String>)

    private fun weeklyMealTemplates(languageCode: String): List<List<MealTemplate>> {
        val lang = languageCode.lowercase()
        return if (lang.startsWith("ru")) {
            listOf(
                listOf(
                    MealTemplate("Р С›Р Р†РЎРѓРЎРЏР Р…Р С”Р В° РЎРѓ РЎРЏР С–Р С•Р Т‘Р В°Р СР С‘", listOf("Р С•Р Р†РЎРѓРЎРЏР Р…Р С”Р В°", "РЎРЏР С–Р С•Р Т‘РЎвЂ№", "Р СР С•Р В»Р С•Р С”Р С•")),
                    MealTemplate("Р С™РЎС“РЎР‚Р С‘Р Р…Р В°РЎРЏ Р С–РЎР‚РЎС“Р Т‘Р С”Р В° РЎРѓ Р С”Р С‘Р Р…Р С•Р В°", listOf("Р С”РЎС“РЎР‚Р С‘Р Р…Р В°РЎРЏ Р С–РЎР‚РЎС“Р Т‘Р С”Р В°", "Р С”Р С‘Р Р…Р С•Р В°", "Р В±РЎР‚Р С•Р С”Р С”Р С•Р В»Р С‘")),
                    MealTemplate("Р вЂєР С•РЎРѓР С•РЎРѓРЎРЉ РЎРѓ РЎР‚Р С‘РЎРѓР С•Р С", listOf("Р В»Р С•РЎРѓР С•РЎРѓРЎРЉ", "Р С”Р С•РЎР‚Р С‘РЎвЂЎР Р…Р ВµР Р†РЎвЂ№Р в„– РЎР‚Р С‘РЎРѓ", "РЎРѓР С—Р В°РЎР‚Р В¶Р В°"))
                ),
                listOf(
                    MealTemplate("Р СћР Р†Р С•РЎР‚Р С•Р С– РЎРѓ Р С•РЎР‚Р ВµРЎвЂ¦Р В°Р СР С‘", listOf("РЎвЂљР Р†Р С•РЎР‚Р С•Р С–", "Р С–РЎР‚Р ВµРЎвЂ Р С”Р С‘Р Вµ Р С•РЎР‚Р ВµРЎвЂ¦Р С‘", "Р СРЎвЂР Т‘")),
                    MealTemplate("Р вЂњР С•Р Р†РЎРЏР Т‘Р С‘Р Р…Р В° РЎРѓ Р С–РЎР‚Р ВµРЎвЂЎР С”Р С•Р в„–", listOf("Р С–Р С•Р Р†РЎРЏР Т‘Р С‘Р Р…Р В°", "Р С–РЎР‚Р ВµРЎвЂЎР С”Р В°", "Р В·Р ВµР В»РЎвЂР Р…РЎвЂ№Р Вµ Р С•Р Р†Р С•РЎвЂ°Р С‘")),
                    MealTemplate("Р ВР Р…Р Т‘Р ВµР в„–Р С”Р В° РЎРѓ Р С”Р В°РЎР‚РЎвЂљР С•РЎвЂћР ВµР В»Р ВµР С", listOf("Р С‘Р Р…Р Т‘Р ВµР в„–Р С”Р В°", "Р В·Р В°Р С—Р ВµРЎвЂЎРЎвЂР Р…Р Р…РЎвЂ№Р в„– Р С”Р В°РЎР‚РЎвЂљР С•РЎвЂћР ВµР В»РЎРЉ", "РЎРѓР В°Р В»Р В°РЎвЂљ"))
                ),
                listOf(
                    MealTemplate("Р С›Р СР В»Р ВµРЎвЂљ РЎРѓ Р С•Р Р†Р С•РЎвЂ°Р В°Р СР С‘", listOf("РЎРЏР в„–РЎвЂ Р В°", "Р С—Р ВµРЎР‚Р ВµРЎвЂ ", "Р С—Р С•Р СР С‘Р Т‘Р С•РЎР‚РЎвЂ№")),
                    MealTemplate("Р СћРЎР‚Р ВµРЎРѓР С”Р В° РЎРѓ Р В±РЎС“Р В»Р С–РЎС“РЎР‚Р С•Р С", listOf("РЎвЂљРЎР‚Р ВµРЎРѓР С”Р В°", "Р В±РЎС“Р В»Р С–РЎС“РЎР‚", "РЎв‚¬Р С—Р С‘Р Р…Р В°РЎвЂљ")),
                    MealTemplate("Р СћРЎС“РЎв‚¬РЎвЂР Р…Р В°РЎРЏ Р С–Р С•Р Р†РЎРЏР Т‘Р С‘Р Р…Р В°", listOf("Р С–Р С•Р Р†РЎРЏР Т‘Р С‘Р Р…Р В°", "Р С•Р Р†Р С•РЎвЂ°Р Р…Р С•Р Вµ РЎР‚Р В°Р С–РЎС“", "Р В·Р ВµР В»Р ВµР Р…РЎРЉ"))
                ),
                listOf(
                    MealTemplate("Р вЂњРЎР‚Р ВµРЎвЂЎР ВµРЎРѓР С”Р С‘Р в„– Р в„–Р С•Р С–РЎС“РЎР‚РЎвЂљ РЎРѓ РЎвЂћРЎР‚РЎС“Р С”РЎвЂљР В°Р СР С‘", listOf("Р в„–Р С•Р С–РЎС“РЎР‚РЎвЂљ", "РЎРЏР В±Р В»Р С•Р С”Р С•", "Р С•РЎР‚Р ВµРЎвЂ¦Р С‘")),
                    MealTemplate("Р С™РЎС“РЎР‚Р С‘РЎвЂ Р В° РЎвЂљР ВµРЎР‚Р С‘РЎРЏР С”Р С‘", listOf("Р С”РЎС“РЎР‚Р С‘РЎвЂ Р В°", "РЎРѓР С•РЎС“РЎРѓ РЎвЂљР ВµРЎР‚Р С‘РЎРЏР С”Р С‘", "РЎР‚Р С‘РЎРѓ", "Р С•Р Р†Р С•РЎвЂ°Р С‘")),
                    MealTemplate("Р СџР В°РЎРѓРЎвЂљР В° Р С‘Р В· РЎвЂ Р ВµР В»РЎРЉР Р…Р С•Р В·Р ВµРЎР‚Р Р…Р С•Р Р†Р С•Р в„– Р СРЎС“Р С”Р С‘", listOf("РЎвЂ Р ВµР В»РЎРЉР Р…Р С•Р В·Р ВµРЎР‚Р Р…Р С•Р Р†Р В°РЎРЏ Р С—Р В°РЎРѓРЎвЂљР В°", "РЎвЂљР С•Р СР В°РЎвЂљР Р…РЎвЂ№Р в„– РЎРѓР С•РЎС“РЎРѓ", "РЎРѓРЎвЂ№РЎР‚"))
                ),
                listOf(
                    MealTemplate("Р РЋР СРЎС“Р В·Р С‘ РЎРѓР С• РЎв‚¬Р С—Р С‘Р Р…Р В°РЎвЂљР С•Р С", listOf("РЎв‚¬Р С—Р С‘Р Р…Р В°РЎвЂљ", "Р В±Р В°Р Р…Р В°Р Р…", "Р в„–Р С•Р С–РЎС“РЎР‚РЎвЂљ")),
                    MealTemplate("Р РЋРЎвЂљР ВµР в„–Р С” РЎРѓ Р С•Р Р†Р С•РЎвЂ°Р В°Р СР С‘", listOf("Р С–Р С•Р Р†РЎРЏР В¶Р С‘Р в„– РЎРѓРЎвЂљР ВµР в„–Р С”", "Р С”Р В°Р В±Р В°РЎвЂЎР С•Р С”", "Р С—Р ВµРЎР‚Р ВµРЎвЂ ")),
                    MealTemplate("Р СћР С•РЎвЂћРЎС“ РЎРѓ РЎР‚Р С‘РЎРѓР С•Р Р†Р С•Р в„– Р В»Р В°Р С—РЎв‚¬Р С•Р в„–", listOf("РЎвЂљР С•РЎвЂћРЎС“", "РЎР‚Р С‘РЎРѓР С•Р Р†Р В°РЎРЏ Р В»Р В°Р С—РЎв‚¬Р В°", "Р С•Р Р†Р С•РЎвЂ°Р С‘"))
                ),
                listOf(
                    MealTemplate("Р вЂњРЎР‚Р В°Р Р…Р С•Р В»Р В° РЎРѓ Р С”Р ВµРЎвЂћР С‘РЎР‚Р С•Р С", listOf("Р С–РЎР‚Р В°Р Р…Р С•Р В»Р В°", "Р С”Р ВµРЎвЂћР С‘РЎР‚", "РЎРЏР С–Р С•Р Т‘РЎвЂ№")),
                    MealTemplate("Р вЂєР С•РЎРѓР С•РЎРѓРЎРЉ Р Р…Р В° Р С—Р В°РЎР‚РЎС“", listOf("Р В»Р С•РЎРѓР С•РЎРѓРЎРЉ", "РЎвЂљРЎвЂ№Р С”Р Р†Р В°", "Р В·Р ВµР В»РЎвЂР Р…Р В°РЎРЏ РЎвЂћР В°РЎРѓР С•Р В»РЎРЉ")),
                    MealTemplate("Р С™РЎС“РЎР‚Р С‘РЎвЂ Р В° РЎРѓ РЎвЂЎР ВµРЎвЂЎР ВµР Р†Р С‘РЎвЂ Р ВµР в„–", listOf("Р С”РЎС“РЎР‚Р С‘РЎвЂ Р В°", "РЎвЂЎР ВµРЎвЂЎР ВµР Р†Р С‘РЎвЂ Р В°", "Р СР С•РЎР‚Р С”Р С•Р Р†РЎРЉ"))
                ),
                listOf(
                    MealTemplate("Р СџР В°Р Р…Р С”Р ВµР в„–Р С”Р С‘ Р С‘Р В· Р С•Р Р†РЎРѓРЎРЏР Р…Р С”Р С‘", listOf("Р С•Р Р†РЎРѓРЎРЏР Р…Р С”Р В°", "РЎРЏР в„–РЎвЂ Р В°", "РЎРЏР С–Р С•Р Т‘РЎвЂ№")),
                    MealTemplate("Р РЋР В°Р В»Р В°РЎвЂљ РЎРѓ РЎвЂљРЎС“Р Р…РЎвЂ Р С•Р С", listOf("РЎвЂљРЎС“Р Р…Р ВµРЎвЂ ", "Р В»Р С‘РЎРѓРЎвЂљР С•Р Р†РЎвЂ№Р в„– РЎРѓР В°Р В»Р В°РЎвЂљ", "Р С•Р В»Р С‘Р Р†Р С”Р С‘")),
                    MealTemplate("Р вЂ”Р В°Р С—Р ВµРЎвЂЎРЎвЂР Р…Р Р…Р В°РЎРЏ РЎвЂљРЎР‚Р ВµРЎРѓР С”Р В°", listOf("РЎвЂљРЎР‚Р ВµРЎРѓР С”Р В°", "Р С”Р В°РЎР‚РЎвЂљР С•РЎвЂћР ВµР В»РЎРЉ", "Р В±РЎР‚Р С•Р С”Р С”Р С•Р В»Р С‘"))
                )
            )
        } else {
            listOf(
                listOf(
                    MealTemplate("Oatmeal with Berries", listOf("oats", "berries", "milk")),
                    MealTemplate("Chicken Quinoa Bowl", listOf("chicken breast", "quinoa", "broccoli")),
                    MealTemplate("Salmon with Brown Rice", listOf("salmon", "brown rice", "asparagus"))
                ),
                listOf(
                    MealTemplate("Greek Yogurt Parfait", listOf("greek yogurt", "granola", "honey")),
                    MealTemplate("Beef and Buckwheat", listOf("lean beef", "buckwheat", "green vegetables")),
                    MealTemplate("Turkey with Roast Potatoes", listOf("turkey", "roasted potatoes", "mixed greens"))
                ),
                listOf(
                    MealTemplate("Veggie Omelette", listOf("eggs", "bell pepper", "tomato")),
                    MealTemplate("Cod with Bulgur", listOf("cod", "bulgur", "spinach")),
                    MealTemplate("Hearty Beef Stew", listOf("beef", "root vegetables", "herbs"))
                ),
                listOf(
                    MealTemplate("Fruit & Yogurt Bowl", listOf("yogurt", "apple", "nuts")),
                    MealTemplate("Teriyaki Chicken", listOf("chicken", "teriyaki sauce", "rice", "vegetables")),
                    MealTemplate("Wholegrain Pasta", listOf("wholegrain pasta", "tomato sauce", "cheese"))
                ),
                listOf(
                    MealTemplate("Spinach Smoothie", listOf("spinach", "banana", "yogurt")),
                    MealTemplate("Sirloin and Veggies", listOf("sirloin", "zucchini", "bell pepper")),
                    MealTemplate("Tofu Stir Fry", listOf("tofu", "rice noodles", "vegetables"))
                ),
                listOf(
                    MealTemplate("Granola with Kefir", listOf("granola", "kefir", "berries")),
                    MealTemplate("Steamed Salmon", listOf("salmon", "pumpkin", "green beans")),
                    MealTemplate("Chicken with Lentils", listOf("chicken fillet", "lentils", "carrot"))
                ),
                listOf(
                    MealTemplate("Oat Pancakes", listOf("oats", "eggs", "berries")),
                    MealTemplate("Tuna Salad", listOf("tuna", "lettuce", "olives")),
                    MealTemplate("Baked Cod", listOf("cod", "potatoes", "broccoli"))
                )
            )
        }
    }

    private fun buildMealsForDay(templates: List<MealTemplate>, macrosDay: Macros, kcalTarget: Int): List<Meal> {
        val count = templates.size.coerceAtLeast(1)
        val calories = distributeTotal(kcalTarget, count)
        val proteins = distributeTotal(macrosDay.proteinGrams, count)
        val fats = distributeTotal(macrosDay.fatGrams, count)
        val carbs = distributeTotal(macrosDay.carbsGrams, count)
        return templates.mapIndexed { index, template ->
            Meal(
                name = template.name,
                ingredients = template.ingredients,
                kcal = calories[index],
                macros = Macros(
                    proteinGrams = proteins[index],
                    fatGrams = fats[index],
                    carbsGrams = carbs[index],
                    kcal = calories[index]
                )
            )
        }
    }

    private fun distributeTotal(total: Int, count: Int): List<Int> {
        if (count <= 0) return emptyList()
        if (total <= 0) return List(count) { 0 }
        val base = total / count
        val remainder = total % count
        return List(count) { index -> base + if (index < remainder) 1 else 0 }
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

class StubAdviceRepository : AdviceRepository {
    private val adviceMap = MutableStateFlow<Map<String, Advice>>(emptyMap())

    override suspend fun getAdvice(profile: Profile, context: Map<String, Any?>): Advice {
        val topic = (context["topic"] as? String)?.lowercase() ?: "general"
        val current = adviceMap.value[topic]
        if (current != null) return current
        val defaults = defaultAdvice(topic)
        adviceMap.value = adviceMap.value + (topic to defaults)
        return defaults
    }

    override suspend fun saveAdvice(topic: String, advice: Advice) {
        val key = topic.lowercase()
        adviceMap.value = adviceMap.value + (key to advice)
    }

    override fun observeAdvice(topic: String): Flow<Advice> {
        val key = topic.lowercase()
        return adviceMap.map { it[key] ?: defaultAdvice(key) }
    }

    override suspend fun hasAdvice(topic: String): Boolean =
        adviceMap.value.containsKey(topic.lowercase())

    private fun defaultAdvice(topic: String): Advice = when (topic) {
        "sleep" -> Advice(
            messages = listOf(
                "Sleep 7-9 hours when possible.",
                "Keep a consistent bedtime routine.",
                "Limit caffeine six hours before bed."
            )
        )
        else -> Advice(messages = listOf("Stay hydrated", "Warm up properly"))
    }
}
class StubPurchasesRepository : PurchasesRepository {
    override suspend fun isSubscriptionActive(): Boolean = false
}

class StubSyncRepository : SyncRepository {
    override suspend fun syncAll(): Boolean = true
}




