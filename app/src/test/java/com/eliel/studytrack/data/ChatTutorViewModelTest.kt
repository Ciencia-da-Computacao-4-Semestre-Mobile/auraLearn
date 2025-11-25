package com.eliel.studytrack.data

import com.aallam.openai.api.chat.ChatChoice
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.client.OpenAI
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatTutorViewModelTest {

    private lateinit var mockClient: OpenAI
    private lateinit var viewModel: ChatTutorViewModel

    @Before
    fun setup() {
        mockClient = mockk()
        viewModel = object : ChatTutorViewModel() {
            override val client: OpenAI = mockClient
        }
    }

    @Test
    fun `quando sendMessage retorna resposta, uiState deve ser Success`() = runTest {
        // Arrange: simula resposta da API
        val fakeMessage = ChatMessage(role = ChatRole.Assistant, content = "Olá aluno!")
        val fakeChoice = ChatChoice(index = 0, message = fakeMessage, finishReason = null)
        val fakeResponse = ChatCompletion(id = "1", choices = listOf(fakeChoice), usage = null)

        coEvery { mockClient.chatCompletion(any()) } returns fakeResponse

        // Act
        viewModel.sendMessage("Qual é a capital da França?")

        // Assert
        val state = viewModel.uiState.value
        assertEquals(ChatTutorUiState.Success("Olá aluno!"), state)
    }

    @Test
    fun `quando sendMessage lança exceção, uiState deve ser Error`() = runTest {
        // Arrange: simula erro da API
        coEvery { mockClient.chatCompletion(any()) } throws RuntimeException("Falha na API")

        // Act
        viewModel.sendMessage("Qual é a capital da França?")

        // Assert
        val state = viewModel.uiState.value
        assert(state is ChatTutorUiState.Error)
        assertEquals("Falha na API", (state as ChatTutorUiState.Error).error)
    }

    @Test
    fun `quando resposta vem vazia, uiState deve ser Success com fallback`() = runTest {
        // Arrange: resposta sem conteúdo
        val fakeChoice = ChatChoice(index = 0, message = null, finishReason = null)
        val fakeResponse = ChatCompletion(id = "2", choices = listOf(fakeChoice), usage = null)

        coEvery { mockClient.chatCompletion(any()) } returns fakeResponse

        // Act
        viewModel.sendMessage("Teste vazio")

        // Assert
        val state = viewModel.uiState.value
        assertEquals(ChatTutorUiState.Success("Não consegui responder."), state)
    }
}
