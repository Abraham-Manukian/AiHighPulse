package com.example.aihighpulse.shared.db

import kotlin.Double
import kotlin.Long
import kotlin.String

public data class Profile(
  public val id: String,
  public val age: Long,
  public val sex: String,
  public val heightCm: Long,
  public val weightKg: Double,
  public val goal: String,
)
