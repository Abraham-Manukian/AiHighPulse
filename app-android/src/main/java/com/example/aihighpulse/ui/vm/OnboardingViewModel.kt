package com.example.aihighpulse.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aihighpulse.shared.domain.model.Constraints
import com.example.aihighpulse.shared.domain.model.Equipment
import com.example.aihighpulse.shared.domain.model.Goal
import com.example.aihighpulse.shared.domain.model.Profile
import com.example.aihighpulse.shared.domain.model.Sex
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.aihighpulse.shared.domain.repository.PreferencesRepository
import com.example.aihighpulse.shared.domain.repository.ProfileRepository
import com.example.aihighpulse.shared.domain.usecase.BootstrapCoachData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private val DEFAULT_EQUIPMENT = setOf("���⥫�", "��୨�")

data class OnboardingState(
    val age: String = "28",
    val sex: Sex = Sex.MALE,
    val heightCm: String = "178",
    val weightKg: String = "78",
    val goal: Goal = Goal.MAINTAIN,
    val experienceLevel: Int = 3,
    val dietaryPreferences: String = "",
    val allergies: String = "",
    val selectedEquipment: Set<String> = DEFAULT_EQUIPMENT,
    val customEquipment: String = "",
    val days: Map<String, Boolean> = mapOf(
        "Mon" to true, "Tue" to true, "Wed" to false, "Thu" to true, "Fri" to false, "Sat" to false, "Sun" to false
    ),
    val languageTag: String = "system",
    val saving: Boolean = false,
    val error: String? = null
)

class OnboardingViewModel(
    private val profileRepository: ProfileRepository,
    private val bootstrapCoachData: BootstrapCoachData,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    init {
        val tag = preferencesRepository.getLanguageTag() ?: "system"
        _state.value = _state.value.copy(languageTag = tag)
    }

    fun update(block: (OnboardingState) -> OnboardingState) { _state.value = block(_state.value) }

    fun toggleEquipment(option: String) {
        update { st ->
            val newSet = if (st.selectedEquipment.contains(option)) st.selectedEquipment - option else st.selectedEquipment + option
            st.copy(selectedEquipment = newSet)
        }
    }

    fun setCustomEquipment(value: String) {
        update { it.copy(customEquipment = value.take(200)) }
    }

    fun setLanguage(tag: String) {
        update { it.copy(languageTag = tag) }
        if (tag == "system") {
            preferencesRepository.setLanguageTag(null)
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
        } else {
            preferencesRepository.setLanguageTag(tag)
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
        }
    }

    fun save(onSuccess: () -> Unit) {
        val s = _state.value
        val age = s.age.toIntOrNull()
        val h = s.heightCm.toIntOrNull()
        val w = s.weightKg.toDoubleOrNull()
        if (age == null || h == null || w == null) {
            _state.value = s.copy(error = "invalid")
            return
        }
        viewModelScope.launch {
            _state.value = s.copy(saving = true, error = null)
            val manualEquipment = s.customEquipment.split(',', ';', '\n').map { it.trim() }.filter { it.isNotEmpty() }
            val allEquipment = (s.selectedEquipment + manualEquipment).map { it.trim() }.filter { it.isNotEmpty() }
            val profile = Profile(
                id = "local",
                age = age,
                sex = s.sex,
                heightCm = h,
                weightKg = w,
                goal = s.goal,
                experienceLevel = s.experienceLevel,
                constraints = Constraints(),
                equipment = Equipment(items = allEquipment),
                dietaryPreferences = s.dietaryPreferences.split(',',';','\n').map { it.trim() }.filter { it.isNotEmpty() },
                allergies = s.allergies.split(',',';','\n').map { it.trim() }.filter { it.isNotEmpty() },
                weeklySchedule = s.days,
                budgetLevel = 2
            )
            // ensure language preference persisted even if user didn't tap chips again
            setLanguage(s.languageTag)
            profileRepository.upsertProfile(profile)
            runCatching { bootstrapCoachData() }.onFailure { it.printStackTrace() }
            _state.value = s.copy(saving = false)
            onSuccess()
        }
    }
}
