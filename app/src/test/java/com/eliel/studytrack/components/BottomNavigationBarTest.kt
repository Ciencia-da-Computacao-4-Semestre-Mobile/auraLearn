package com.eliel.studytrack.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.eliel.studytrack.MainActivity
import com.eliel.studytrack.Screen
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BottomNavigationBarTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private var navController: NavHostController? = null

    private fun setTestContent() {
        composeRule.setContent {
            MaterialTheme {
                val controller = rememberNavController()
                navController = controller

                NavHost(navController = controller, startDestination = Screen.Home.route) {
                    composable(Screen.Home.route) {}
                    composable(Screen.Schedule.route) {}
                    composable(Screen.Pomodoro.route) {}
                    composable(Screen.Reports.route) {}
                    composable(Screen.Settings.route) {}
                }

                BottomNavigationBar(navController = controller)
            }
        }
    }

    @Test
    fun bottomNav_showsAllItems() {
        setTestContent()

        composeRule.onNodeWithContentDescription("Início").assertExists()
        composeRule.onNodeWithContentDescription("Cronograma").assertExists()
        composeRule.onNodeWithContentDescription("Pomodoro").assertExists()
        composeRule.onNodeWithContentDescription("Relatórios").assertExists()
        composeRule.onNodeWithContentDescription("Configurações").assertExists()
    }

    @Test
    fun clickingOnCronograma_navigatesToScheduleScreen() {
        setTestContent()

        composeRule.onNodeWithContentDescription("Cronograma").performClick()
        assertThat(navController?.currentDestination?.route).isEqualTo(Screen.Schedule.route)
    }

    @Test
    fun clickingOnHome_keepsHomeSelected() {
        setTestContent()

        composeRule.onNodeWithContentDescription("Configurações").performClick()
        composeRule.onNodeWithContentDescription("Início").performClick()
        assertThat(navController?.currentDestination?.route).isEqualTo(Screen.Home.route)
    }

    @Test
    fun clickingOnMultipleItems_navigatesCorrectly() {
        setTestContent()

        composeRule.onNodeWithContentDescription("Pomodoro").performClick()
        assertThat(navController?.currentDestination?.route).isEqualTo(Screen.Pomodoro.route)

        composeRule.onNodeWithContentDescription("Relatórios").performClick()
        assertThat(navController?.currentDestination?.route).isEqualTo(Screen.Reports.route)
    }

    @Test
    fun homeItem_isSelectedWhenClicked() {
        setTestContent()

        composeRule.onNodeWithContentDescription("Início").performClick()
        composeRule.onNodeWithContentDescription("Início").assertIsSelected()
    }

    @Test
    fun settingsItem_isSelectedWhenClicked() {
        setTestContent()

        composeRule.onNodeWithContentDescription("Configurações").performClick()
        composeRule.onNodeWithContentDescription("Configurações").assertIsSelected()
    }
}

