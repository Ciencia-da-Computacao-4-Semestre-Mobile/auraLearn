package com.eliel.studytrack.screens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
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

class ScheduleScreenUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var mockNavController: NavHostController

    @Before
    fun setup() {
        mockNavController = mockk(relaxed = true)

        // Mock repositories to avoid Firebase calls
        mockkObject(SubjectRepository)
        mockkObject(TaskRepository)
        mockkObject(StudyPlanRepository)

        // Define default behavior
        coEvery { SubjectRepository.getSubjects() } returns emptyList()
        coEvery { TaskRepository.getTasks() } returns emptyList()
        coEvery { StudyPlanRepository.getPlansForCurrentUser() } returns emptyList()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun scheduleScreen_displaysTitleAndTabs() {
        composeTestRule.setContent {
            ScheduleScreenUI(navController = mockNavController)
        }

        // Verify Title
        composeTestRule.onNodeWithText("Cronograma").assertIsDisplayed()
        
        // Verify Tabs
        composeTestRule.onNodeWithText("Tarefas").assertIsDisplayed()
        composeTestRule.onNodeWithText("Matérias").assertIsDisplayed()
        composeTestRule.onNodeWithText("Plano de Estudos").assertIsDisplayed()
    }

    @Test
    fun scheduleScreen_displaysNewTaskButton() {
        composeTestRule.setContent {
            ScheduleScreenUI(navController = mockNavController)
        }

        composeTestRule.onNodeWithText("Nova Tarefa").assertIsDisplayed()
    }

    @Test
    fun scheduleScreen_canSwitchTabs() {
        composeTestRule.setContent {
            ScheduleScreenUI(navController = mockNavController)
        }

        // Click on "Matérias" tab
        composeTestRule.onNodeWithText("Matérias").performClick()
        // Verify it is still displayed
        composeTestRule.onNodeWithText("Matérias").assertIsDisplayed()

        // Click on "Plano de Estudos" tab
        composeTestRule.onNodeWithText("Plano de Estudos").performClick()
        composeTestRule.onNodeWithText("Plano de Estudos").assertIsDisplayed()
    }
}
