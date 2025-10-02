package com.eliel.studytrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.eliel.studytrack.components.BottomNavigationBar
import com.eliel.studytrack.ui.theme.StudyTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudyTrackTheme {
                val navController = rememberNavController()


                Scaffold(
                    bottomBar = { BottomBarConditional(navController) }
                ) { paddingValues ->
                    StudyTrackNavHost(
                        navController = navController,
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
