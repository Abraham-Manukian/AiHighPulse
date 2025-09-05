package com.example.aihighpulse.shared.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.aihighpulse.shared.db.AppDatabase

fun createAndroidDriver(context: Context): SqlDriver =
    AndroidSqliteDriver(AppDatabase.Schema, context, "app.db")

