package com.eliel.studytrack.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test

class SettingsScreenUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun verifyUserInfoIsDisplayed() {
        composeTestRule.setContent {
            SettingsScreenUI(navController = rememberNavController())
        }

        composeTestRule.onNodeWithText("Usuário").assertExists()
        composeTestRule.onNodeWithText("email@exemplo.com").assertExists()
        composeTestRule.onNodeWithText("Plano: Gratuito").assertExists()
    }

    @Test
    fun verifyPomodoroSectionIsDisplayed() {
        composeTestRule.setContent {
            SettingsScreenUI(navController = rememberNavController())
        }

        composeTestRule.onNodeWithText("Rotina Pomodoro").assertExists()
        composeTestRule.onNodeWithText("Personalize seus tempos de estudo").assertExists()
    }

    @Test
    fun verifyNotificationsSectionIsDisplayed() {
        composeTestRule.setContent {
            SettingsScreenUI(navController = rememberNavController())
        }

        composeTestRule.onNodeWithText("Notificações").assertExists()
        composeTestRule.onNodeWithText("Lembretes de estudo").assertExists()
        composeTestRule.onNodeWithText("Prazos de tarefas").assertExists()
    }

    @Test
    fun verifyAppearanceSectionIsDisplayed() {
        composeTestRule.setContent {
            SettingsScreenUI(navController = rememberNavController())
        }

        composeTestRule.onNodeWithText("Aparência").assertExists()
        composeTestRule.onNodeWithText("Modo escuro").assertExists()
        composeTestRule.onNodeWithText("Interface escura para estudos noturnos").assertExists()
    }

    @Test
    fun verifyPremiumButtonIsDisplayed() {
        composeTestRule.setContent {
            SettingsScreenUI(navController = rememberNavController())
        }

        composeTestRule.onNodeWithText("Assine o Premium").assertExists()
    }

    @Test
    fun verifyLogoutButtonIsDisplayed() {
        composeTestRule.setContent {
            SettingsScreenUI(navController = rememberNavController())
        }

        composeTestRule.onNodeWithText("Sair da conta").assertExists()
    }

    @Test
    fun verifyPremiumButtonNavigatesToPremiumScreen() {
        composeTestRule.setContent {
            SettingsScreenUI(navController = rememberNavController())
        }

        composeTestRule.onNodeWithText("Assine o Premium").performClick()
    }

    @Test
    fun verifyLogoutButtonNavigatesToLoginScreen() {
        composeTestRule.setContent {
            SettingsScreenUI(navController = rememberNavController())
        }

        composeTestRule.onNodeWithText("Sair da conta").performClick()
    }
}