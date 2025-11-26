package com.eliel.studytrack.data

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DataSourceTest {

    @Test
    fun testGetTasksForSubject_AllSubjects() {
        val tasks = DataSource.getTasksForSubject("Todas as matérias")
        assertEquals(DataSource.tasks.size, tasks.size)
    }

    @Test
    fun testGetTasksForSubject_SpecificSubject() {
        val subjectName = "Matemática"
        val tasks = DataSource.getTasksForSubject(subjectName)
        assertTrue(tasks.all { it.subject == subjectName })
    }

    @Test
    fun testGetSubjectNames() {
        val subjectNames = DataSource.getSubjectNames()
        assertTrue(subjectNames.contains("Todas as matérias"))
        DataSource.subjects.forEach { subject ->
            assertTrue(subjectNames.contains(subject.name))
        }
    }

    @Test
    fun testDailyStudyTimeDefault() {
        assertEquals("0h0m", DataSource.dailyStudyTime.value)
    }

    @Test
    fun testPomodoroTimeDefault() {
        assertEquals(25, DataSource.pomodoroTime.value)
    }

    @Test
    fun testShortBreakTimeDefault() {
        assertEquals(5, DataSource.shortBreakTime.value)
    }

    @Test
    fun testLongBreakTimeDefault() {
        assertEquals(15, DataSource.longBreakTime.value)
    }

    @Test
    fun testDailyStudyGoalSessionsDefault() {
        assertEquals(4, DataSource.dailyStudyGoalSessions.value)
    }

    @Test
    fun testStudyRemindersEnabledDefault() {
        assertTrue(DataSource.studyRemindersEnabled.value)
    }

    @Test
    fun testTaskDeadlinesEnabledDefault() {
        assertTrue(DataSource.taskDeadlinesEnabled.value)
    }

    @Test
    fun testAppThemeDefault() {
        assertEquals("Claro", DataSource.appTheme.value)
    }

    @Test
    fun testAchievementsUnlocked() {
        val unlockedAchievements = DataSource.achievements.filter { it.isUnlocked }
        assertTrue(unlockedAchievements.isNotEmpty())
    }

    @Test
    fun testTasksOverdue() {
        val overdueTasks = DataSource.tasks.filter { it.isOverdue }
        assertTrue(overdueTasks.isNotEmpty())
    }

    @Test
    fun testSubjectsCompletionPercentage() {
        DataSource.subjects.forEach { subject ->
            assertTrue(subject.completionPercentage in 0..100)
        }
    }
}

