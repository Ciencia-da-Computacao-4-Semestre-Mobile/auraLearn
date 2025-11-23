package com.eliel.studytrack.data.firestore

import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import kotlinx.coroutines.tasks.await

data class PomodoroSessionData(
    val id: String = "",
    val subject: String = "",
    val minutes: Int = 0,
    val completedAt: Timestamp = Timestamp.now()
)

object PomodoroRepository {
    @SuppressLint("StaticFieldLeak")
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun sessions() =
        db.collection("users").document(auth.currentUser!!.uid).collection("pomodoro_sessions")

    suspend fun addSession(session: PomodoroSessionData) {
        val doc = sessions().document()
        val toSave = session.copy(id = doc.id)
        doc.set(toSave).await()
    }

    suspend fun getSessions(): List<PomodoroSessionData> {
        val snapshot = sessions().get().await()
        return snapshot.toObjects(PomodoroSessionData::class.java)
    }
}