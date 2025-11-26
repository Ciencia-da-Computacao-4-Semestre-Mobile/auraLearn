package com.eliel.studytrack

import org.junit.Assert.assertEquals
import org.junit.Test

class ScreenTest {

    @Test
    fun `test screen routes`() {
        assertEquals("home", Screen.Home.route)
        assertEquals("schedule", Screen.Schedule.route)
        assertEquals("pomodoro", Screen.Pomodoro.route)
        assertEquals("reports", Screen.Reports.route)
        assertEquals("settings", Screen.Settings.route)
        assertEquals("chat_tutor", Screen.ChatTutor.route)
        assertEquals("premium", Screen.Premium.route)
        assertEquals("review", Screen.Review.route)
    }
}