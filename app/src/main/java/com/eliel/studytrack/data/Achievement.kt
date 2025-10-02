package com.eliel.studytrack.data

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    var isUnlocked: Boolean = false,
    val points: Int = 0
)