package com.eliel.studytrack.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test

class PomodoroScreenUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun verifyPomodoroTimerIsDisplayed() {
        composeTestRule.setContent {
            PomodoroScreenUI(navController = rememberNavController())
        }

        composeTestRule.onNodeWithText("Pomodoro Timer").assertExists()
        composeTestRule.onNodeWithText("Técnica de produtividade para estudos").assertExists()
    }

    @Test
    fun verifyTimerStartsOnClick() {
        composeTestRule.setContent {
            PomodoroScreenUI(navController = rememberNavController())
        }

        composeTestRule.onNodeWithText("Iniciar").performClick()
        composeTestRule.onNodeWithText("Pausar").assertExists()
    }

    @Test
    fun verifyTimerResetsOnClick() {
        composeTestRule.setContent {
            PomodoroScreenUI(navController = rememberNavController())
        }

        composeTestRule.onNodeWithText("Iniciar").performClick()
        composeTestRule.onNodeWithText("Resetar").performClick()
        composeTestRule.onNodeWithText("Iniciar").assertExists()
    }

    @Test
    fun verifySubjectDropdownIsDisplayed() {
        composeTestRule.setContent {
            PomodoroScreenUI(navController = rememberNavController())
        }

        composeTestRule.onNodeWithText("Escolha a matéria para estudar").assertExists()
    }

    @Test
    fun verifyStatsSectionIsDisplayed() {
        composeTestRule.setContent {
            PomodoroScreenUI(navController = rememberNavController())
        }

        composeTestRule.onNodeWithText("Pomodoros concluídos").assertExists()
        composeTestRule.onNodeWithText("Tempo focado hoje").assertExists()
    }

    @Test
    fun verifyTipsSectionIsDisplayed() {
        composeTestRule.setContent {
            PomodoroScreenUI(navController = rememberNavController())
        }

        composeTestRule.onNodeWithText("Dicas para uma sessão produtiva").assertExists()
        composeTestRule.onNodeWithText("Elimine distrações: celular, redes sociais").assertExists()
        composeTestRule.onNodeWithText("Mantenha água por perto").assertExists()
        composeTestRule.onNodeWithText("Nas pausas, levante-se e movimente-se").assertExists()
        composeTestRule.onNodeWithText("A cada 4 pomodoros, faça uma pausa mais longa").assertExists()
    }
}