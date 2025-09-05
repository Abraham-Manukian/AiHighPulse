package com.example.aihighpulse.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aihighpulse.shared.domain.model.*
import com.example.aihighpulse.shared.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OnboardingState(
    val age: String = "28",
    val sex: Sex = Sex.MALE,
    val heightCm: String = "178",
    val weightKg: String = "78",
    val goal: Goal = Goal.MAINTAIN,
    val experienceLevel: Int = 3,
    val dietaryPreferences: String = "",
    val allergies: String = "",
    val equipment: String = "гантели, турник",
    val days: Map<String, Boolean> = mapOf(
        "Mon" to true, "Tue" to true, "Wed" to false, "Thu" to true, "Fri" to false, "Sat" to false, "Sun" to false
    ),
    val saving: Boolean = false,
    val error: String? = null
)

class OnboardingViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    fun update(block: (OnboardingState) -> OnboardingState) { _state.value = block(_state.value) }

    fun save(onSuccess: () -> Unit) {
        val s = _state.value
        val age = s.age.toIntOrNull()
        val h = s.heightCm.toIntOrNull()
        val w = s.weightKg.toDoubleOrNull()
        if (age == null || h == null || w == null) {
            _state.value = s.copy(error = "Проверьте корректность данных")
            return
        }
        viewModelScope.launch {
            _state.value = s.copy(saving = true, error = null)
            val profile = Profile(
                id = "local",
                age = age,
                sex = s.sex,
                heightCm = h,
                weightKg = w,
                goal = s.goal,
                experienceLevel = s.experienceLevel,
                constraints = Constraints(),
                equipment = Equipment(items = s.equipment.split(',').map { it.trim() }.filter { it.isNotEmpty() }),
                dietaryPreferences = s.dietaryPreferences.split(',').map { it.trim() }.filter { it.isNotEmpty() },
                allergies = s.allergies.split(',').map { it.trim() }.filter { it.isNotEmpty() },
                weeklySchedule = s.days,
                budgetLevel = 2
            )
            profileRepository.upsertProfile(profile)
            _state.value = s.copy(saving = false)
            onSuccess()
        }
    }
}
