package com.example.aihighpulse.shared.domain.usecase

import com.example.aihighpulse.shared.domain.model.Profile
import com.example.aihighpulse.shared.domain.repository.ChatMessage
import com.example.aihighpulse.shared.domain.repository.ChatRepository
import com.example.aihighpulse.shared.domain.repository.PreferencesRepository
import com.example.aihighpulse.shared.domain.repository.ProfileRepository

class AskAiTrainer(
    private val profileRepository: ProfileRepository,
    private val chatRepository: ChatRepository,
    private val preferencesRepository: PreferencesRepository
) {
    suspend operator fun invoke(
        history: List<ChatMessage>,
        userMessage: String,
        localeOverride: String? = null
    ): String {
        val profile: Profile = profileRepository.getProfile() ?: error("Profile required")
        val locale = localeOverride ?: preferencesRepository.getLanguageTag()
        return chatRepository.send(profile, history, userMessage, locale)
    }
}
