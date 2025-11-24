package com.eliel.studytrack.data

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

data class Flashcard(val front: String, val back: String)

sealed interface ReviewUiState {
    object Idle : ReviewUiState
    object Loading : ReviewUiState
    data class Success(val cards: List<Flashcard>) : ReviewUiState
    data class Error(val message: String) : ReviewUiState
}

@OptIn(BetaOpenAI::class)
class ReviewViewModel : ViewModel() {

    private val openAi = OpenAI(OpenAIConfig(token = BuildConfig.OPENAI_KEY))
    private val _uiState = MutableStateFlow<ReviewUiState>(ReviewUiState.Idle)
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    fun generateFlashcards(materia: String, tema: String, dayTask: String) {
        viewModelScope.launch {
            _uiState.value = ReviewUiState.Loading
            try {
                val prompt = buildString {
                    appendLine("Gere exatamente 5 flashcards curtos e objetivos para revisão.")
                    appendLine("Matéria: $materia")
                    appendLine("Tema: $tema")
                    if (dayTask.isNotBlank()) {
                        appendLine("Contexto específico do dia: $dayTask")
                        appendLine("As perguntas devem se basear nessa atividade diária e podem usar o tema apenas como suporte.")
                    } else {
                        appendLine("Sem contexto diário informado; foque no tema fornecido.")
                    }
                    appendLine("Formato: cada linha deve conter 'P: pergunta | R: resposta' sem markdown.")
                }
                val req = ChatCompletionRequest(
                    model = ModelId("gpt-4o-mini"),
                    messages = listOf(ChatMessage(role = ChatRole.User, content = prompt))
                )
                val resp = openAi.chatCompletion(req)
                val raw = resp.choices.firstOrNull()?.message?.content ?: ""
                val sanitized = raw.replace(Regex("[*_#`]+"), "").trim()
                val lines = sanitized.lines().map { it.trim() }.filter { it.isNotEmpty() }
                val cards = mutableListOf<Flashcard>()
                for (line in lines) {
                    val cleaned = line.replace(Regex("^\\d+[.)-]\\s*"), "")
                    val parts = cleaned.split("|").map { it.trim() }
                    if (parts.size >= 2) {
                        val q = parts[0].replace(Regex("^P[:：]?\\s*"), "").trim()
                        val a = parts[1].replace(Regex("^R[:：]?\\s*"), "").trim()
                        cards.add(Flashcard(front = q, back = a))
                    }
                    if (cards.size == 5) break
                }
                if (cards.size < 5) {
                    val fallback = listOf(
                        Flashcard(dayTask.ifBlank { "Defina $tema" }, "Resumo objetivo do que foi estudado."),
                        Flashcard("Exemplo prático do dia", "Relacione com a atividade: $dayTask"),
                        Flashcard("Conceito relacionado", "Explique ligação com o tema $tema"),
                        Flashcard("Erro comum sobre $tema", "Ensine como evitar"),
                        Flashcard("Resumo do dia", "Síntese em uma frase sobre '$dayTask'")
                    )
                    _uiState.value = ReviewUiState.Success(fallback)
                } else {
                    _uiState.value = ReviewUiState.Success(cards)
                }
            } catch (e: Exception) {
                val fallback = listOf(
                    Flashcard(dayTask.ifBlank { "Defina $tema" }, "Resumo objetivo do que foi estudado."),
                    Flashcard("Exemplo prático do dia", "Relacione com a atividade: $dayTask"),
                    Flashcard("Conceito relacionado", "Explique ligação com o tema $tema"),
                    Flashcard("Erro comum sobre $tema", "Ensine como evitar"),
                    Flashcard("Resumo do dia", "Síntese em uma frase sobre '$dayTask'")
                )
                _uiState.value = ReviewUiState.Success(fallback)
            }
        }
    }
}