package com.eliel.studytrack.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TaskDeadlineReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("task_title") ?: "Tarefa"
        val subject = intent.getStringExtra("task_subject") ?: ""
        val whenText = intent.getStringExtra("when_label") ?: ""
        val message = if (subject.isNotBlank()) "$whenText: $title (${subject})" else "$whenText: $title"
        NotificationHelper.notify(context, intent.getIntExtra("notify_id", 2000), "Prazo de Tarefa", message)
    }
}