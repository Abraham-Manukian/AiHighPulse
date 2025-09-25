package com.example.aihighpulse

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.aihighpulse.di.AppModule
import com.example.aihighpulse.shared.data.di.DI
import com.example.aihighpulse.shared.domain.repository.PreferencesRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

class AiHighPulseApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val apiBaseUrl = BuildConfig.API_BASE_URL
        Log.i(TAG, "Using API base URL (${BuildConfig.BUILD_TYPE}): $apiBaseUrl")
        startKoin {
            androidContext(this@AiHighPulseApp)
            modules(DI.coreModule(apiBaseUrl = apiBaseUrl), AppModule.module)
        }
        // Apply persisted app preferences (language/theme)
        val koin = GlobalContext.get()
        val prefs = koin.get<PreferencesRepository>()
        val lang = prefs.getLanguageTag()
        if (lang != null) {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(lang))
        }
        when (prefs.getTheme()) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    companion object {
        private const val TAG = "AiHighPulseApp"
    }
}

