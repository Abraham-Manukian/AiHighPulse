package com.example.aihighpulse.shared.db.shared

import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import com.example.aihighpulse.shared.db.AppDatabase
import kotlin.Long
import kotlin.Unit
import kotlin.reflect.KClass

internal val KClass<AppDatabase>.schema: SqlSchema<QueryResult.Value<Unit>>
  get() = AppDatabaseImpl.Schema

internal fun KClass<AppDatabase>.newInstance(driver: SqlDriver): AppDatabase =
    AppDatabaseImpl(driver)

private class AppDatabaseImpl(
  driver: SqlDriver,
) : TransacterImpl(driver), AppDatabase {
  public object Schema : SqlSchema<QueryResult.Value<Unit>> {
    override val version: Long
      get() = 1

    override fun create(driver: SqlDriver): QueryResult.Value<Unit> {
      driver.execute(null, """
          |CREATE TABLE Profile (
          |  id TEXT NOT NULL PRIMARY KEY,
          |  age INTEGER NOT NULL,
          |  sex TEXT NOT NULL,
          |  heightCm INTEGER NOT NULL,
          |  weightKg REAL NOT NULL,
          |  goal TEXT NOT NULL
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE WorkoutLog (
          |  id TEXT NOT NULL PRIMARY KEY,
          |  date TEXT NOT NULL,
          |  payload TEXT NOT NULL -- JSON serialized sets
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE KeyValue (
          |  key TEXT NOT NULL PRIMARY KEY,
          |  value TEXT
          |)
          """.trimMargin(), 0)
      return QueryResult.Unit
    }

    override fun migrate(
      driver: SqlDriver,
      oldVersion: Long,
      newVersion: Long,
      vararg callbacks: AfterVersion,
    ): QueryResult.Value<Unit> = QueryResult.Unit
  }
}
