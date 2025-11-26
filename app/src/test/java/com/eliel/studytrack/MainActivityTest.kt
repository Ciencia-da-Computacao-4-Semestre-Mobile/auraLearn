package com.eliel.studytrack

import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.assertExists
import androidx.navigation.compose.rememberNavController
import com.eliel.studytrack.ui.theme.StudyTrackTheme
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun `test theme toggling`() {
        composeTestRule.setContent {
            var isDarkTheme by remember { mutableStateOf(false) }

            ThemeController.toggleTheme = { isDarkTheme = !isDarkTheme }
            ThemeController.isDarkMode = { isDarkTheme }

            StudyTrackTheme(darkTheme = isDarkTheme) {
                // Content for testing
            }
        }

        // Initially, the theme should be light
        assert(!ThemeController.isDarkMode?.invoke()!!)

        // Toggle the theme
        ThemeController.toggleTheme?.invoke()

        // Now, the theme should be dark
        assert(ThemeController.isDarkMode?.invoke()!!)
    }

    @Test
    fun `test bottom bar visibility on specific routes`() {
        composeTestRule.setContent {
            val navController = rememberNavController()

            Scaffold(
                bottomBar = { BottomBarConditional(navController) }
            ) {
                // Content for testing
            }
        }

        // Simulate navigation to a route with a bottom bar
        composeTestRule.runOnUiThread {
            composeTestRule.activity.runOnUiThread {
                navController.navigate(Screen.Home.route)
            }
        }

        composeTestRule.onNodeWithTag("BottomNavigationBar").assertExists()
    }
}