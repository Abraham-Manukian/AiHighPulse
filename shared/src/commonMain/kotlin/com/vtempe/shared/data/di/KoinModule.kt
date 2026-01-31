package com.vtempe.shared.data.di

import com.vtempe.shared.data.network.ApiClient
import com.vtempe.shared.data.network.createHttpClient
import com.vtempe.shared.domain.model.*
import com.vtempe.shared.domain.repository.*
import com.vtempe.shared.data.repo.TrainingRepositoryDb
import com.vtempe.shared.data.repo.NetworkAiTrainerRepository
import com.vtempe.shared.data.repo.NetworkChatRepository
import com.vtempe.shared.data.repo.AiResponseCache
import com.vtempe.shared.data.repo.NutritionRepositoryDb
import com.vtempe.shared.domain.usecase.*
import com.vtempe.shared.data.repo.ProfileSettingsRepository
import com.vtempe.shared.data.repo.ProfileRepositoryDb
import com.vtempe.shared.data.repo.SettingsPreferencesRepository
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
                    MealTemplate("Р В РЎвЂєР В Р вЂ Р РЋР С“Р РЋР РЏР В Р вЂ¦Р В РЎвЂќР В Р’В° Р РЋР С“ Р РЋР РЏР В РЎвЂ“Р В РЎвЂўР В РўвЂР В Р’В°Р В РЎВР В РЎвЂ", listOf("Р В РЎвЂўР В Р вЂ Р РЋР С“Р РЋР РЏР В Р вЂ¦Р В РЎвЂќР В Р’В°", "Р РЋР РЏР В РЎвЂ“Р В РЎвЂўР В РўвЂР РЋРІР‚в„–", "Р В РЎВР В РЎвЂўР В Р’В»Р В РЎвЂўР В РЎвЂќР В РЎвЂў")),
                    MealTemplate("Р В РЎв„ўР РЋРЎвЂњР РЋР вЂљР В РЎвЂР В Р вЂ¦Р В Р’В°Р РЋР РЏ Р В РЎвЂ“Р РЋР вЂљР РЋРЎвЂњР В РўвЂР В РЎвЂќР В Р’В° Р РЋР С“ Р В РЎвЂќР В РЎвЂР В Р вЂ¦Р В РЎвЂўР В Р’В°", listOf("Р В РЎвЂќР РЋРЎвЂњР РЋР вЂљР В РЎвЂР В Р вЂ¦Р В Р’В°Р РЋР РЏ Р В РЎвЂ“Р РЋР вЂљР РЋРЎвЂњР В РўвЂР В РЎвЂќР В Р’В°", "Р В РЎвЂќР В РЎвЂР В Р вЂ¦Р В РЎвЂўР В Р’В°", "Р В Р’В±Р РЋР вЂљР В РЎвЂўР В РЎвЂќР В РЎвЂќР В РЎвЂўР В Р’В»Р В РЎвЂ")),
                    MealTemplate("Р В РІР‚С”Р В РЎвЂўР РЋР С“Р В РЎвЂўР РЋР С“Р РЋР Р‰ Р РЋР С“ Р РЋР вЂљР В РЎвЂР РЋР С“Р В РЎвЂўР В РЎВ", listOf("Р В Р’В»Р В РЎвЂўР РЋР С“Р В РЎвЂўР РЋР С“Р РЋР Р‰", "Р В РЎвЂќР В РЎвЂўР РЋР вЂљР В РЎвЂР РЋРІР‚РЋР В Р вЂ¦Р В Р’ВµР В Р вЂ Р РЋРІР‚в„–Р В РІвЂћвЂ“ Р РЋР вЂљР В РЎвЂР РЋР С“", "Р РЋР С“Р В РЎвЂ”Р В Р’В°Р РЋР вЂљР В Р’В¶Р В Р’В°"))
                ),
                listOf(
                    MealTemplate("Р В РЎС›Р В Р вЂ Р В РЎвЂўР РЋР вЂљР В РЎвЂўР В РЎвЂ“ Р РЋР С“ Р В РЎвЂўР РЋР вЂљР В Р’ВµР РЋРІР‚В¦Р В Р’В°Р В РЎВР В РЎвЂ", listOf("Р РЋРІР‚С™Р В Р вЂ Р В РЎвЂўР РЋР вЂљР В РЎвЂўР В РЎвЂ“", "Р В РЎвЂ“Р РЋР вЂљР В Р’ВµР РЋРІР‚В Р В РЎвЂќР В РЎвЂР В Р’Вµ Р В РЎвЂўР РЋР вЂљР В Р’ВµР РЋРІР‚В¦Р В РЎвЂ", "Р В РЎВР РЋРІР‚ВР В РўвЂ")),
                    MealTemplate("Р В РІР‚СљР В РЎвЂўР В Р вЂ Р РЋР РЏР В РўвЂР В РЎвЂР В Р вЂ¦Р В Р’В° Р РЋР С“ Р В РЎвЂ“Р РЋР вЂљР В Р’ВµР РЋРІР‚РЋР В РЎвЂќР В РЎвЂўР В РІвЂћвЂ“", listOf("Р В РЎвЂ“Р В РЎвЂўР В Р вЂ Р РЋР РЏР В РўвЂР В РЎвЂР В Р вЂ¦Р В Р’В°", "Р В РЎвЂ“Р РЋР вЂљР В Р’ВµР РЋРІР‚РЋР В РЎвЂќР В Р’В°", "Р В Р’В·Р В Р’ВµР В Р’В»Р РЋРІР‚ВР В Р вЂ¦Р РЋРІР‚в„–Р В Р’Вµ Р В РЎвЂўР В Р вЂ Р В РЎвЂўР РЋРІР‚В°Р В РЎвЂ")),
                    MealTemplate("Р В Р’ВР В Р вЂ¦Р В РўвЂР В Р’ВµР В РІвЂћвЂ“Р В РЎвЂќР В Р’В° Р РЋР С“ Р В РЎвЂќР В Р’В°Р РЋР вЂљР РЋРІР‚С™Р В РЎвЂўР РЋРІР‚С›Р В Р’ВµР В Р’В»Р В Р’ВµР В РЎВ", listOf("Р В РЎвЂР В Р вЂ¦Р В РўвЂР В Р’ВµР В РІвЂћвЂ“Р В РЎвЂќР В Р’В°", "Р В Р’В·Р В Р’В°Р В РЎвЂ”Р В Р’ВµР РЋРІР‚РЋР РЋРІР‚ВР В Р вЂ¦Р В Р вЂ¦Р РЋРІР‚в„–Р В РІвЂћвЂ“ Р В РЎвЂќР В Р’В°Р РЋР вЂљР РЋРІР‚С™Р В РЎвЂўР РЋРІР‚С›Р В Р’ВµР В Р’В»Р РЋР Р‰", "Р РЋР С“Р В Р’В°Р В Р’В»Р В Р’В°Р РЋРІР‚С™"))
                ),
                listOf(
                    MealTemplate("Р В РЎвЂєР В РЎВР В Р’В»Р В Р’ВµР РЋРІР‚С™ Р РЋР С“ Р В РЎвЂўР В Р вЂ Р В РЎвЂўР РЋРІР‚В°Р В Р’В°Р В РЎВР В РЎвЂ", listOf("Р РЋР РЏР В РІвЂћвЂ“Р РЋРІР‚В Р В Р’В°", "Р В РЎвЂ”Р В Р’ВµР РЋР вЂљР В Р’ВµР РЋРІР‚В ", "Р В РЎвЂ”Р В РЎвЂўР В РЎВР В РЎвЂР В РўвЂР В РЎвЂўР РЋР вЂљР РЋРІР‚в„–")),
                    MealTemplate("Р В РЎС›Р РЋР вЂљР В Р’ВµР РЋР С“Р В РЎвЂќР В Р’В° Р РЋР С“ Р В Р’В±Р РЋРЎвЂњР В Р’В»Р В РЎвЂ“Р РЋРЎвЂњР РЋР вЂљР В РЎвЂўР В РЎВ", listOf("Р РЋРІР‚С™Р РЋР вЂљР В Р’ВµР РЋР С“Р В РЎвЂќР В Р’В°", "Р В Р’В±Р РЋРЎвЂњР В Р’В»Р В РЎвЂ“Р РЋРЎвЂњР РЋР вЂљ", "Р РЋРІвЂљВ¬Р В РЎвЂ”Р В РЎвЂР В Р вЂ¦Р В Р’В°Р РЋРІР‚С™")),
                    MealTemplate("Р В РЎС›Р РЋРЎвЂњР РЋРІвЂљВ¬Р РЋРІР‚ВР В Р вЂ¦Р В Р’В°Р РЋР РЏ Р В РЎвЂ“Р В РЎвЂўР В Р вЂ Р РЋР РЏР В РўвЂР В РЎвЂР В Р вЂ¦Р В Р’В°", listOf("Р В РЎвЂ“Р В РЎвЂўР В Р вЂ Р РЋР РЏР В РўвЂР В РЎвЂР В Р вЂ¦Р В Р’В°", "Р В РЎвЂўР В Р вЂ Р В РЎвЂўР РЋРІР‚В°Р В Р вЂ¦Р В РЎвЂўР В Р’Вµ Р РЋР вЂљР В Р’В°Р В РЎвЂ“Р РЋРЎвЂњ", "Р В Р’В·Р В Р’ВµР В Р’В»Р В Р’ВµР В Р вЂ¦Р РЋР Р‰"))
                ),
                listOf(
                    MealTemplate("Р В РІР‚СљР РЋР вЂљР В Р’ВµР РЋРІР‚РЋР В Р’ВµР РЋР С“Р В РЎвЂќР В РЎвЂР В РІвЂћвЂ“ Р В РІвЂћвЂ“Р В РЎвЂўР В РЎвЂ“Р РЋРЎвЂњР РЋР вЂљР РЋРІР‚С™ Р РЋР С“ Р РЋРІР‚С›Р РЋР вЂљР РЋРЎвЂњР В РЎвЂќР РЋРІР‚С™Р В Р’В°Р В РЎВР В РЎвЂ", listOf("Р В РІвЂћвЂ“Р В РЎвЂўР В РЎвЂ“Р РЋРЎвЂњР РЋР вЂљР РЋРІР‚С™", "Р РЋР РЏР В Р’В±Р В Р’В»Р В РЎвЂўР В РЎвЂќР В РЎвЂў", "Р В РЎвЂўР РЋР вЂљР В Р’ВµР РЋРІР‚В¦Р В РЎвЂ")),
                    MealTemplate("Р В РЎв„ўР РЋРЎвЂњР РЋР вЂљР В РЎвЂР РЋРІР‚В Р В Р’В° Р РЋРІР‚С™Р В Р’ВµР РЋР вЂљР В РЎвЂР РЋР РЏР В РЎвЂќР В РЎвЂ", listOf("Р В РЎвЂќР РЋРЎвЂњР РЋР вЂљР В РЎвЂР РЋРІР‚В Р В Р’В°", "Р РЋР С“Р В РЎвЂўР РЋРЎвЂњР РЋР С“ Р РЋРІР‚С™Р В Р’ВµР РЋР вЂљР В РЎвЂР РЋР РЏР В РЎвЂќР В РЎвЂ", "Р РЋР вЂљР В РЎвЂР РЋР С“", "Р В РЎвЂўР В Р вЂ Р В РЎвЂўР РЋРІР‚В°Р В РЎвЂ")),
                    MealTemplate("Р В РЎСџР В Р’В°Р РЋР С“Р РЋРІР‚С™Р В Р’В° Р В РЎвЂР В Р’В· Р РЋРІР‚В Р В Р’ВµР В Р’В»Р РЋР Р‰Р В Р вЂ¦Р В РЎвЂўР В Р’В·Р В Р’ВµР РЋР вЂљР В Р вЂ¦Р В РЎвЂўР В Р вЂ Р В РЎвЂўР В РІвЂћвЂ“ Р В РЎВР РЋРЎвЂњР В РЎвЂќР В РЎвЂ", listOf("Р РЋРІР‚В Р В Р’ВµР В Р’В»Р РЋР Р‰Р В Р вЂ¦Р В РЎвЂўР В Р’В·Р В Р’ВµР РЋР вЂљР В Р вЂ¦Р В РЎвЂўР В Р вЂ Р В Р’В°Р РЋР РЏ Р В РЎвЂ”Р В Р’В°Р РЋР С“Р РЋРІР‚С™Р В Р’В°", "Р РЋРІР‚С™Р В РЎвЂўР В РЎВР В Р’В°Р РЋРІР‚С™Р В Р вЂ¦Р РЋРІР‚в„–Р В РІвЂћвЂ“ Р РЋР С“Р В РЎвЂўР РЋРЎвЂњР РЋР С“", "Р РЋР С“Р РЋРІР‚в„–Р РЋР вЂљ"))
                ),
                listOf(
                    MealTemplate("Р В Р Р‹Р В РЎВР РЋРЎвЂњР В Р’В·Р В РЎвЂ Р РЋР С“Р В РЎвЂў Р РЋРІвЂљВ¬Р В РЎвЂ”Р В РЎвЂР В Р вЂ¦Р В Р’В°Р РЋРІР‚С™Р В РЎвЂўР В РЎВ", listOf("Р РЋРІвЂљВ¬Р В РЎвЂ”Р В РЎвЂР В Р вЂ¦Р В Р’В°Р РЋРІР‚С™", "Р В Р’В±Р В Р’В°Р В Р вЂ¦Р В Р’В°Р В Р вЂ¦", "Р В РІвЂћвЂ“Р В РЎвЂўР В РЎвЂ“Р РЋРЎвЂњР РЋР вЂљР РЋРІР‚С™")),
                    MealTemplate("Р В Р Р‹Р РЋРІР‚С™Р В Р’ВµР В РІвЂћвЂ“Р В РЎвЂќ Р РЋР С“ Р В РЎвЂўР В Р вЂ Р В РЎвЂўР РЋРІР‚В°Р В Р’В°Р В РЎВР В РЎвЂ", listOf("Р В РЎвЂ“Р В РЎвЂўР В Р вЂ Р РЋР РЏР В Р’В¶Р В РЎвЂР В РІвЂћвЂ“ Р РЋР С“Р РЋРІР‚С™Р В Р’ВµР В РІвЂћвЂ“Р В РЎвЂќ", "Р В РЎвЂќР В Р’В°Р В Р’В±Р В Р’В°Р РЋРІР‚РЋР В РЎвЂўР В РЎвЂќ", "Р В РЎвЂ”Р В Р’ВµР РЋР вЂљР В Р’ВµР РЋРІР‚В ")),
                    MealTemplate("Р В РЎС›Р В РЎвЂўР РЋРІР‚С›Р РЋРЎвЂњ Р РЋР С“ Р РЋР вЂљР В РЎвЂР РЋР С“Р В РЎвЂўР В Р вЂ Р В РЎвЂўР В РІвЂћвЂ“ Р В Р’В»Р В Р’В°Р В РЎвЂ”Р РЋРІвЂљВ¬Р В РЎвЂўР В РІвЂћвЂ“", listOf("Р РЋРІР‚С™Р В РЎвЂўР РЋРІР‚С›Р РЋРЎвЂњ", "Р РЋР вЂљР В РЎвЂР РЋР С“Р В РЎвЂўР В Р вЂ Р В Р’В°Р РЋР РЏ Р В Р’В»Р В Р’В°Р В РЎвЂ”Р РЋРІвЂљВ¬Р В Р’В°", "Р В РЎвЂўР В Р вЂ Р В РЎвЂўР РЋРІР‚В°Р В РЎвЂ"))
                ),
                listOf(
                    MealTemplate("Р В РІР‚СљР РЋР вЂљР В Р’В°Р В Р вЂ¦Р В РЎвЂўР В Р’В»Р В Р’В° Р РЋР С“ Р В РЎвЂќР В Р’ВµР РЋРІР‚С›Р В РЎвЂР РЋР вЂљР В РЎвЂўР В РЎВ", listOf("Р В РЎвЂ“Р РЋР вЂљР В Р’В°Р В Р вЂ¦Р В РЎвЂўР В Р’В»Р В Р’В°", "Р В РЎвЂќР В Р’ВµР РЋРІР‚С›Р В РЎвЂР РЋР вЂљ", "Р РЋР РЏР В РЎвЂ“Р В РЎвЂўР В РўвЂР РЋРІР‚в„–")),
                    MealTemplate("Р В РІР‚С”Р В РЎвЂўР РЋР С“Р В РЎвЂўР РЋР С“Р РЋР Р‰ Р В Р вЂ¦Р В Р’В° Р В РЎвЂ”Р В Р’В°Р РЋР вЂљР РЋРЎвЂњ", listOf("Р В Р’В»Р В РЎвЂўР РЋР С“Р В РЎвЂўР РЋР С“Р РЋР Р‰", "Р РЋРІР‚С™Р РЋРІР‚в„–Р В РЎвЂќР В Р вЂ Р В Р’В°", "Р В Р’В·Р В Р’ВµР В Р’В»Р РЋРІР‚ВР В Р вЂ¦Р В Р’В°Р РЋР РЏ Р РЋРІР‚С›Р В Р’В°Р РЋР С“Р В РЎвЂўР В Р’В»Р РЋР Р‰")),
                    MealTemplate("Р В РЎв„ўР РЋРЎвЂњР РЋР вЂљР В РЎвЂР РЋРІР‚В Р В Р’В° Р РЋР С“ Р РЋРІР‚РЋР В Р’ВµР РЋРІР‚РЋР В Р’ВµР В Р вЂ Р В РЎвЂР РЋРІР‚В Р В Р’ВµР В РІвЂћвЂ“", listOf("Р В РЎвЂќР РЋРЎвЂњР РЋР вЂљР В РЎвЂР РЋРІР‚В Р В Р’В°", "Р РЋРІР‚РЋР В Р’ВµР РЋРІР‚РЋР В Р’ВµР В Р вЂ Р В РЎвЂР РЋРІР‚В Р В Р’В°", "Р В РЎВР В РЎвЂўР РЋР вЂљР В РЎвЂќР В РЎвЂўР В Р вЂ Р РЋР Р‰"))
                ),
                listOf(
                    MealTemplate("Р В РЎСџР В Р’В°Р В Р вЂ¦Р В РЎвЂќР В Р’ВµР В РІвЂћвЂ“Р В РЎвЂќР В РЎвЂ Р В РЎвЂР В Р’В· Р В РЎвЂўР В Р вЂ Р РЋР С“Р РЋР РЏР В Р вЂ¦Р В РЎвЂќР В РЎвЂ", listOf("Р В РЎвЂўР В Р вЂ Р РЋР С“Р РЋР РЏР В Р вЂ¦Р В РЎвЂќР В Р’В°", "Р РЋР РЏР В РІвЂћвЂ“Р РЋРІР‚В Р В Р’В°", "Р РЋР РЏР В РЎвЂ“Р В РЎвЂўР В РўвЂР РЋРІР‚в„–")),
                    MealTemplate("Р В Р Р‹Р В Р’В°Р В Р’В»Р В Р’В°Р РЋРІР‚С™ Р РЋР С“ Р РЋРІР‚С™Р РЋРЎвЂњР В Р вЂ¦Р РЋРІР‚В Р В РЎвЂўР В РЎВ", listOf("Р РЋРІР‚С™Р РЋРЎвЂњР В Р вЂ¦Р В Р’ВµР РЋРІР‚В ", "Р В Р’В»Р В РЎвЂР РЋР С“Р РЋРІР‚С™Р В РЎвЂўР В Р вЂ Р РЋРІР‚в„–Р В РІвЂћвЂ“ Р РЋР С“Р В Р’В°Р В Р’В»Р В Р’В°Р РЋРІР‚С™", "Р В РЎвЂўР В Р’В»Р В РЎвЂР В Р вЂ Р В РЎвЂќР В РЎвЂ")),
                    MealTemplate("Р В РІР‚вЂќР В Р’В°Р В РЎвЂ”Р В Р’ВµР РЋРІР‚РЋР РЋРІР‚ВР В Р вЂ¦Р В Р вЂ¦Р В Р’В°Р РЋР РЏ Р РЋРІР‚С™Р РЋР вЂљР В Р’ВµР РЋР С“Р В РЎвЂќР В Р’В°", listOf("Р РЋРІР‚С™Р РЋР вЂљР В Р’ВµР РЋР С“Р В РЎвЂќР В Р’В°", "Р В РЎвЂќР В Р’В°Р РЋР вЂљР РЋРІР‚С™Р В РЎвЂўР РЋРІР‚С›Р В Р’ВµР В Р’В»Р РЋР Р‰", "Р В Р’В±Р РЋР вЂљР В РЎвЂўР В РЎвЂќР В РЎвЂќР В РЎвЂўР В Р’В»Р В РЎвЂ"))
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





