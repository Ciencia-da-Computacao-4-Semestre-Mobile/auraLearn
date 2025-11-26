package com.eliel.studytrack.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.eliel.studytrack.MainActivity
import com.eliel.studytrack.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenUITest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testUserCardDisplayed() {
        composeTestRule.onNodeWithText("Usuário").assertIsDisplayed()
        composeTestRule.onNodeWithText("email@exemplo.com").assertIsDisplayed()
        composeTestRule.onNodeWithText("Plano: Gratuito").assertIsDisplayed()
    }

    @Test
    fun testPomodoroSettingsDisplayed() {
        composeTestRule.onNodeWithText("Rotina Pomodoro").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tempo de estudo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pausa curta").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pausa longa").assertIsDisplayed()
        composeTestRule.onNodeWithText("Meta diária").assertIsDisplayed()
    }

    @Test
    fun testNotificationSettingsDisplayed() {
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.notificacoes)).assertIsDisplayed()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.lembretes_de_estudo)).assertIsDisplayed()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.prazos_de_tarefas)).assertIsDisplayed()
    }

    @Test
    fun testAppearanceSettingsDisplayed() {
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.aparencia)).assertIsDisplayed()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.modo_escuro)).assertIsDisplayed()
    }

    @Test
    fun testPremiumButtonNavigates() {
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.assine_o_premium)).performClick()
        composeTestRule.onNodeWithText("Premium Screen").assertIsDisplayed()
    }

    @Test
    fun testLogoutButtonNavigatesToLogin() {
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.sair_da_conta)).performClick()
        composeTestRule.onNodeWithText("Login Screen").assertIsDisplayed()
    }
}