package com.eliel.studytrack.data.firestore

import com.google.firebase.Timestamp

data class StudyDay(
    var dayIndex: Int = 0,
    var text: String = "",
    var completed: Boolean = false
)

data class StudyPlan(
    var id: String = "",
    var userId: String = "",
    var title: String = "",
    var materia: String = "",
    var tema: String = "",
    var objetivo: String = "",
    var days: List<StudyDay> = emptyList(),
    var totalDays: Int = 0,
    var horasPorDia: Int = 0,
    var createdAt: Timestamp? = null
)
