package com.example.aihighpulse.ui.navigation

object Routes {
    const val Onboarding = "onboarding"
    const val Home = "home"
    const val Workout = "workout"
    const val Nutrition = "nutrition"
    const val NutritionDetail = "nutrition_detail/{day}/{index}"
    const val ShoppingList = "shopping_list"
    const val Sleep = "sleep"
    const val Progress = "progress"
    const val Paywall = "paywall"
    const val Settings = "settings"
}

fun Routes.nutritionDetail(day: String, index: Int): String =
    "nutrition_detail/$day/$index"
