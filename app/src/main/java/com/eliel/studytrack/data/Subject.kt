package com.eliel.studytrack.data
import androidx.compose.ui.graphics.Color
data class Subject(
    val id: String,
    val name: String,
    val weeklyGoalHours: Int,
    val priority: Priority,
    val color: Color,
    var currentWeeklyProgressHours: Int = 0,
    val completionPercentage: Int = 0,
    val generalProgressPercentage: Int = 0
)
