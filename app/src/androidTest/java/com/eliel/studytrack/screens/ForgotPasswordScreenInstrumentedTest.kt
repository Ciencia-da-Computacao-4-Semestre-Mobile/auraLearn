package com.eliel.studytrack.screens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test

class ForgotPasswordScreenInstrumentedTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun testForgotPasswordScreenDisplaysTitleAndEmailField() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            ForgotPasswordScreen(navController)
        }

        composeTestRule.onNodeWithText("Recuperar Senha").assertExists()
        composeTestRule.onNodeWithText("Digite seu email").assertExists()
    }

    @Test
    fun testSendResetLinkWithEmptyEmailShowsError() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            ForgotPasswordScreen(navController)
        }

        composeTestRule.onNodeWithText("Enviar link de redefinição").performClick()
        composeTestRule.onNodeWithText("Digite seu email").assertExists()
    }

    @Test
    fun testSendResetLinkWithValidEmail() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            ForgotPasswordScreen(navController)
        }

        composeTestRule.onNodeWithText("Digite seu email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Enviar link de redefinição").performClick()
    }

    @Test
    fun testBackToLoginButtonNavigatesBack() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            ForgotPasswordScreen(navController)
        }

        composeTestRule.onNodeWithText("Voltar para o login").performClick()
    }
}