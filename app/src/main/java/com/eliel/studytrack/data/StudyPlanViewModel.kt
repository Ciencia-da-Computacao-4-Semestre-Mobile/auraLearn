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
import com.eliel.studytrack.data.firestore.StudyDay
import com.eliel.studytrack.data.firestore.StudyPlan
import com.eliel.studytrack.data.firestore.StudyPlanRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface StudyPlanUiState {
    object Idle : StudyPlanUiState
    object Loading : StudyPlanUiState
    data class SuccessPlans(val plans: List<StudyPlan>) : StudyPlanUiState
    data class Error(val message: String) : StudyPlanUiState
    data class Generating(val message: String = "Gerando plano...") : StudyPlanUiState
}

@OptIn(BetaOpenAI::class)
class StudyPlanViewModel : ViewModel() {

    private val openAi = OpenAI(OpenAIConfig(token = BuildConfig.OPENAI_KEY))
    private val _uiState = MutableStateFlow<StudyPlanUiState>(StudyPlanUiState.Idle)
    val uiState: StateFlow<StudyPlanUiState> = _uiState.asStateFlow()

    init {
        loadPlans()
    }

    fun loadPlans() {
        viewModelScope.launch {
            _uiState.value = StudyPlanUiState.Loading
            try {
                val plans = StudyPlanRepository.getPlansForCurrentUser()
                _uiState.value = StudyPlanUiState.SuccessPlans(plans)
            } catch (e: Exception) {
                _uiState.value = StudyPlanUiState.Error(e.localizedMessage ?: "Erro ao carregar planos")
            }
        }
    }

    fun deletePlan(planId: String) {
        viewModelScope.launch {
            try {
                StudyPlanRepository.deletePlan(planId)
                loadPlans()
            } catch (e: Exception) {
                _uiState.value = StudyPlanUiState.Error(e.localizedMessage ?: "Erro ao excluir")
            }
        }
    }

    fun generateAndSavePlan(
        materia: String,
        tema: String,
        objetivo: String,
        dias: Int,
        horasPorDia: Int,
        onDone: (String?) -> Unit = {}
    ) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            _uiState.value = StudyPlanUiState.Error("Usuário não logado")
            onDone(null)
            return
        }

        viewModelScope.launch {
            _uiState.value = StudyPlanUiState.Generating()
            try {
                val prompt = buildString {
                    appendLine("Você é um assistente que gera um plano de estudos curto e prático.")
                    appendLine("Entrada:")
                    appendLine("Matéria: $materia")
                    appendLine("Tema: $tema")
                    appendLine("Objetivo: $objetivo")
                    appendLine("Dias: $dias")
                    appendLine("Horas por dia: $horasPorDia")
                    appendLine()
                    appendLine("Gere exatamente $dias linhas numeradas (1., 2., ...), cada uma com uma instrução breve para o dia correspondente.")
                    appendLine("Não use markdown ou títulos, retorne apenas as linhas do plano.")
                }

                val req = ChatCompletionRequest(
                    model = ModelId("gpt-4o-mini"),
                    messages = listOf(
                        ChatMessage(role = ChatRole.User, content = prompt)
                    )
                )
                val resp = openAi.chatCompletion(req)
                val raw = resp.choices.firstOrNull()?.message?.content ?: ""
                val sanitized = raw.replace(Regex("[*_#`]+"), "")
                val lines = sanitized.lines().map { it.trim() }.filter { it.isNotEmpty() }

                val daysList = mutableListOf<StudyDay>()
                var idx = 1
                for (line in lines) {
                    val cleaned = line.replace(Regex("^\\s*(?:\\d+[.)-]?|Dia\\s*\\d+[:\\-]?|[-*•])\\s*"), "")
                    daysList.add(StudyDay(dayIndex = idx, text = cleaned, completed = false))
                    idx++
                    if (idx > dias) break
                }
                while (daysList.size < dias) {
                    val next = daysList.size + 1
                    daysList.add(StudyDay(dayIndex = next, text = "Atividade do dia $next: revisar $tema.", completed = false))
                }

                val title = "$materia — $tema (${dias}d)"
                val plan = StudyPlan(
                    id = "",
                    userId = uid,
                    title = title,
                    materia = materia,
                    tema = tema,
                    objetivo = objetivo,
                    days = daysList,
                    totalDays = dias,
                    horasPorDia = horasPorDia
                )

                val savedId = StudyPlanRepository.addPlan(plan)
                loadPlans()
                _uiState.value = StudyPlanUiState.Idle
                onDone(savedId)
            } catch (e: Exception) {
                val fallbackDays = mutableListOf<StudyDay>()
                for (i in 1..dias) {
                    fallbackDays.add(StudyDay(dayIndex = i, text = "Atividade do dia $i: revisar $tema.", completed = false))
                }
                val title = "$materia — $tema (${dias}d)"
                val fallbackPlan = StudyPlan(
                    id = "",
                    userId = uid,
                    title = title,
                    materia = materia,
                    tema = tema,
                    objetivo = objetivo,
                    days = fallbackDays,
                    totalDays = dias,
                    horasPorDia = horasPorDia
                )
                val savedId = try { StudyPlanRepository.addPlan(fallbackPlan) } catch (_: Exception) { null }
                loadPlans()
                _uiState.value = if (savedId != null) StudyPlanUiState.Idle else StudyPlanUiState.Error(e.localizedMessage ?: "Erro ao gerar plano")
                onDone(savedId)
            }
        }
    }

    fun toggleDayCompletion(plan: StudyPlan, dayIndex: Int) {
        viewModelScope.launch {
            try {
                val newDays = plan.days.map {
                    if (it.dayIndex == dayIndex) it.copy(completed = !it.completed) else it
                }
                val updated = plan.copy(days = newDays)
                StudyPlanRepository.updatePlan(updated)
                loadPlans()
            } catch (e: Exception) {
                _uiState.value = StudyPlanUiState.Error(e.localizedMessage ?: "Erro ao atualizar dia")
            }
        }
    }

    fun markPlanCompleted(plan: StudyPlan) {
        viewModelScope.launch {
            try {
                val newDays = plan.days.map { it.copy(completed = true) }
                val updated = plan.copy(days = newDays)
                StudyPlanRepository.updatePlan(updated)
                loadPlans()
            } catch (e: Exception) {
                _uiState.value = StudyPlanUiState.Error(e.localizedMessage ?: "Erro ao concluir plano")
            }
        }
    }
}