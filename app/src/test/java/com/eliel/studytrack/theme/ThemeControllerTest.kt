package com.eliel.studytrack.ui.theme

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ThemeControllerTest {

    @Before
    fun setUp() {
        ThemeController.toggleTheme = null
        ThemeController.isDarkMode = null
    }

    @Test
    fun `toggleTheme invokes the provided lambda`() {
        var themeToggled = false
        ThemeController.toggleTheme = { themeToggled = true }

        ThemeController.toggleTheme?.invoke()

        assertTrue(themeToggled)
    }

    @Test
    fun `isDarkMode returns the correct value`() {
        ThemeController.isDarkMode = { true }
        assertTrue(ThemeController.isDarkMode?.invoke() == true)

        ThemeController.isDarkMode = { false }
        assertFalse(ThemeController.isDarkMode?.invoke() == true)
    }
}