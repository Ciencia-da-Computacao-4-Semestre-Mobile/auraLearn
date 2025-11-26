package com.eliel.studytrack.screens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import com.eliel.studytrack.auth.AuthViewModel
import org.junit.Rule
import org.junit.Test

class RegisterScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun testEmptyNameShowsErrorMessage() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            RegisterScreen(activity = composeTestRule.activity, navController = navController, viewModel = AuthViewModel())
        }

        composeTestRule.onNodeWithText("Cadastrar").performClick()
        composeTestRule.onNodeWithText("Por favor, insira seu nome").assertExists()
    }

    @Test
    fun testEmptyEmailShowsErrorMessage() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            RegisterScreen(activity = composeTestRule.activity, navController = navController, viewModel = AuthViewModel())
        }

        composeTestRule.onNodeWithText("Nome completo").performTextInput("Test User")
        composeTestRule.onNodeWithText("Cadastrar").performClick()
        composeTestRule.onNodeWithText("Por favor, insira seu email").assertExists()
    }

    @Test
    fun testEmptyPasswordShowsErrorMessage() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            RegisterScreen(activity = composeTestRule.activity, navController = navController, viewModel = AuthViewModel())
        }

        composeTestRule.onNodeWithText("Nome completo").performTextInput("Test User")
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Cadastrar").performClick()
        composeTestRule.onNodeWithText("Por favor, insira sua senha").assertExists()
    }

    @Test
    fun testMismatchedPasswordsShowErrorMessage() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            RegisterScreen(activity = composeTestRule.activity, navController = navController, viewModel = AuthViewModel())
        }

        composeTestRule.onNodeWithText("Nome completo").performTextInput("Test User")
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Senha").performTextInput("password123")
        composeTestRule.onNodeWithText("Confirmar Senha").performTextInput("password321")
        composeTestRule.onNodeWithText("Cadastrar").performClick()
        composeTestRule.onNodeWithText("As senhas não coincidem").assertExists()
    }

    @Test
    fun testShortPasswordShowsErrorMessage() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            RegisterScreen(activity = composeTestRule.activity, navController = navController, viewModel = AuthViewModel())
        }

        composeTestRule.onNodeWithText("Nome completo").performTextInput("Test User")
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Senha").performTextInput("123")
        composeTestRule.onNodeWithText("Confirmar Senha").performTextInput("123")
        composeTestRule.onNodeWithText("Cadastrar").performClick()
        composeTestRule.onNodeWithText("A senha deve ter pelo menos 6 caracteres").assertExists()
    }

    @Test
    fun testGoogleSignInButtonExists() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            RegisterScreen(activity = composeTestRule.activity, navController = navController, viewModel = AuthViewModel())
        }

        composeTestRule.onNodeWithText("Cadastrar com Google").assertExists()
    }

    @Test
    fun testNavigateToLogin() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            RegisterScreen(activity = composeTestRule.activity, navController = navController, viewModel = AuthViewModel())
        }

        composeTestRule.onNodeWithText("Já tem conta? Faça login").performClick()
    }
}