package com.eliel.studytrack.data

enum class Priority {
    ALTA,
    MEDIA,
    BAIXA
}

data class Task(
    val id: String,
    val title: String,
    val description: String? = null,
    val subject: String,
    val dueDate: String,
    val estimatedTime: String,
    val priority: Priority,
    var isCompleted: Boolean = false,
    var inProgress: Boolean = false,
    val isOverdue: Boolean = false
)
