package com.example.aihighpulse.shared.data.repo

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.aihighpulse.shared.db.AppDatabase
import com.example.aihighpulse.shared.domain.model.*
import com.example.aihighpulse.shared.domain.repository.TrainingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class TrainingRepositoryDb(
    private val db: AppDatabase,
    private val ai: com.example.aihighpulse.shared.domain.repository.AiTrainerRepository,
    private val validateSubscription: com.example.aihighpulse.shared.domain.usecase.ValidateSubscription,
) : TrainingRepository {
    override suspend fun generatePlan(profile: Profile, weekIndex: Int): TrainingPlan {
        // If subscription active, try AI plan first
        val useAi = runCatching { validateSubscription() }.getOrDefault(false)
        if (useAi) {
            val aiPlan = runCatching { ai.generateTrainingPlan(profile, weekIndex) }.getOrNull()
            if (aiPlan != null) {
                persistPlan(aiPlan)
                return aiPlan
            }
        }

        // Reset previous week plan
        db.workoutQueries.deleteWorkoutsByWeek(weekIndex.toLong())

        // Seed minimal exercises dictionary
        db.exerciseQueries.upsertExercise("squat", "Приседания", "[\"legs\"]", 2L)
        db.exerciseQueries.upsertExercise("bench", "Жим лёжа", "[\"chest\"]", 2L)
        db.exerciseQueries.upsertExercise("deadlift", "Становая тяга", "[\"back\",\"legs\"]", 3L)
        db.exerciseQueries.upsertExercise("ohp", "Жим стоя", "[\"shoulders\"]", 2L)
        db.exerciseQueries.upsertExercise("row", "Тяга в наклоне", "[\"back\"]", 2L)
        db.exerciseQueries.upsertExercise("pullup", "Подтягивания", "[\"back\"]", 2L)
        db.exerciseQueries.upsertExercise("lunge", "Выпады", "[\"legs\"]", 1L)

        // Simple offline plan persisted to DB
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        data class SetPlan(val exId: String, val reps: Int, val weight: Double?)
        val workouts = List(3) { day ->
            val id = "w_${weekIndex}_$day"
            db.workoutQueries.insertWorkout(id, weekIndex.toLong(), today.toString())
            val plan = when (day) {
                0 -> listOf(SetPlan("squat", 8, 40.0), SetPlan("bench", 10, 30.0), SetPlan("row", 10, 30.0))
                1 -> listOf(SetPlan("deadlift", 5, 60.0), SetPlan("ohp", 8, 20.0), SetPlan("lunge", 10, 20.0))
                else -> listOf(SetPlan("squat", 6, 45.0), SetPlan("bench", 8, 32.5), SetPlan("pullup", 6, null))
            }
            val sets = mutableListOf<WorkoutSet>()
            plan.forEach { p ->
                db.workoutQueries.insertSet(id, p.exId, p.reps.toLong(), p.weight, 7.0)
                sets += WorkoutSet(p.exId, p.reps, p.weight, 7.0)
            }
            Workout(id = id, date = today, sets = sets)
        }
        val plan = TrainingPlan(weekIndex, workouts)
        return plan
    }

    override suspend fun logSet(workoutId: String, set: WorkoutSet) {
        db.workoutQueries.insertSet(workoutId, set.exerciseId, set.reps.toLong(), set.weightKg, set.rpe)
    }

    override fun observeWorkouts(): Flow<List<Workout>> =
        db.workoutQueries.selectWorkoutsWithSets()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { rows ->
                val grouped = rows.groupBy { it.id }
                grouped.map { (id, list) ->
                    val dateStr = list.firstOrNull()?.date ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
                    val date = LocalDate.parse(dateStr)
                    val sets = list.filter { it.exerciseId != null }.map { r ->
                        WorkoutSet(
                            exerciseId = r.exerciseId!!,
                            reps = r.reps!!.toInt(),
                            weightKg = r.weightKg,
                            rpe = r.rpe
                        )
                    }
                    Workout(id = id, date = date, sets = sets)
                }
            }

    private fun persistPlan(plan: TrainingPlan) {
        db.workoutQueries.deleteWorkoutsByWeek(plan.weekIndex.toLong())
        plan.workouts.forEach { w ->
            db.workoutQueries.insertWorkout(w.id, plan.weekIndex.toLong(), w.date.toString())
            w.sets.forEach { s ->
                db.workoutQueries.insertSet(w.id, s.exerciseId, s.reps.toLong(), s.weightKg, s.rpe)
            }
        }
    }
}
