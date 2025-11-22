package com.eliel.studytrack.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class StudyPlanReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val titles = intent.getStringArrayListExtra("plan_titles")
        val message = if (!titles.isNullOrEmpty()) {
            "Continue seu cronograma: ${titles.joinToString(", ")}."
        } else {
            "Você tem planos de estudo para continuar hoje."
        }
        NotificationHelper.notify(context, 1001, "Plano de Estudos", message)
    }
}