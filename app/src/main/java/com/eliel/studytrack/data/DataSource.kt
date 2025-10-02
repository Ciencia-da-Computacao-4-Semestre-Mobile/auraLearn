
package com.eliel.studytrack.data

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

object DataSource {
    val tasks = mutableListOf(
        Task(
            id = "1",
            title = "Redação sobre meio ambiente",
            description = "Argumentação e dissertação",
            subject = "Português",
            dueDate = "25/12",
            estimatedTime = "2h",
            priority = Priority.ALTA,
            isOverdue = true
        ),
        Task(
            id = "2",
            title = "Resolver exercícios de álgebra",
            description = "Capítulo 5 - Equações quadráticas",
            subject = "Matemática",
            dueDate = "24/12",
            estimatedTime = "2h",
            priority = Priority.ALTA,
            isOverdue = true
        ),
        Task(
            id = "3",
            title = "Ler capítulo sobre Segunda Guerra",
            description = "História Geral - páginas 180-220",
            subject = "História",
            dueDate = "24/12",
            estimatedTime = "1.5h",
            priority = Priority.MEDIA,
            isOverdue = true
        ),
        Task(
            id = "4",
            title = "Revisar conceitos de física",
            description = "Leis de Newton",
            subject = "Física",
            dueDate = "26/12",
            estimatedTime = "1h",
            priority = Priority.BAIXA,
            isCompleted = false
        )
    )

    val subjects = mutableListOf(
        Subject(
            id = "1",
            name = "História",
            weeklyGoalHours = 4,
            priority = Priority.MEDIA,
            color = Color(0xFF6200EE),
            currentWeeklyProgressHours = 0,
            completionPercentage = (Math.random() * 100).toInt(),
            generalProgressPercentage = (Math.random() * 100).toInt()
        ),
        Subject(
            id = "2",
            name = "Matemática",
            weeklyGoalHours = 8,
            priority = Priority.ALTA,
            color = Color(0xFF00C853),
            currentWeeklyProgressHours = 0,
            completionPercentage = (Math.random() * 100).toInt(),
            generalProgressPercentage = (Math.random() * 100).toInt()
        ),
        Subject(
            id = "3",
            name = "Português",
            weeklyGoalHours = 6,
            priority = Priority.MEDIA,
            color = Color(0xFFFFC107),
            currentWeeklyProgressHours = 0,
            completionPercentage = (Math.random() * 100).toInt(),
            generalProgressPercentage = (Math.random() * 100).toInt()
        )
    )

    val achievements = mutableListOf(
        Achievement(
            id = "1",
            title = "Primeira Hora",
            description = "Complete sua primeira hora de estudo",
            isUnlocked = true,
            points = 50
        ),
        Achievement(
            id = "2",
            title = "Mestre das Tarefas",
            description = "Complete 10 tarefas",
            isUnlocked = false,
            points = 75
        ),
        Achievement(
            id = "3",
            title = "Sequência de Estudos",
            description = "Estude por 7 dias consecutivos",
            isUnlocked = false,
            points = 100
        ),
        Achievement(
            id = "4",
            title = "Campeão do Foco",
            description = "Complete 25 sessões Pomodoro",
            isUnlocked = false,
            points = 150
        ),
        Achievement(
            id = "5",
            title = "Maratona de Estudos",
            description = "Acumule 50 horas de estudo",
            isUnlocked = false,
            points = 200
        ),
        Achievement(
            id = "6",
            title = "Madrugador",
            description = "Estude antes das 7h da manhã",
            isUnlocked = false,
            points = 120
        )
    )

    val dailyStudyTime = mutableStateOf("0h0m")
    val completedTasksToday = mutableStateOf(0)
    val totalTasksToday = mutableStateOf(0)
    val dailyGoal = mutableStateOf("2h")
    val totalStudyTime = mutableStateOf("1h10m")
    val totalCompletedTasks = mutableStateOf(0)
    val weeklyProgress = mutableStateOf("0h0m")
    val totalStudySessions = mutableStateOf(2)
    val dailyGoalAverage = mutableStateOf("35") // Changed to a simple string representing minutes
    val weeklyGoal = mutableStateOf("14h")
    val weeklyGoalProgress = mutableStateOf("0h0m esta semana")

    val pomodorosCompleted = mutableStateOf(0)
    val focusedTimeToday = mutableStateOf("0min")

    val pomodoroTime = mutableStateOf(25)
    val shortBreakTime = mutableStateOf(5)
    val longBreakTime = mutableStateOf(15)
    val dailyStudyGoalSessions = mutableStateOf(4) // Alterado para sessões

    val studyRemindersEnabled = mutableStateOf(true)
    val taskDeadlinesEnabled = mutableStateOf(true)
    val achievementsUnlockedEnabled = mutableStateOf(true)
    val dailySummaryEnabled = mutableStateOf(false)
    val appTheme = mutableStateOf("Claro")

    fun getTasksForSubject(subjectName: String): List<Task> {
        return if (subjectName == "Todas as matérias") {
            tasks
        } else {
            tasks.filter { it.subject == subjectName }
        }
    }

    fun getSubjectNames(): List<String> {
        return listOf("Todas as matérias") + subjects.map { it.name }
    }
}

