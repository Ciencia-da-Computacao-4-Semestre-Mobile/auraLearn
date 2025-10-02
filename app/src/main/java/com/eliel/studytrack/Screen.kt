package com.eliel.studytrack

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Schedule : Screen("schedule")
    object Pomodoro : Screen("pomodoro")
    object Reports : Screen("reports")
    object Settings : Screen("settings")
    object ChatTutor : Screen("chat_tutor")
    object Premium : Screen("premium")
}
