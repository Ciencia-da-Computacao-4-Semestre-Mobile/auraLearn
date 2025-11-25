package com.eliel.studytrack.screens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import com.eliel.studytrack.auth.AuthViewModel
import com.eliel.studytrack.auth.GoogleAuthHelper
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@Suppress("DEPRECATION")
class RegisterScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var mockViewModel: AuthViewModel
    private lateinit var mockNavController: NavHostController
    private lateinit var mockGoogleClient: GoogleSignInClient

    @Before
    fun setup() {
        mockViewModel = mockk(relaxed = true)
        mockNavController = mockk(relaxed = true)
        mockGoogleClient = mockk(relaxed = true)

        // Mock GoogleAuthHelper para evitar erros de inicialização do Google Sign In
        mockkObject(GoogleAuthHelper)
        every { GoogleAuthHelper.getClient(any()) } returns mockGoogleClient
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun showErrorMessage_whenNameIsEmpty() {
        composeTestRule.setContent {
            RegisterScreen(
                activity = composeTestRule.activity,
                navController = mockNavController,
                viewModel = mockViewModel
            )
        }

        composeTestRule.onNodeWithText("Cadastrar").performClick()
        composeTestRule.onNodeWithText("Por favor, insira seu nome").assertIsDisplayed()
    }

    @Test
    fun showErrorMessage_whenEmailIsEmpty() {
        composeTestRule.setContent {
            RegisterScreen(
                activity = composeTestRule.activity,
                navController = mockNavController,
                viewModel = mockViewModel
            )
        }

        composeTestRule.onNodeWithText("Nome completo").performTextInput("Usuario Teste")
        composeTestRule.onNodeWithText("Cadastrar").performClick()
        
        composeTestRule.onNodeWithText("Por favor, insira seu email").assertIsDisplayed()
    }

    @Test
    fun showErrorMessage_whenPasswordIsEmpty() {
        composeTestRule.setContent {
            RegisterScreen(
                activity = composeTestRule.activity,
                navController = mockNavController,
                viewModel = mockViewModel
            )
        }

        composeTestRule.onNodeWithText("Nome completo").performTextInput("Usuario Teste")
        composeTestRule.onNodeWithText("Email").performTextInput("teste@email.com")
        
        composeTestRule.onNodeWithText("Cadastrar").performClick()

        composeTestRule.onNodeWithText("Por favor, insira sua senha").assertIsDisplayed()
    }

    @Test
    fun showErrorMessage_whenPasswordsDoNotMatch() {
        composeTestRule.setContent {
            RegisterScreen(
                activity = composeTestRule.activity,
                navController = mockNavController,
                viewModel = mockViewModel
            )
        }

        composeTestRule.onNodeWithText("Nome completo").performTextInput("Usuario Teste")
        composeTestRule.onNodeWithText("Email").performTextInput("teste@email.com")
        composeTestRule.onNodeWithText("Senha").performTextInput("123456")
        composeTestRule.onNodeWithText("Confirmar Senha").performTextInput("654321")

        composeTestRule.onNodeWithText("Cadastrar").performClick()

        composeTestRule.onNodeWithText("As senhas não coincidem").assertIsDisplayed()
    }

    @Test
    fun showErrorMessage_whenPasswordIsTooShort() {
        composeTestRule.setContent {
            RegisterScreen(
                activity = composeTestRule.activity,
                navController = mockNavController,
                viewModel = mockViewModel
            )
        }

        composeTestRule.onNodeWithText("Nome completo").performTextInput("Usuario Teste")
        composeTestRule.onNodeWithText("Email").performTextInput("teste@email.com")
        composeTestRule.onNodeWithText("Senha").performTextInput("123")
        composeTestRule.onNodeWithText("Confirmar Senha").performTextInput("123")

        composeTestRule.onNodeWithText("Cadastrar").performClick()

        composeTestRule.onNodeWithText("A senha deve ter pelo menos 6 caracteres").assertIsDisplayed()
    }

    @Test
    fun callRegisterUser_whenDataIsValid() {
        composeTestRule.setContent {
            RegisterScreen(
                activity = composeTestRule.activity,
                navController = mockNavController,
                viewModel = mockViewModel
            )
        }

        composeTestRule.onNodeWithText("Nome completo").performTextInput("Usuario Teste")
        composeTestRule.onNodeWithText("Email").performTextInput("teste@email.com")
        composeTestRule.onNodeWithText("Senha").performTextInput("123456")
        composeTestRule.onNodeWithText("Confirmar Senha").performTextInput("123456")

        composeTestRule.onNodeWithText("Cadastrar").performClick()

        verify {
            mockViewModel.registerUser(
                email = "teste@email.com",
                password = "123456",
                name = "Usuario Teste",
                onResult = any()
            )
        }
    }

    @Test
    fun navigateToHome_whenRegisterSucceeds() {
        // Configurar o mock para chamar o callback com sucesso
        val callbackSlot = slot<(Boolean, String?) -> Unit>()
        every {
            mockViewModel.registerUser(any(), any(), any(), capture(callbackSlot))
        } answers {
            callbackSlot.captured.invoke(true, null)
        }

        composeTestRule.setContent {
            RegisterScreen(
                activity = composeTestRule.activity,
                navController = mockNavController,
                viewModel = mockViewModel
            )
        }

        composeTestRule.onNodeWithText("Nome completo").performTextInput("Usuario Teste")
        composeTestRule.onNodeWithText("Email").performTextInput("teste@email.com")
        composeTestRule.onNodeWithText("Senha").performTextInput("123456")
        composeTestRule.onNodeWithText("Confirmar Senha").performTextInput("123456")

        composeTestRule.onNodeWithText("Cadastrar").performClick()

        verify {
            mockNavController.navigate(
                route = "home",
                builder = any<NavOptionsBuilder.() -> Unit>()
            )
        }
    }
}
