package com.eliel.studytrack.screens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test

class ChatTutorScreenInstrumentedTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun testChatTutorScreenDisplaysTitle() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            ChatTutorScreen(navController)
        }

        composeTestRule.onNodeWithText("Chat Tutor").assertExists()
    }

    @Test
    fun testSendMessage() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            ChatTutorScreen(navController)
        }

        composeTestRule.onNodeWithText("Digite sua pergunta").performTextInput("Olá, Tutor!")
        composeTestRule.onNodeWithText("Enviar").performClick()
        composeTestRule.onNodeWithText("Olá, Tutor!").assertExists()
    }

    @Test
    fun testTypingAnimation() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            ChatTutorScreen(navController)
        }

        composeTestRule.onNodeWithText("Digitando...").assertExists()
    }
}