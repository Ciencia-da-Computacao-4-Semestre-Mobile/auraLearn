package com.eliel.studytrack.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.eliel.studytrack.data.ReviewUiState
import com.eliel.studytrack.data.ReviewViewModel
import org.junit.Rule
import org.junit.Test

class ReviewScreenUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testLoadingState() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            val viewModel = FakeReviewViewModel(ReviewUiState.Loading)
            ReviewScreenUI(navController, "Matemática", "Álgebra", "Hoje")
        }

        composeTestRule.onNodeWithText("Carregando...").assertExists()
    }

    @Test
    fun testErrorState() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            val viewModel = FakeReviewViewModel(ReviewUiState.Error("Erro ao carregar"))
            ReviewScreenUI(navController, "Matemática", "Álgebra", "Hoje")
        }

        composeTestRule.onNodeWithText("Erro ao carregar").assertExists()
    }

    @Test
    fun testSuccessState() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            val viewModel = FakeReviewViewModel(ReviewUiState.Success(listOf()))
            ReviewScreenUI(navController, "Matemática", "Álgebra", "Hoje")
        }

        composeTestRule.onNodeWithText("Revisão").assertExists()
    }

    @Test
    fun testCloseButton() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            ReviewScreenUI(navController, "Matemática", "Álgebra", "Hoje")
        }

        composeTestRule.onNodeWithText("Fechar").performClick()
    }
}

class FakeReviewViewModel(initialState: ReviewUiState) : ReviewViewModel() {
    init {
        uiState.value = initialState
    }
}