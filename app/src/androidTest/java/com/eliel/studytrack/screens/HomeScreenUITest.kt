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
class HomeScreenUITest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testGreetingMessageDisplayed() {
        val greetingMessages = listOf(
            "Bom dia! ☀️ Vamos começar seus estudos?",
            "Boa tarde! 🌞 Continue avançando!",
            "Boa noite! 🌙 Hora de revisar e relaxar!"
        )
        greetingMessages.forEach { message ->
            composeTestRule.onNodeWithText(message).assertIsDisplayed()
        }
    }

    @Test
    fun testCurrentDateDisplayed() {
        val currentDate = "Quarta-feira, 26 de Novembro"
        composeTestRule.onNodeWithText(currentDate).assertIsDisplayed()
    }

    @Test
    fun testQuickActionsDisplayed() {
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.pomodoro)).assertIsDisplayed()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.cronograma)).assertIsDisplayed()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.relatorios)).assertIsDisplayed()
    }

    @Test
    fun testQuickActionsNavigation() {
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.pomodoro)).performClick()
        composeTestRule.onNodeWithText("Pomodoro Screen").assertIsDisplayed()

        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.cronograma)).performClick()
        composeTestRule.onNodeWithText("Schedule Screen").assertIsDisplayed()

        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.relatorios)).performClick()
        composeTestRule.onNodeWithText("Reports Screen").assertIsDisplayed()
    }

    @Test
    fun testUpcomingTasksDisplayed() {
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.proximas_tarefas)).assertIsDisplayed()
        composeTestRule.onAllNodesWithTag("UpcomingTaskCard").assertCountEquals(3)
    }

    @Test
    fun testOpenChatTutor() {
        composeTestRule.onNodeWithText("Abrir Chat Tutor").performClick()
        composeTestRule.onNodeWithText("Chat Tutor Screen").assertIsDisplayed()
    }

    @Test
    fun testOpenPremium() {
        composeTestRule.onNodeWithText("Assine o Premium").performClick()
        composeTestRule.onNodeWithText("Premium Screen").assertIsDisplayed()
    }
}