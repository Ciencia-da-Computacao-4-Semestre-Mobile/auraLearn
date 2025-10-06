package com.eliel.studytrack.data.firestore


import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestore.getInstance
import kotlinx.coroutines.tasks.await

data class SubjectData(
    val id: String = "",
    val name: String = "",
    val color: Long = 0xFF6200EE,
    val weeklyGoalHours: Int = 3,
    val currentWeeklyProgressHours: Int = 0
)

object SubjectRepository {

    @SuppressLint("StaticFieldLeak")
    private val db = getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun userSubjects() =
        db.collection("users").document(auth.currentUser!!.uid).collection("subjects")

    suspend fun addSubject(subject: SubjectData) {
        userSubjects().document(subject.id).set(subject).await()
    }

    suspend fun getSubjects(): List<SubjectData> {
        val snapshot = userSubjects().get().await()
        return snapshot.toObjects(SubjectData::class.java)
    }

    suspend fun deleteSubject(id: String) {
        userSubjects().document(id).delete().await()
    }
}
