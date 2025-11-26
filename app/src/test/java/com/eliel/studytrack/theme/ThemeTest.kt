package com.eliel.studytrack.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertBackgroundColorIsEqualTo
import org.junit.Rule
import org.junit.Test

class ThemeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `StudyTrackTheme applies light color scheme`() {
        composeTestRule.setContent {
            StudyTrackTheme(darkTheme = false) {
                TestBox(tag = "LightThemeBox")
            }
        }

        composeTestRule.onNodeWithTag("LightThemeBox")
            .assertExists()
            .assertBackgroundColorIsEqualTo(StudyTrackBackground)
    }

    @Test
    fun `StudyTrackTheme applies dark color scheme`() {
        composeTestRule.setContent {
            StudyTrackTheme(darkTheme = true) {
                TestBox(tag = "DarkThemeBox")
            }
        }

        composeTestRule.onNodeWithTag("DarkThemeBox")
            .assertExists()
            .assertBackgroundColorIsEqualTo(Color(0xFF1C1B1F)) // Dark theme background color
    }

    @Composable
    private fun TestBox(tag: String) {
        Box(
            modifier = Modifier
                .testTag(tag)
                .background(MaterialTheme.colorScheme.background)
        )
    }
}