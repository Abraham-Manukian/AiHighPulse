package com.example.aihighpulse.ios

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.aihighpulse.shared.data.di.DI
import com.example.aihighpulse.shared.data.di.KoinProvider
import com.example.aihighpulse.shared.db.AppDatabase
import org.koin.core.context.startKoin
import org.koin.dsl.module
import platform.Foundation.NSBundle
import platform.Foundation.NSProcessInfo

private const val DEFAULT_API_BASE_URL = "http://localhost:8081"

fun initKoinIfNeeded(apiBaseUrl: String = resolveApiBaseUrl()) {
    if (KoinProvider.koin != null) return

    val iosModule = module {
        single<SqlDriver> { NativeSqliteDriver(AppDatabase.Schema, "app_v2.db") }
        single { AppDatabase(get()) }
    }

    val koinApp = startKoin {
        modules(
            DI.coreModule(apiBaseUrl = apiBaseUrl),
            iosModule
        )
    }
    KoinProvider.koin = koinApp.koin
}

private fun resolveApiBaseUrl(): String {
    val environment = NSProcessInfo.processInfo.environment
    val env = (environment["AIHIGHPULSE_API_BASE_URL"] ?: environment["API_BASE_URL"])
        ?.toString()
        ?.trim()
        ?.takeIf { it.isNotEmpty() }

    val plist = NSBundle.mainBundle
        .objectForInfoDictionaryKey("API_BASE_URL")
        ?.toString()
        ?.trim()
        ?.takeIf { it.isNotEmpty() }

    return env ?: plist ?: DEFAULT_API_BASE_URL
}
