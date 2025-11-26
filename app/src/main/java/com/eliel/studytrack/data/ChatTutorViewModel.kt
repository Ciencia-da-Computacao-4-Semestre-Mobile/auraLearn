package com.eliel.studytrack.data

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.eliel.studytrack.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@OptIn(BetaOpenAI::class)
open class ChatTutorViewModel : ViewModel() {

    private val client = OpenAI(
        OpenAIConfig(
            token = BuildConfig.OPENAI_KEY
        )
    )

    private val _uiState = MutableStateFlow<ChatTutorUiState>(ChatTutorUiState.Initial)
    open val uiState: MutableState<ChatTutorUiState> = _uiState.asStateFlow()

    open fun String.sendMessage() {
        viewModelScope.launch {

            _uiState.value = ChatTutorUiState.Loading

            try {
                val fullPrompt = """
                    Você é um tutor educacional paciente e claro.
                    Explique sempre de forma simples, objetiva e sem formatação (*, **, #).
                    Responda como se estivesse falando com um aluno.
                    
                    Pergunta: ${this@sendMessage}
                """.trimIndent()

                val request = ChatCompletionRequest(
                    model = ModelId("gpt-4.1-mini"),
                    messages = listOf(
                        ChatMessage(
                            role = ChatRole.User,
                            content = fullPrompt
                        )
                    )
                )

                val response = client.chatCompletion(request)
                val raw = response.choices.firstOrNull()?.message?.content
                    ?: "Não consegui responder."

                val cleanAnswer = raw
                    .replace(Regex("[*_#`]+"), "")
                    .trim()

                _uiState.value = ChatTutorUiState.Success(cleanAnswer)

            } catch (e: Exception) {
                _uiState.value =
                    ChatTutorUiState.Error(e.localizedMessage ?: "Erro inesperado")
            }
        }
    }

    open fun sendMessage(message: String) {}
}

sealed interface ChatTutorUiState {
    object Initial : ChatTutorUiState
    object Loading : ChatTutorUiState
    data class Success(val answer: String) : ChatTutorUiState
    data class Error(val error: String) : ChatTutorUiState
    companion object {
        val Idle: Any
    }
}
