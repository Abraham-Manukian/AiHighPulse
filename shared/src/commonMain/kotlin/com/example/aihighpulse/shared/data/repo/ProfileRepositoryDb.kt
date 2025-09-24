package com.example.aihighpulse.shared.data.repo

import com.example.aihighpulse.shared.db.AppDatabase
import com.example.aihighpulse.shared.domain.model.*
import com.example.aihighpulse.shared.domain.repository.ProfileRepository

class ProfileRepositoryDb(
    private val db: AppDatabase
) : ProfileRepository {
    override suspend fun getProfile(): Profile? {
        val row = db.profileQueries.selectProfile().executeAsOneOrNull() ?: return null
        val id = row.id
        val equipment = db.profileDetailsQueries.selectEquipment(id).executeAsList()
        val prefs = db.profileDetailsQueries.selectDietPrefs(id).executeAsList()
        val allergies = db.profileDetailsQueries.selectAllergies(id).executeAsList()
        val schedule = db.profileDetailsQueries.selectSchedule(id).executeAsList().associate { it.day to (it.active != 0L) }
        return Profile(
            id = row.id,
            age = row.age.toInt(),
            sex = runCatching { Sex.valueOf(row.sex) }.getOrDefault(Sex.OTHER),
            heightCm = row.heightCm.toInt(),
            weightKg = row.weightKg,
            goal = runCatching { Goal.valueOf(row.goal) }.getOrDefault(Goal.MAINTAIN),
            experienceLevel = row.experienceLevel.toInt(),
            constraints = Constraints(),
            equipment = Equipment(items = equipment),
            dietaryPreferences = prefs,
            allergies = allergies,
            weeklySchedule = schedule,
            budgetLevel = row.budgetLevel.toInt()
        )
    }

    override suspend fun upsertProfile(profile: Profile) {
        db.profileQueries.upsertProfile(
            id = profile.id,
            age = profile.age.toLong(),
            sex = profile.sex.name,
            heightCm = profile.heightCm.toLong(),
            weightKg = profile.weightKg,
            goal = profile.goal.name,
            experienceLevel = profile.experienceLevel.toLong(),
            budgetLevel = profile.budgetLevel.toLong()
        )
        // Replace detail tables
        db.profileDetailsQueries.deleteEquipmentForProfile(profile.id)
        profile.equipment.items.forEach { item -> db.profileDetailsQueries.insertEquipment(profile.id, item) }
        db.profileDetailsQueries.deleteDietPrefForProfile(profile.id)
        profile.dietaryPreferences.forEach { pref -> db.profileDetailsQueries.insertDietPref(profile.id, pref) }
        db.profileDetailsQueries.deleteAllergyForProfile(profile.id)
        profile.allergies.forEach { a -> db.profileDetailsQueries.insertAllergy(profile.id, a) }
        db.profileDetailsQueries.deleteScheduleForProfile(profile.id)
        profile.weeklySchedule.forEach { (day, active) -> db.profileDetailsQueries.insertSchedule(profile.id, day, if (active) 1L else 0L) }
    }
}
