package com.eliel.studytrack.screens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.rememberNavController
import com.eliel.studytrack.auth.AuthViewModel
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun typingEmailAndPasswordWorks() {
        val vm = AuthViewModel()

        composeRule.setContent {
            val nav = rememberNavController()

            LoginScreen(
                activity = composeRule.activity,
                navController = nav,
                viewModel = vm
            )
        }

        composeRule.onNode(hasText("Email"))
            .performTextInput("teste@teste.com")

        composeRule.onNode(hasText("Senha"))
            .performTextInput("123456")

        composeRule.onNode(hasText("Email"))
            .assertTextContains("teste@teste.com")

        composeRule.onNode(hasText("Senha"))
            .assertExists()
    }

    @Test
    fun loginButtonExists() {
        val vm = AuthViewModel()

        composeRule.setContent {
            val nav = rememberNavController()
            LoginScreen(
                activity = composeRule.activity,
                navController = nav,
                viewModel = vm
            )
        }

        composeRule.onNodeWithText("Entrar")
            .assertExists()
            .assertHasClickAction()
    }

    @Test
    fun googleButtonExists() {
        val vm = AuthViewModel()

        composeRule.setContent {
            val nav = rememberNavController()
            LoginScreen(
                activity = composeRule.activity,
                navController = nav,
                viewModel = vm
            )
        }

        composeRule.onNodeWithText("Entrar com Google")
            .assertExists()
            .assertHasClickAction()
    }
}
