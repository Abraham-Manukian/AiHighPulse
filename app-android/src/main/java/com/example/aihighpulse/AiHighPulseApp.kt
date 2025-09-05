package com.example.aihighpulse

import android.app.Application
import com.example.aihighpulse.shared.data.di.DI
import com.example.aihighpulse.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AiHighPulseApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AiHighPulseApp)
            modules(DI.coreModule(apiBaseUrl = "https://api.example.com"), AppModule.module)
        }
    }
}
