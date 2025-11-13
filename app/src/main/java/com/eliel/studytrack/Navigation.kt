package com.eliel.studytrack

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.eliel.studytrack.screens.*
import android.app.Activity

@Composable
fun StudyTrackNavHost(
    navController: NavHostController,
    activity: Activity,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = "login", modifier = modifier) {

        composable("login") {
            LoginScreen(navController = navController, activity = activity) //
        }

        composable("register") {
            RegisterScreen(navController = navController, activity = activity) //
        }
        composable("forgot_password") {
            ForgotPasswordScreen(navController)
        }
        composable(Screen.Home.route) { HomeScreenUI(navController) }
        composable(Screen.Schedule.route) { ScheduleScreen(navController) }
        composable(Screen.Pomodoro.route) { PomodoroScreen(navController) }
        composable(Screen.Reports.route) { ReportsScreen(navController) }
        composable(Screen.Settings.route) { SettingsScreen(navController) }
        composable(Screen.ChatTutor.route) { ChatTutorScreen(navController) }
        composable(Screen.Premium.route) { PremiumScreen(navController) }
    }
}
