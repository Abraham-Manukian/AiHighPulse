package com.example.aihighpulse.shared.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.aihighpulse.shared.db.AppDatabase

fun createIosDriver(): SqlDriver =
    NativeSqliteDriver(AppDatabase.Schema, "app.db")

