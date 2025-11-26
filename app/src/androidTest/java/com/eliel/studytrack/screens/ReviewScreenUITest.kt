package com.eliel.studytrack.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.eliel.studytrack.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReviewScreenUITest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testReviewScreenHeaderDisplayed() {
        composeTestRule.onNodeWithText("Revisão").assertIsDisplayed()
        composeTestRule.onNodeWithText("Matéria • Tema").assertIsDisplayed()
    }

    @Test
    fun testLoadingStateDisplayed() {
        composeTestRule.onNodeWithTag("LoadingIndicator").assertIsDisplayed()
    }

    @Test
    fun testErrorStateDisplayed() {
        composeTestRule.onNodeWithText("Erro ao carregar flashcards").assertIsDisplayed()
    }

    @Test
    fun testFlashcardsDisplayed() {
        composeTestRule.onNodeWithTag("Flashcard").assertExists()
        composeTestRule.onNodeWithText("PERGUNTA").assertIsDisplayed()
        composeTestRule.onNodeWithText("Toque para ver a resposta").assertIsDisplayed()
        composeTestRule.onNodeWithText("RESPOSTA").assertDoesNotExist()
        composeTestRule.onNodeWithTag("Flashcard").performClick()
        composeTestRule.onNodeWithText("RESPOSTA").assertIsDisplayed()
    }

    @Test
    fun testCloseButtonNavigatesBack() {
        composeTestRule.onNodeWithText("Fechar").performClick()
        composeTestRule.onNodeWithText("Home Screen").assertIsDisplayed()
    }
}