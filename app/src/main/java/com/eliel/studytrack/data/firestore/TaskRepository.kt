package com.eliel.studytrack.data.firestore

import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class TaskData(
    val id: String = "",
    val title: String = "",
    val description: String? = null,
    val subject: String = "",
    val dueDate: String = "",
    val estimatedTime: String = "",
    val priority: String = "MEDIA",
    val completed: Boolean = false
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
        userTasks().document(id).update("completed", completed).await()
    }

    suspend fun deleteTask(id: String) {
        userTasks().document(id).delete().await()
    }
}
