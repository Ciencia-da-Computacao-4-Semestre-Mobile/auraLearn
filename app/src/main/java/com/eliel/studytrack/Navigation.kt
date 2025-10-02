package com.eliel.studytrack

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.eliel.studytrack.screens.*

@Composable
fun StudyTrackNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = "login", modifier = modifier) {


        composable("login") {
            LoginScreen(navController = navController)
        }


        composable("register") {
            RegisterScreen(navController = navController)
        }


        composable(Screen.Home.route) { HomeScreenUI(navController) }
        composable(Screen.Schedule.route) { ScheduleScreen(navController) }
        composable(Screen.Pomodoro.route) { PomodoroScreen(navController) }
        composable(Screen.Reports.route) { ReportsScreen(navController) }
        composable("achievements_list") { AchievementsListScreen(navController = navController) }
        composable(Screen.Settings.route) { SettingsScreen(navController) }
        composable(Screen.ChatTutor.route) { ChatTutorScreen(navController) }
        composable(Screen.Premium.route) { PremiumScreen(navController) }
    }
}
