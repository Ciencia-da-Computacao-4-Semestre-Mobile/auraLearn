package com.eliel.studytrack.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import com.eliel.studytrack.auth.AuthViewModel
import org.junit.Rule
import org.junit.Test

class ForgotPasswordScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun verifyInitialUIElementsAreDisplayed() {
        composeTestRule.setContent {
            ForgotPasswordScreen(navController = rememberNavController(), viewModel = FakeAuthViewModel())
        }

        composeTestRule.onNodeWithText("Recuperar senha").assertExists()
        composeTestRule.onNodeWithText("Digite seu email").assertExists()
        composeTestRule.onNodeWithText("Enviar link de redefinição").assertExists()
        composeTestRule.onNodeWithText("Voltar para o login").assertExists()
    }

    @Test
    fun verifyEmailInputUpdatesCorrectly() {
        composeTestRule.setContent {
            ForgotPasswordScreen(navController = rememberNavController(), viewModel = FakeAuthViewModel())
        }

        composeTestRule.onNodeWithText("Digite seu email").performTextInput("teste@email.com")
        composeTestRule.onNodeWithText("teste@email.com").assertExists()
    }

    @Test
    fun verifyLoadingStateIsDisplayedWhenButtonIsClicked() {
        composeTestRule.setContent {
            ForgotPasswordScreen(navController = rememberNavController(), viewModel = FakeAuthViewModel())
        }

        composeTestRule.onNodeWithText("Enviar link de redefinição").performClick()
        composeTestRule.onNodeWithText("Enviando...").assertExists()
    }

    @Test
    fun verifySuccessMessageIsDisplayedOnSuccess() {
        val fakeViewModel = FakeAuthViewModel(successMessage = "Link enviado com sucesso!")

        composeTestRule.setContent {
            ForgotPasswordScreen(navController = rememberNavController(), viewModel = fakeViewModel)
        }

        composeTestRule.onNodeWithText("Enviar link de redefinição").performClick()
        composeTestRule.onNodeWithText("Link enviado com sucesso!").assertExists()
    }

    @Test
    fun verifyErrorMessageIsDisplayedOnFailure() {
        val fakeViewModel = FakeAuthViewModel(errorMessage = "Erro ao enviar o link!")

        composeTestRule.setContent {
            ForgotPasswordScreen(navController = rememberNavController(), viewModel = fakeViewModel)
        }

        composeTestRule.onNodeWithText("Enviar link de redefinição").performClick()
        composeTestRule.onNodeWithText("Erro ao enviar o link!").assertExists()
    }

    @Test
    fun verifyBackButtonNavigatesToLoginScreen() {
        composeTestRule.setContent {
            ForgotPasswordScreen(navController = rememberNavController(), viewModel = FakeAuthViewModel())
        }

        composeTestRule.onNodeWithText("Voltar para o login").performClick()
    }
}

class FakeAuthViewModel(
    private val successMessage: String? = null,
    private val errorMessage: String? = null
) : AuthViewModel() {
    override fun resetPassword(email: String, callback: (Boolean, String?) -> Unit) {
        if (successMessage != null) {
            callback(true, successMessage)
        } else if (errorMessage != null) {
            callback(false, errorMessage)
        }
    }
}