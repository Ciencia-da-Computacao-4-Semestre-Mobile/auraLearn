package com.eliel.studytrack.screens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavHostController
import com.eliel.studytrack.R
import com.eliel.studytrack.data.firestore.PomodoroRepository
import com.eliel.studytrack.data.firestore.StudyPlanRepository
import com.eliel.studytrack.data.firestore.SubjectRepository
import com.eliel.studytrack.data.firestore.TaskRepository
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ReportsScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var mockNavController: NavHostController

    @Before
    fun setup() {
        mockNavController = mockk(relaxed = true)

        mockkObject(PomodoroRepository)
        mockkObject(TaskRepository)
        mockkObject(SubjectRepository)
        mockkObject(StudyPlanRepository)

        // Define default behavior for repositories (return empty lists)
        coEvery { PomodoroRepository.getSessions() } returns emptyList()
        coEvery { TaskRepository.getTasks() } returns emptyList()
        coEvery { SubjectRepository.getSubjects() } returns emptyList()
        coEvery { StudyPlanRepository.getPlansForCurrentUser() } returns emptyList()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun reportsScreen_displaysTitleCorrectly() {
        composeTestRule.setContent {
            ReportsScreen(navController = mockNavController)
        }

        val expectedTitle = composeTestRule.activity.getString(R.string.relatorios)
        val expectedSubtitle = composeTestRule.activity.getString(R.string.acompanhe_seu_progresso_nos_estudos)

        composeTestRule.onNodeWithText(expectedTitle).assertIsDisplayed()
        composeTestRule.onNodeWithText(expectedSubtitle).assertIsDisplayed()
    }

    @Test
    fun reportsScreen_displaysStatsCardsCorrectly() {
        composeTestRule.setContent {
            ReportsScreenUI(navController = mockNavController)
        }

        val completedTasks = composeTestRule.activity.getString(R.string.tarefas_concluidas)
        val studiedHours = composeTestRule.activity.getString(R.string.horas_estudadas)
        val studySessions = composeTestRule.activity.getString(R.string.sessoes_de_estudo)
        val avgSession = "Média por Sessão" // Hardcoded in UI

        composeTestRule.onNodeWithText(completedTasks).assertIsDisplayed()
        composeTestRule.onNodeWithText(studiedHours).assertIsDisplayed()
        composeTestRule.onNodeWithText(studySessions).assertIsDisplayed()
        composeTestRule.onNodeWithText(avgSession).assertIsDisplayed()
    }

    @Test
    fun reportsScreen_displaysSectionsCorrectly() {
        composeTestRule.setContent {
            ReportsScreen(navController = mockNavController)
        }

        val progressBySubject = composeTestRule.activity.getString(R.string.progresso_por_materia)
        val performanceByDay = composeTestRule.activity.getString(R.string.performance_por_dia_da_semana)

        composeTestRule.onNodeWithText(progressBySubject).assertIsDisplayed()
        composeTestRule.onNodeWithText(performanceByDay).assertIsDisplayed()
    }

    @Test
    fun reportsScreen_displaysDaysOfWeekCorrectly() {
        composeTestRule.setContent {
            ReportsScreen(navController = mockNavController)
        }

        val days = listOf(
            R.string.domingo,
            R.string.segunda,
            R.string.terca,
            R.string.quarta,
            R.string.quinta,
            R.string.sexta,
            R.string.sabado
        )

        days.forEach { resId ->
            val dayName = composeTestRule.activity.getString(resId)
            composeTestRule.onNodeWithText(dayName).assertIsDisplayed()
        }
    }
}
