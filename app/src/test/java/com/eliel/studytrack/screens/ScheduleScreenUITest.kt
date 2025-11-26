package com.eliel.studytrack.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test

class ScheduleScreenUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testInitialTabIsTasks() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            ScheduleScreenUI(navController)
        }

        composeTestRule.onNodeWithText("Tarefas").assertExists()
    }

    @Test
    fun testSwitchToSubjectsTab() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            ScheduleScreenUI(navController)
        }

        composeTestRule.onNodeWithText("Matérias").performClick()
        composeTestRule.onNodeWithText("Nova Matéria").assertExists()
    }

    @Test
    fun testSwitchToStudyPlanTab() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            ScheduleScreenUI(navController)
        }

        composeTestRule.onNodeWithText("Plano de Estudos").performClick()
        composeTestRule.onNodeWithText("Planos de Estudo").assertExists()
    }

    @Test
    fun testAddNewTaskDialog() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            ScheduleScreenUI(navController)
        }

        composeTestRule.onNodeWithText("Nova Tarefa").performClick()
        composeTestRule.onNodeWithText("Nova Tarefa").assertExists()
    }

    @Test
    fun testAddNewSubjectDialog() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            ScheduleScreenUI(navController)
        }

        composeTestRule.onNodeWithText("Matérias").performClick()
        composeTestRule.onNodeWithText("Nova Matéria").performClick()
        composeTestRule.onNodeWithText("Nova Matéria").assertExists()
    }

    @Test
    fun testTaskFilterDropdown() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            ScheduleScreenUI(navController)
        }

        composeTestRule.onNodeWithText("Filtrar por").performClick()
        composeTestRule.onNodeWithText("Todas").assertExists()
    }

    @Test
    fun testDeleteTaskDialog() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            ScheduleScreenUI(navController)
        }

        composeTestRule.onNodeWithText("Tarefas").performClick()
        composeTestRule.onNodeWithText("Excluir Tarefa").performClick()
        composeTestRule.onNodeWithText("Tem certeza que deseja excluir esta tarefa?").assertExists()
    }

    @Test
    fun testDeleteSubjectDialog() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            ScheduleScreenUI(navController)
        }

        composeTestRule.onNodeWithText("Matérias").performClick()
        composeTestRule.onNodeWithText("Excluir Matéria").performClick()
        composeTestRule.onNodeWithText("Tem certeza que deseja excluir esta matéria?").assertExists()
    }

    @Test
    fun testStudyPlanDeleteDialog() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            ScheduleScreenUI(navController)
        }

        composeTestRule.onNodeWithText("Plano de Estudos").performClick()
        composeTestRule.onNodeWithText("Excluir Plano").performClick()
        composeTestRule.onNodeWithText("Tem certeza que deseja excluir este plano de estudos?").assertExists()
    }
}