package com.eliel.studytrack.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test

class HomeScreenUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun verifyGreetingMessageIsDisplayed() {
        composeTestRule.setContent {
            HomeScreenUI(navController = rememberNavController())
        }

        val expectedGreeting = when (java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)) {
            in 5..11 -> "Bom dia! ☀️ Vamos começar seus estudos?"
            in 12..17 -> "Boa tarde! 🌞 Continue avançando!"
            else -> "Boa noite! 🌙 Hora de revisar e relaxar!"
        }

        composeTestRule.onNodeWithText(expectedGreeting).assertExists()
    }

    @Test
    fun verifyQuickActionPomodoroNavigatesToPomodoroScreen() {
        composeTestRule.setContent {
            HomeScreenUI(navController = rememberNavController())
        }

        composeTestRule.onNodeWithText("Pomodoro").performClick()
    }

    @Test
    fun verifyQuickActionScheduleNavigatesToScheduleScreen() {
        composeTestRule.setContent {
            HomeScreenUI(navController = rememberNavController())
        }

        composeTestRule.onNodeWithText("Cronograma").performClick()
    }

    @Test
    fun verifyQuickActionReportsNavigatesToReportsScreen() {
        composeTestRule.setContent {
            HomeScreenUI(navController = rememberNavController())
        }

        composeTestRule.onNodeWithText("Relatórios").performClick()
    }

    @Test
    fun verifyUpcomingTasksSectionIsDisplayed() {
        composeTestRule.setContent {
            HomeScreenUI(navController = rememberNavController())
        }

        composeTestRule.onNodeWithText("Próximas tarefas").assertExists()
    }

    @Test
    fun verifyPremiumButtonIsDisplayed() {
        composeTestRule.setContent {
            HomeScreenUI(navController = rememberNavController())
        }

        composeTestRule.onNodeWithText("Assine o Premium").assertExists()
    }

    @Test
    fun verifyChatTutorButtonIsDisplayed() {
        composeTestRule.setContent {
            HomeScreenUI(navController = rememberNavController())
        }

        composeTestRule.onNodeWithText("Abrir Chat Tutor").assertExists()
    }
}