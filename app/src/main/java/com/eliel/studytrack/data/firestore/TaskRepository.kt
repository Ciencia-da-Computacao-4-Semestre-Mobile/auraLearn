package com.eliel.studytrack.data.firestore

import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import kotlinx.coroutines.tasks.await

data class TaskData(
    val id: String = "",
    val title: String = "",
    val description: String? = null,
    val subject: String = "",
    val dueDate: String = "",
    val estimatedTime: String = "",
    val priority: String = "MEDIA",
    val completed: Boolean = false,
    val completedAt: Timestamp? = null
)

object TaskRepository {

    @SuppressLint("StaticFieldLeak")
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun userTasks() =
        db.collection("users").document(auth.currentUser!!.uid).collection("tasks")

    suspend fun addTask(task: TaskData) {
        userTasks().document(task.id).set(task).await()
    }

    suspend fun getTasks(): List<TaskData> {
        val snapshot = userTasks().get().await()
        return snapshot.toObjects(TaskData::class.java)
    }

    suspend fun updateTaskStatus(id: String, completed: Boolean) {
        val updates = hashMapOf<String, Any?>(
            "completed" to completed,
            "completedAt" to if (completed) Timestamp.now() else null
        )
        userTasks().document(id).update(updates).await()
    }

    suspend fun deleteTask(id: String) {
        userTasks().document(id).delete().await()
    }
}
