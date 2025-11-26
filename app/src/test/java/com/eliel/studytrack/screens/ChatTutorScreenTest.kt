package com.eliel.studytrack.screens

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavHostController
import androidx.test.core.app.ApplicationProvider
import com.eliel.studytrack.data.ChatTutorUiState
import com.eliel.studytrack.data.ChatTutorViewModel
import org.junit.Rule
import org.junit.Test

class ChatTutorScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testInitialState() {
        composeTestRule.setContent {
            ChatTutorScreen(
                navController = FakeNavController(),
                chatViewModel = FakeChatTutorViewModel(ChatTutorUiState.Success(""))
            )
        }

        composeTestRule.onNodeWithText("Como posso te ajudar?").assertExists()
    }

    @Test
    fun testSendMessage() {
        composeTestRule.setContent {
            ChatTutorScreen(
                navController = FakeNavController(),
                chatViewModel = FakeChatTutorViewModel()
            )
        }

        val inputMessage = "Hello, Tutor!"

        composeTestRule.onNodeWithText("Digite sua pergunta").performTextInput(inputMessage)
        composeTestRule.onNodeWithContentDescription("Enviar").performClick()

        composeTestRule.onNodeWithText(inputMessage).assertExists()
    }

    @Test
    fun testLoadingState() {
        composeTestRule.setContent {
            ChatTutorScreen(
                navController = FakeNavController(),
                chatViewModel = FakeChatTutorViewModel(ChatTutorUiState.Loading)
            )
        }

        composeTestRule.onNodeWithText("Digitando", substring = true).assertExists()
    }

    @Test
    fun testErrorState() {
        composeTestRule.setContent {
            ChatTutorScreen(
                navController = FakeNavController(),
                chatViewModel = FakeChatTutorViewModel(ChatTutorUiState.Error("Network Error"))
            )
        }

        composeTestRule.onNodeWithText("Erro: Network Error").assertExists()
    }

    @Test
    fun testSuccessState() {
        composeTestRule.setContent {
            ChatTutorScreen(
                navController = FakeNavController(),
                chatViewModel = FakeChatTutorViewModel(ChatTutorUiState.Success("Hello, User!"))
            )
        }

        composeTestRule.onNodeWithText("Hello, User!").assertExists()
    }
}


class FakeNavController : NavHostController(
    ApplicationProvider.getApplicationContext()
) {
    override fun popBackStack(): Boolean = true
}

class FakeChatTutorViewModel(
    initialState: ChatTutorUiState = ChatTutorUiState.Success("")
) : ChatTutorViewModel() {

    override val uiState = mutableStateOf(initialState)

}
