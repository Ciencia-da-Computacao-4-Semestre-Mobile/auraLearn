package com.eliel.studytrack.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatTutorViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _uiState: MutableStateFlow<ChatTutorUiState> =
        MutableStateFlow(ChatTutorUiState.Initial)
    val uiState: StateFlow<ChatTutorUiState> = _uiState.asStateFlow()

    private val model: GenerativeModel = Firebase.ai()
        .generativeModel("gemini-2.5-flash-lite")

    fun sendMessage(question: String) {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user == null) {
                _uiState.value =
                    ChatTutorUiState.Error("Você precisa estar logado para usar o tutor.")
                return@launch
            }

            _uiState.value = ChatTutorUiState.Loading

            try {
                val fullPrompt = """
                    Você é um tutor paciente e didático que ajuda estudantes a entenderem suas dúvidas de forma simples.
                    Explique de maneira clara e curta.
                    
                    Pergunta do aluno: $question
                """.trimIndent()

                val response = model.generateContent(fullPrompt)
                val rawAnswer = response.text ?: "Não consegui entender, pode reformular?"


                val cleanAnswer = rawAnswer
                    .replace(Regex("\\*\\*|\\*|_|`|#{1,6}\\s*"), "")
                    .trim()

                _uiState.value = ChatTutorUiState.Success(cleanAnswer)

            } catch (e: Exception) {
                _uiState.value = ChatTutorUiState.Error(
                    e.localizedMessage ?: "Erro desconhecido"
                )
            }
        }
    }
}

sealed interface ChatTutorUiState {
    object Initial : ChatTutorUiState
    object Loading : ChatTutorUiState
    data class Success(val answer: String) : ChatTutorUiState
    data class Error(val error: String) : ChatTutorUiState
}
