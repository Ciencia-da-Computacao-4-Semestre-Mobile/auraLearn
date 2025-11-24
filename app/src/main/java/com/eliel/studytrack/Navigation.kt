package com.eliel.studytrack

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.eliel.studytrack.screens.*
import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.NavType
import androidx.navigation.navArgument



@Composable
fun StudyTrackNavHost(
    navController: NavHostController,
    activity: Activity,
    modifier: Modifier = Modifier
) {
    val prefs = activity.getSharedPreferences("studytrack_prefs", android.content.Context.MODE_PRIVATE)
    val rememberMe = prefs.getBoolean("remember_me", false)
    val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
    val startDest = if (rememberMe && isLoggedIn) Screen.Home.route else "login"

    NavHost(navController = navController, startDestination = startDest, modifier = modifier) {

        composable("login") {
            LoginScreen(navController = navController, activity = activity)
        }

        composable("register") {
            RegisterScreen(navController = navController, activity = activity)
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
        composable(
            route = Screen.Review.route + "/{materia}/{tema}/{dayText}",
            arguments = listOf(
                navArgument("materia") { type = NavType.StringType },
                navArgument("tema") { type = NavType.StringType },
                navArgument("dayText") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val materia = backStackEntry.arguments?.getString("materia") ?: ""
            val tema = backStackEntry.arguments?.getString("tema") ?: ""
            val dayText = backStackEntry.arguments?.getString("dayText") ?: ""
            ReviewScreen(
                navController = navController,
                materia = materia,
                tema = tema,
                dayText = dayText
            )
        }
    }
}
