package com.mirror.app.ui.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object CheckIn : Screen("checkin/{date}") {
        fun createRoute(date: String) = "checkin/$date"
    }
    object History : Screen("history")
    object Settings : Screen("settings")
}
