package com.eliel.studytrack

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.eliel.studytrack.components.BottomNavigationBar
import com.eliel.studytrack.ui.theme.StudyTrackTheme
import com.eliel.studytrack.ui.theme.ThemeController
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }

            ThemeController.toggleTheme = { isDarkTheme = !isDarkTheme }
            ThemeController.isDarkMode = { isDarkTheme }

            StudyTrackTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                val activity: Activity = this@MainActivity

                Scaffold(
                    bottomBar = { BottomBarConditional(navController) }
                ) { paddingValues ->
                    StudyTrackNavHost(
                        navController = navController,
                        activity = activity,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}

@Composable
fun BottomBarConditional(navController: androidx.navigation.NavHostController) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    if (currentDestination?.route in listOf(
            Screen.Home.route,
            Screen.Schedule.route,
            Screen.Pomodoro.route,
            Screen.Reports.route,
            Screen.Settings.route
        )
    ) {
        BottomNavigationBar(navController)
    }
}
