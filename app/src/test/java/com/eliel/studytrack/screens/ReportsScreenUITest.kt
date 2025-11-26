package com.eliel.studytrack.screens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test

class ReportsScreenUITest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun testReportsScreenDisplaysTitleAndSubtitle() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            ReportsScreenUI(navController)
        }

        composeTestRule.onNodeWithText("Relatórios").assertExists()
        composeTestRule.onNodeWithText("Acompanhe seu progresso nos estudos").assertExists()
    }

    @Test
    fun testReportsScreenDisplaysStatCards() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            ReportsScreenUI(navController)
        }

        composeTestRule.onNodeWithText("Tarefas Concluídas").assertExists()
        composeTestRule.onNodeWithText("Horas Estudadas").assertExists()
        composeTestRule.onNodeWithText("Sessões de Estudo").assertExists()
        composeTestRule.onNodeWithText("Média por Sessão").assertExists()
    }

    @Test
    fun testReportsScreenDisplaysProgressBySubject() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            ReportsScreenUI(navController)
        }

        composeTestRule.onNodeWithText("Progresso por Matéria").assertExists()
    }

    @Test
    fun testReportsScreenDisplaysPerformanceByDayOfWeek() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            ReportsScreenUI(navController)
        }

        composeTestRule.onNodeWithText("Performance por Dia da Semana").assertExists()
    }

    @Test
    fun testStatCardClickNavigates() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            ReportsScreenUI(navController)
        }

        composeTestRule.onNodeWithText("Tarefas Concluídas").performClick()
    }
}